package de.uniks.pmws2021.chat.network.server.controller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.model.Chat;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.network.server.websocket.ChatSocket;
import de.uniks.pmws2021.chat.util.JsonUtil;
import de.uniks.pmws2021.chat.util.ResourceManager;
import de.uniks.pmws2021.chat.util.ServerResponse;
import de.uniks.pmws2021.chat.util.ValidationUtil;
import spark.Request;
import spark.Response;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pmws2021.chat.Constants.COM_NAME;
import static de.uniks.pmws2021.chat.util.ServerResponse.FAILURE;
import static de.uniks.pmws2021.chat.util.ServerResponse.SUCCESS;

public class UserController {
    private Chat model;
    private ChatEditor editor;
    private ChatSocket chatSocket;


    public UserController(Chat model, ChatEditor editor, ChatSocket chatSocket) {
        this.model = model;
        this.editor = editor;
        this.chatSocket = chatSocket;
    }

    public String getAllLoggedInUsers(Request req, Response res) {
        /* get all users with status online*/
        List<User> users = model.getAvailableUser();
        List<User> onlineUser = new ArrayList<>();
        for (User user : users) {
            if (user.getStatus()) {
                onlineUser.add(user);
            }
        }

        JsonArray bodyResult = JsonUtil.usersToJson(onlineUser);
        res.status(200);
        return JsonUtil.stringify(new ServerResponse(SUCCESS, bodyResult));
    }

    public String login(Request req, Response res) {
        // Check for the body
        String body = req.body();
        ServerResponse err = ValidationUtil.validateBody(body);

        // Return on error
        if (err != null) {
            res.status(400);
            return JsonUtil.stringify(err);
        }

        // parse request body
        JsonObject bodyJson = JsonUtil.parse(body);

        // get name from body
        String name = bodyJson.getString(COM_NAME);

        // check if user already logged in, if yes, return with error message
        for (User user : model.getAvailableUser()) {
            if (user.getName().equals(name) && user.getStatus()) {
                res.status(400);
                return JsonUtil.stringify(new ServerResponse(FAILURE, "Already logged in"));
            }
        }

        // set user online and save ip
        User currentUser = editor.haveUser(name, req.ip());
        chatSocket.sendUserJoined(currentUser);

        // Save currentUser
        ResourceManager.saveServerUsers(currentUser);

        // send response that everything went fine
        res.status(200);
        return JsonUtil.stringify(new ServerResponse(SUCCESS, JsonUtil.buildOkLogin()));
    }

    public String logout(Request req, Response res) {
        // Check for the body
        String body = req.body();
        ServerResponse err = ValidationUtil.validateBody(body);

        // Return on error
        if (err != null) {
            res.status(400);
            return JsonUtil.stringify(err);
        }

        JsonObject bodyJson = JsonUtil.parse(body);

        // get user by name
        String name = bodyJson.getString(COM_NAME);

        // check if user already logged out, if yes, return with error message
        User currentUser = null;
        for (User user : model.getAvailableUser()) {
            if (user.getName().equals(name)) {
                currentUser = user;
                break;
            }
        }
        if (currentUser == null || !currentUser.getStatus()) {
            res.status(400);
            return JsonUtil.stringify(new ServerResponse(FAILURE, "Already logged out"));
        }

        // end websocket connection of user
        // set user offline
        // send logout websocket message
        chatSocket.killConnection(currentUser, "User logged out");

        // send response that everything went fine
        res.status(200);
        return JsonUtil.stringify(new ServerResponse(SUCCESS, JsonUtil.buildOkLogout()));
    }
}
