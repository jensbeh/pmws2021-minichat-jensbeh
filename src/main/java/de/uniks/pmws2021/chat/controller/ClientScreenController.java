package de.uniks.pmws2021.chat.controller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.StageManager;
import de.uniks.pmws2021.chat.model.Message;
import de.uniks.pmws2021.chat.model.OpenChatUserInfo;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.network.client.RestClient;
import de.uniks.pmws2021.chat.network.client.WSCallback;
import de.uniks.pmws2021.chat.network.client.WebSocketClient;
import de.uniks.pmws2021.chat.util.JsonUtil;
import de.uniks.pmws2021.chat.util.ResourceManager;
import de.uniks.pmws2021.chat.view.ChatListCellFactory;
import de.uniks.pmws2021.chat.view.OnlineUserListCellFactory;
import de.uniks.pmws2021.chat.view.OpenChatUserInfoListCellFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.uniks.pmws2021.chat.Constants.*;

public class ClientScreenController {
    // Variables
    private final Parent view;
    private static String currentUserName;
    private ChatEditor chatEditor;
    private WebSocketClient USER_CLIENT;

    private List<User> onlineUsers;
    private static Map<String, ArrayList<Message>> chats;
    private static List<OpenChatUserInfo> openChatUserInfo;
    private static OpenChatUserInfo currentOpenChatUserInfo;

    private Button buttonSend;
    private Button buttonLeave;
    private Label labelCurrentUser;
    private static Label labelOtherUser;
    private TextField textFieldInputMessage;
    private ListView<User> listViewOnlineUser;
    private static Button buttonDelete;
    private static Button buttonClear;
    private static ListView<OpenChatUserInfo> listViewOpenChatUserInfo;
    private static ListView<Message> listViewChat;
    private static ChatListCellFactory chatListCellFactory;

    public ClientScreenController(Parent view, ChatEditor chatEditor) {
        this.view = view;
        this.chatEditor = chatEditor;
        this.currentUserName = chatEditor.getChat().getCurrentUsername();
    }

