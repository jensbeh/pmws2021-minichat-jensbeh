package de.uniks.pmws2021.chat.controller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.StageManager;
import de.uniks.pmws2021.chat.model.Chat;
import de.uniks.pmws2021.chat.model.Status;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.network.server.ChatServer;
import de.uniks.pmws2021.chat.util.ResourceManager;
import de.uniks.pmws2021.chat.view.OnlineUserListCellFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class ServerScreenController {
    private PropertyChangeListener userAddListener = this::onUserAdd;
    private PropertyChangeListener userStatusListener = this::onStatusChange;

    // Variables
    private final Parent view;
    private ChatEditor chatEditor;
    private ChatServer server;

    private Button dcAllButton;
    private Button dcOneButton;
    private Button closeServerButton;
    private ListView<User> userList;
    private User selectedUser;
    private List<User> users;

    public ServerScreenController(Parent view, ChatEditor chatEditor) {
        this.view = view;
        this.chatEditor = chatEditor;
    }

    public void init() {
        // Load all view references
        dcAllButton = (Button) view.lookup("#button_dcAll");
        dcOneButton = (Button) view.lookup("#button_dcOne");
        closeServerButton = (Button) view.lookup("#button_closeServer");
        userList = (ListView<User>) view.lookup("#listview_userList");
        userList.setCellFactory(new OnlineUserListCellFactory());


        // Add action listeners
        dcAllButton.setOnAction(this::buttonDcAllOnClick);
        dcOneButton.setOnAction(this::buttonDcOneOnClick);
        closeServerButton.setOnAction(this::buttonCloseServerOnClick);
        userList.setOnMouseReleased(this::onListClick);


        // Initialisation
        server = new ChatServer(chatEditor.haveChat(), chatEditor);
        users = ResourceManager.loadServerUsers();
        for (User user : users) {
            user.setStatus(Status.offline);
        }
        chatEditor.getChat().withAvailableUser(users);

        // put users on ListView
        List<User> users = chatEditor.getChat().getAvailableUser();
        this.userList.setItems(FXCollections.observableList(users));

        // Put PCL here
        userAddListener = this::onUserAdd;
        userStatusListener = this::onStatusChange;
        this.chatEditor.getChat().addPropertyChangeListener(Chat.PROPERTY_AVAILABLE_USER, this.userAddListener);
        for (User user : users) {
            user.addPropertyChangeListener(User.PROPERTY_STATUS, this.userStatusListener);
        }
    }

    public void stop() {
        // Clear action listeners
        dcAllButton.setOnAction(null);
        dcOneButton.setOnAction(null);
        closeServerButton.setOnAction(null);

        // stop listener
        this.chatEditor.getChat().removePropertyChangeListener(Chat.PROPERTY_AVAILABLE_USER, this.userAddListener);
        for (User user : chatEditor.getChat().getAvailableUser()) {
            user.removePropertyChangeListener(User.PROPERTY_STATUS, this.userStatusListener);
        }

        // stop server
        server.stopServer();
    }

    // ===========================================================================================
    // Button Action Methods
    // ===========================================================================================

    private void buttonDcAllOnClick(ActionEvent actionEvent) {
        System.out.println("Disconnected All Users");
        for (User user : chatEditor.getChat().getAvailableUser()) {
            if (user.getStatus()) {
                server.disconnectUser(user);
            }
        }
    }

    private void buttonDcOneOnClick(ActionEvent actionEvent) {
        if (selectedUser != null) {
            if (selectedUser.getStatus()) {
                server.disconnectUser(selectedUser);
            }
        }
    }

    private void buttonCloseServerOnClick(ActionEvent actionEvent) {
        System.out.println("Server closed");
        server.stopServer();
        StageManager.showStartScreen();
    }

    // ===========================================================================================
    // Additional Methods
    // ===========================================================================================

    public void onUserAdd(PropertyChangeEvent event) {
        for (User user : chatEditor.getChat().getAvailableUser()) {
            if (!users.contains(user)) {
                user.addPropertyChangeListener(User.PROPERTY_STATUS, this.userStatusListener);
                users.add(user);
                System.out.println("Add user: " + user);
            }
        }
        updateUserList();
    }

    private void onStatusChange(PropertyChangeEvent propertyChangeEvent) {
        updateUserList();
    }

    private void onListClick(MouseEvent event) {
        selectedUser = userList.getSelectionModel().getSelectedItem();
    }

    private void updateUserList() {
        // put users on ListView
        Platform.runLater(() -> {
            List<User> users = chatEditor.getChat().getAvailableUser();
            this.userList.setItems(FXCollections.observableList(users));
        });
    }
}
