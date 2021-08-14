package de.uniks.pmws2021.chat.network.server.websocket;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.Constants;
import de.uniks.pmws2021.chat.model.Status;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.util.JsonUtil;
import de.uniks.pmws2021.chat.util.ServerResponse;
import de.uniks.pmws2021.chat.util.ValidationUtil;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import javax.json.JsonObject;
import java.io.EOFException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import static de.uniks.pmws2021.chat.Constants.*;

@WebSocket
public class ChatSocket {
    private final ChatEditor editor;

    // save all connections to send messages correctly
    private final Map<String, Session> userSessionMap;
    private final Queue<Session> clients;

    public ChatSocket(ChatEditor editor) {
        this.editor = editor;

        this.userSessionMap = new LinkedHashMap<>();
        this.clients = new ConcurrentLinkedDeque<>();
    }

    @OnWebSocketConnect
    public void onNewConnection(Session session) throws IOException {
        System.out.println("##### New Connection #####");

        // get user name from session request header
        String name = session.getUpgradeRequest().getHeader(COM_NAME);

        if (name != null && !name.isEmpty()) {
            for (User user : editor.getChat().getAvailableUser()) {
                // check if user has logged in
                if (user.getName().equals(name) && user.getStatus()) {

                    // if yes, store user with his session and add session to clients
                    // also send a system message that the user has logged in
                    this.userSessionMap.put(name, session);
                    clients.add(session);
                    sendUserJoined(editor.haveUser(name, session.getLocalAddress().getHostName()));
                    System.out.println("ChatSocket: " + name + " has connected successfully");
                    return;
                }
            }
            session.getRemote().sendString(JsonUtil.stringify(new ServerResponse(ServerResponse.FAILURE, "Log in first")));
            session.getRemote().flush();
            session.close(401, "No login");

        } else {
            session.getRemote().sendString(JsonUtil.stringify(new ServerResponse(ServerResponse.FAILURE, "Missing name parameter header")));
            session.getRemote().flush();
            session.close(1000, "Incorrect connection");
        }
    }

    @OnWebSocketClose
    public void onConnectionClose(Session session, int statusCode, String reason) {
        // if user is logged in remove session and send system message to notify all about logout
        String name = session.getUpgradeRequest().getHeader(COM_NAME);
        for (User user : this.editor.getChat().getAvailableUser()) {
            if (name.equals(user.getName())) {
                if (user.getStatus()) {
                    killConnection(user, reason);
                }
            }
        }
        // just some logging
        System.out.println("Chat session closed, because of " + reason);

    }

    @OnWebSocketError
    public void onSocketError(Throwable e) {
        // Only print errors which belong to a real failure
        if (!(e instanceof EOFException)) {
            System.err.println("Error on chat socket:");
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            // If this was a noop, just do nothing
            if (message.equals(Constants.COM_NOOP)) {
                return;
            }

            // Validate the chat message
            ServerResponse err = ValidationUtil.validateChatMessage(message);
            if (err != null) {
                try {
                    // send message to given session
                    session.getRemote().sendString(JsonUtil.stringify(err));
                    session.getRemote().flush();
                    return;
                } catch (Exception e) {
                    System.err.println("Error while processing incoming message:");
                    e.printStackTrace();
                }
            }

            // get user name (from)
            String name = session.getUpgradeRequest().getHeader(COM_NAME);

            // parse string message to json
            JsonObject jsonMessage = JsonUtil.parse(message);

            // get channel and msg identifier
            String channel = jsonMessage.getString(COM_CHANNEL);
            String msg = jsonMessage.getString(COM_MSG);

            // check if session is open
            if (session.isOpen()) {
                // build answer message (channel, from, message)
                // Check if the message is public or private
                // send message to the receiver
                if (channel.equals(COM_CHANNEL_ALL)) {
                    for (Session everySession : userSessionMap.values()) {
                        // if message is public, send message to every client
                        // check if session is open before sending message
                        if (everySession.isOpen()) {
                            everySession.getRemote().sendString(JsonUtil.buildAnswer(channel, name, msg).toString());
                            everySession.getRemote().flush();
                        }
                    }
                } else if (channel.equals(COM_CHANNEL_PRIVATE)) {
                    // if message is private
                    // lookup session of receiving user
                    String toUser = jsonMessage.getString(COM_TO);
                    Session receiverSession = userSessionMap.get(toUser);
                    if (receiverSession.isOpen()) {
                        receiverSession.getRemote().sendString(JsonUtil.buildAnswer(channel, name, msg).toString());
                        receiverSession.getRemote().flush();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error while processing incoming message:");
            e.printStackTrace();
        }
    }

    public void sendUserJoined(User user) {
        // send system message with data
        System.out.println(user.getName() + " joined");
        sendSystemMessage(JsonUtil.buildUserJoinedSystemMessage(user).toString());
    }

    public void sendUserLeft(User user) {
        // send system message with data
        System.out.println(user.getName() + " left");
        sendSystemMessage(JsonUtil.buildUserLeftSystemMessage(user).toString());
    }

    public void killConnection(User user, String reason) {
        // get session of user, remove from lists and close it (if open)
        Session currentSession = userSessionMap.get(user.getName());

        user.setStatus(Status.offline);
        this.userSessionMap.remove(user.getName(), currentSession);
        sendUserLeft(user);

        if (currentSession != null) { // TODO delete if
            if (currentSession.isOpen()) {
                currentSession.close(200, "Killed connection");
            }
        }
    }

    private void sendSystemMessage(String message) {
        // send message to every client
        for (Session values : userSessionMap.values()) {
            try {
                values.getRemote().sendString(message);
                values.getRemote().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