    public void init() {
        // Load all view references
        buttonSend = (Button) view.lookup("#button_send");
        buttonLeave = (Button) view.lookup("#button_leave");
        buttonDelete = (Button) view.lookup("#button_delete");
        buttonClear = (Button) view.lookup("#button_clear");
        labelCurrentUser = (Label) view.lookup("#label_current_user");
        labelOtherUser = (Label) view.lookup("#label_other_user");
        textFieldInputMessage = (TextField) view.lookup("#tf_message");
        listViewOnlineUser = (ListView<User>) view.lookup("#listview_online_users");
        listViewOnlineUser.setCellFactory(new OnlineUserListCellFactory());
        listViewOpenChatUserInfo = (ListView<OpenChatUserInfo>) view.lookup("#listview_open_user_chats");
        listViewOpenChatUserInfo.setCellFactory(new OpenChatUserInfoListCellFactory());
        listViewChat = (ListView<Message>) view.lookup("#listview_chat");
        chatListCellFactory = new ChatListCellFactory(currentUserName);
        listViewChat.setCellFactory(chatListCellFactory);

        // Add action listeners
        buttonSend.setOnAction(this::buttonSendOnClick);
        buttonLeave.setOnAction(this::buttonLeaveOnClick);
        buttonDelete.setOnAction(this::buttonDeleteOnClick);
        buttonClear.setOnAction(this::buttonClearOnClick);
        listViewOnlineUser.setOnMouseReleased(this::onOnlineUserListClick);
        listViewOpenChatUserInfo.setOnMouseReleased(this::onOpenChatUserInfoListClick);

        textFieldInputMessage.setOnKeyReleased(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                buttonSend.fire();
            }
        });

        labelCurrentUser.setText("Logged in as: " + currentUserName);
        labelOtherUser.setText("");

        buttonDelete.setVisible(false);
        buttonClear.setVisible(false);

        openChatUserInfo = new ArrayList<>();
        openChatUserInfo.add(new OpenChatUserInfo(COM_CHANNEL_ALL));
        currentOpenChatUserInfo = openChatUserInfo.get(0);
        labelOtherUser.setText(currentOpenChatUserInfo.getUserName());

        updateOpenUserList();


        chats = new HashMap<>();
        chats.put(COM_CHANNEL_ALL, new ArrayList<Message>());

        updateChatList(COM_CHANNEL_ALL, chats.get(COM_CHANNEL_ALL));


        // login and handle messages
        RestClient.login(currentUserName, response -> {
            try {
                USER_CLIENT = new WebSocketClient(chatEditor, new URI(CHAT_SOCKET_URL), new WSCallback() {
                    String lastUserJoinedLeft = "";

                    @Override
                    public void handleMessage(JsonStructure msg) {
                        //System.out.println("msg: " + msg);
                        JsonObject jsonMsg = JsonUtil.parse(msg.toString());
                        String currentChannel = jsonMsg.getString(COM_CHANNEL);

                        if (currentChannel.equals(COM_CHANNEL_SYSTEM)) {
                            String currentAction = jsonMsg.get(COM_DATA).asJsonObject().getString(COM_ACTION);

                            // USER JOINED/LEFT
                            if (currentAction.equals(COM_USER_JOINED) || currentAction.equals(COM_USER_LEFT)) {
                                String userName = jsonMsg.get(COM_DATA).asJsonObject().get(COM_USER).asJsonObject().getString(COM_NAME);
                                if (currentAction.equals(COM_USER_JOINED)) {
                                    String userJoinedLeft = "User " + userName + " joined";
                                    if (!lastUserJoinedLeft.equals(userJoinedLeft)) {
                                        chats.get(COM_CHANNEL_ALL).add(new Message(COM_CHANNEL_ALL, COM_CHANNEL_ALL, userJoinedLeft));
                                        Message newMessage = new Message(COM_CHANNEL_ALL, COM_CHANNEL_ALL, "Welcome " + userName + "!");
                                        chats.get(COM_CHANNEL_ALL).add(newMessage);
                                        for (OpenChatUserInfo info : openChatUserInfo) {
                                            if (info.getUserName().equals(COM_CHANNEL_ALL)) {
                                                info.setLastMessage(newMessage);
                                                updateOpenUserList();
                                            }
                                        }
                                        lastUserJoinedLeft = userJoinedLeft;
                                    }
                                } else {
                                    String userJoinedLeft = "User " + userName + " left";
                                    Message newMessage = new Message(COM_CHANNEL_ALL, COM_CHANNEL_ALL, userJoinedLeft);
                                    chats.get(COM_CHANNEL_ALL).add(newMessage);
                                    for (OpenChatUserInfo info : openChatUserInfo) {
                                        if (info.getUserName().equals(COM_CHANNEL_ALL)) {
                                            info.setLastMessage(newMessage);
                                            updateOpenUserList();
                                        }
                                    }
                                }

                                if (currentOpenChatUserInfo.getUserName().equals(COM_CHANNEL_ALL)) {
                                    updateChatList(COM_CHANNEL_ALL, chats.get(COM_CHANNEL_ALL));
                                }


                                RestClient.getAllAvailableUser(response1 -> {
                                    JsonObject jsonObject = JsonUtil.parse(response1.getBody().toString());
                                    JsonArray jsonArray = jsonObject.getJsonArray(COM_DATA);

                                    onlineUsers = JsonUtil.parseUsers(jsonArray);
                                    updateUserListView();
                                });
                            }
                            // ALL MESSAGE
                        } else if (currentChannel.equals(COM_CHANNEL_ALL)) {
                            String fromUserName = jsonMsg.getString(COM_FROM);

                            if (!fromUserName.equals(currentUserName)) {
                                String receivedMessage = jsonMsg.getString(COM_MSG);
                                Message newMessage = new Message(fromUserName, COM_CHANNEL_ALL, receivedMessage);
                                chats.get(COM_CHANNEL_ALL).add(newMessage);
                                for (OpenChatUserInfo info : openChatUserInfo) {
                                    if (info.getUserName().equals(COM_CHANNEL_ALL)) {
                                        info.setLastMessage(newMessage);
                                        updateOpenUserList();
                                    }
                                }

                                if (currentOpenChatUserInfo.getUserName().equals(COM_CHANNEL_ALL)) {
                                    updateChatList(COM_CHANNEL_ALL, chats.get(COM_CHANNEL_ALL));
                                }

                                // new message visual -> update UI
                                if (!currentOpenChatUserInfo.getUserName().equals(COM_CHANNEL_ALL)) {
                                    for (OpenChatUserInfo info : openChatUserInfo) {
                                        if (info.getUserName().equals(COM_CHANNEL_ALL)) {
                                            info.setUnreadMessagesCounter(info.getUnreadMessagesCounter() + 1);
                                            updateOpenUserList();
                                        }
                                    }
                                }
                            }

                            // PRIVATE MESSAGE
                        } else if (currentChannel.equals(COM_CHANNEL_PRIVATE)) {
                            String fromUserName = jsonMsg.getString(COM_FROM);
                            String receivedMessage = jsonMsg.getString(COM_MSG);


                            // if chat/tab not open -> create and new chat/tab
                            boolean found = false;
                            for (OpenChatUserInfo info : openChatUserInfo) {
                                if (info.getUserName().equals(fromUserName)) {
                                    found = true;
                                    break;
                                } else {
                                    found = false;
                                }
                            }
                            if (!found) {
                                openChatUserInfo.add(new OpenChatUserInfo(fromUserName));
                                updateOpenUserList();
                            }
                            if (!chats.containsKey(fromUserName)) {
                                ArrayList<Message> loadedChat = ResourceManager.loadChat(currentUserName, fromUserName);
                                chats.put(fromUserName, loadedChat);
                            }

                            // put message to chat
                            int id;
                            if (chats.get(fromUserName).size() == 0) {
                                id = 1;
                            } else {
                                id = chats.get(fromUserName).get(chats.get(fromUserName).size() - 1).getMessageId() + 1;
                            }
                            Message newMessage = new Message(fromUserName, currentUserName, receivedMessage, id);
                            chats.get(fromUserName).add(newMessage);
                            for (OpenChatUserInfo info : openChatUserInfo) {
                                if (info.getUserName().equals(fromUserName)) {
                                    info.setLastMessage(newMessage);
                                    updateOpenUserList();
                                }
                            }

                            // update chat view
                            if (currentOpenChatUserInfo.getUserName().equals(fromUserName)) {
                                updateChatList(fromUserName, chats.get(fromUserName));
                            }
                            ResourceManager.saveChat(currentUserName, fromUserName, chats.get(fromUserName));

                            // new message visual -> update UI
                            if (!currentOpenChatUserInfo.getUserName().equals(fromUserName)) {
                                for (OpenChatUserInfo info : openChatUserInfo) {
                                    if (info.getUserName().equals(fromUserName)) {
                                        info.setUnreadMessagesCounter(info.getUnreadMessagesCounter() + 1);
                                        updateOpenUserList();
                                    }
                                }
                            }

                        }
                    }
                });
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    public static void closeChat(OpenChatUserInfo info) {
        // if chat was closed
        Platform.runLater(() -> {
            if (currentOpenChatUserInfo.getUserName().equals(info.getUserName())) {
                for (OpenChatUserInfo info1 : openChatUserInfo) {
                    if (info1.getUserName().equals(COM_CHANNEL_ALL)) {
                        currentOpenChatUserInfo = info1;
                    }
                }
            }
            labelOtherUser.setText(currentOpenChatUserInfo.getUserName());
            openChatUserInfo.remove(info);

            if (currentOpenChatUserInfo.getUserName().equals(COM_CHANNEL_ALL)) {
                buttonDelete.setVisible(false);
                buttonClear.setVisible(false);
            } else {
                buttonDelete.setVisible(true);
                buttonClear.setVisible(true);
            }

            updateOpenUserList();
            updateChatList(currentOpenChatUserInfo.getUserName(), chats.get(currentOpenChatUserInfo.getUserName()));
        });
    }

    private static void updateOpenUserList() {
        Platform.runLater(() -> {
            listViewOpenChatUserInfo.setCellFactory(new OpenChatUserInfoListCellFactory());
            listViewOpenChatUserInfo.setItems(FXCollections.observableList(openChatUserInfo));
        });
    }

    private void onOpenChatUserInfoListClick(MouseEvent event) {
        // get current OpenChatUserInfo
        OpenChatUserInfo oldInfo = currentOpenChatUserInfo;
        currentOpenChatUserInfo = listViewOpenChatUserInfo.getSelectionModel().getSelectedItem();
        if (currentOpenChatUserInfo != null && !oldInfo.getUserName().equals(currentOpenChatUserInfo.getUserName())) {

            oldInfo.setSelected(false);
            currentOpenChatUserInfo.setSelected(true);
            labelOtherUser.setText(currentOpenChatUserInfo.getUserName());

            updateChatList(currentOpenChatUserInfo.getUserName(), chats.get(currentOpenChatUserInfo.getUserName()));

            if (currentOpenChatUserInfo.getUnreadMessagesCounter() > 0) {
                currentOpenChatUserInfo.setUnreadMessagesCounter(0);
                updateOpenUserList();
            }

            if (currentOpenChatUserInfo.getUserName().equals(COM_CHANNEL_ALL)) {
                buttonDelete.setVisible(false);
                buttonClear.setVisible(false);
            } else {
                buttonDelete.setVisible(true);
                buttonClear.setVisible(true);
            }
        }
    }

    private void buttonDeleteOnClick(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            Message selectedMessage = listViewChat.getSelectionModel().getSelectedItem();
            if (selectedMessage != null) {
                ArrayList<Message> chat = chats.get(currentOpenChatUserInfo.getUserName());

                chat.remove(selectedMessage);

                // put messages on ListView
                updateChatList(currentOpenChatUserInfo.getUserName(), chat);
                ResourceManager.saveChat(currentUserName, currentOpenChatUserInfo.getUserName(), chat);
            }
        });
    }

    private void buttonClearOnClick(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            if (!chats.get(currentOpenChatUserInfo.getUserName()).isEmpty()) {

                chats.get(currentOpenChatUserInfo.getUserName()).clear();

                ArrayList<Message> chat = new ArrayList<>();
                updateChatList(currentOpenChatUserInfo.getUserName(), chat);
                ResourceManager.saveChat(currentUserName, currentOpenChatUserInfo.getUserName(), chat);
            }
        });
    }

    private void onOnlineUserListClick(MouseEvent event) {

        User selectedUser = this.listViewOnlineUser.getSelectionModel().getSelectedItem();


        if (event.getClickCount() == 2) {
            if (selectedUser != null && !selectedUser.getName().equals(currentUserName)) {
                boolean found = false;
                for (OpenChatUserInfo info : openChatUserInfo) {
                    if (info.getUserName().equals(selectedUser.getName())) {
                        found = true;
                        break;
                    } else {
                        found = false;
                    }
                }

                if (!found) {
                    OpenChatUserInfo newChatUserInfo = new OpenChatUserInfo(selectedUser.getName());
                    newChatUserInfo.setSelected(true);
                    openChatUserInfo.add(newChatUserInfo);


                    currentOpenChatUserInfo = newChatUserInfo;
                    labelOtherUser.setText(currentOpenChatUserInfo.getUserName());


                    if (!chats.containsKey(newChatUserInfo.getUserName())) {
                        chats.put(newChatUserInfo.getUserName(), new ArrayList<>());
                    }

                    ArrayList<Message> loadedChat = ResourceManager.loadChat(currentUserName, selectedUser.getName());
                    chats.put(selectedUser.getName(), loadedChat);

                    // get last message
                    if (loadedChat.size() > 0) {
                        Message newMessage = loadedChat.get(loadedChat.size() - 1);
                        for (OpenChatUserInfo info : openChatUserInfo) {
                            if (info.getUserName().equals(currentOpenChatUserInfo.getUserName())) {
                                info.setLastMessage(newMessage);
                            }
                        }
                    }

                    if (currentOpenChatUserInfo.getUserName().equals(COM_CHANNEL_ALL)) {
                        buttonDelete.setVisible(false);
                        buttonClear.setVisible(false);
                    } else {
                        buttonDelete.setVisible(true);
                        buttonClear.setVisible(true);
                    }

                    updateChatList(selectedUser.getName(), loadedChat);
                    updateOpenUserList();
                }
            }
        }
    }

    public void stop() {
        // Clear action listeners
        buttonSend.setOnAction(null);
        buttonLeave.setOnAction(null);
    }

    // ===========================================================================================
    // Button Action Methods
    // ===========================================================================================

    private void buttonSendOnClick(ActionEvent actionEvent) {
        String sendMessage = textFieldInputMessage.getText();
        textFieldInputMessage.clear();

        if (currentOpenChatUserInfo.getUserName().equals(COM_CHANNEL_ALL)) {

            try {

                USER_CLIENT.sendMessage(JsonUtil.buildPublicChatMessage(sendMessage).toString());

                Message newMessage = new Message(currentUserName, COM_CHANNEL_ALL, sendMessage);
                chats.get(COM_CHANNEL_ALL).add(newMessage);
                for (OpenChatUserInfo info : openChatUserInfo) {
                    if (info.getUserName().equals(COM_CHANNEL_ALL)) {
                        info.setLastMessage(newMessage);
                        updateOpenUserList();
                    }
                }
                updateChatList(COM_CHANNEL_ALL, chats.get(COM_CHANNEL_ALL));
                ResourceManager.saveChat(currentUserName, COM_CHANNEL_ALL, chats.get(COM_CHANNEL_ALL));
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            String toUserName = currentOpenChatUserInfo.getUserName();
            Platform.runLater(() -> {
                try {
                    USER_CLIENT.sendMessage(JsonUtil.buildPrivateChatMessage(sendMessage, toUserName).toString());

                    int id;
                    if (chats.get(toUserName).size() == 0) {
                        id = 1;
                    } else {
                        id = chats.get(toUserName).get(chats.get(toUserName).size() - 1).getMessageId() + 1;
                    }
                    Message newMessage = new Message(currentUserName, toUserName, sendMessage, id);
                    chats.get(toUserName).add(newMessage);
                    for (OpenChatUserInfo info : openChatUserInfo) {
                        if (info.getUserName().equals(toUserName)) {
                            info.setLastMessage(newMessage);
                            updateOpenUserList();
                        }
                    }
                    updateChatList(toUserName, chats.get(toUserName));
                    ResourceManager.saveChat(currentUserName, toUserName, chats.get(toUserName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void buttonLeaveOnClick(ActionEvent actionEvent) {
        RestClient.logout(currentUserName, response -> {
        });

        StageManager.showStartScreen();
    }

    // ===========================================================================================
    // Additional Methods
    // ===========================================================================================

    private void updateUserListView() {
        // put users on ListView
        Platform.runLater(() -> {
            this.listViewOnlineUser.setItems(FXCollections.observableList(onlineUsers));
        });
    }

    private static void updateChatList(String channelReceiver, List<Message> chatList) {
        Platform.runLater(() -> {
            //listViewChat.setCellFactory(new ChatListCellFactory(currentUserName));
            chatListCellFactory.setCurrentUser2("currentUserName");
            listViewChat.setItems(FXCollections.observableList(chatList));
            listViewChat.scrollTo(chatList.size());
        });
    }
}