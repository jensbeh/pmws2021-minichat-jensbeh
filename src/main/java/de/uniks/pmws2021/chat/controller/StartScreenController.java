package de.uniks.pmws2021.chat.controller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.StageManager;
import de.uniks.pmws2021.chat.model.User;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;

public class StartScreenController {
    // Variables
    private final Parent view;
    private ChatEditor chatEditor;

    private Button serverButton;
    private Button clientButton;

    public StartScreenController(Parent view, ChatEditor chatEditor) {
        this.view = view;
        this.chatEditor = chatEditor;
    }

    public void init() {
        // Load all view references
        serverButton = (Button) view.lookup("#button_server");
        clientButton = (Button) view.lookup("#button_client");

        // Add action listeners
        serverButton.setOnAction(this::buttonServerOnClick);
        clientButton.setOnAction(this::buttonClientOnClick);


        // Initialisation

    }

    public void stop() {
        // Clear references
        // Clear action listeners
        serverButton.setOnAction(null);
        clientButton.setOnAction(null);

    }

    // ===========================================================================================
    // Button Action Methods
    // ===========================================================================================

    private void buttonServerOnClick(ActionEvent actionEvent) {
        System.out.println("Show ServerScreen");
        StageManager.showServerScreen();
    }

    private void buttonClientOnClick(ActionEvent actionEvent) {
        System.out.println("Open TextInputDialog to put in username");

        // Define Dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Username");
        dialog.setHeaderText("Please put in username");
        dialog.setContentText("Username:");

        // Wait for submit
        dialog.showAndWait().ifPresent((name) -> {
            System.out.println("Username: " + name);

            chatEditor.haveChat().setCurrentUsername(name);
            User currentUser = chatEditor.haveUser(name, "192.168.0.1");
            System.out.println("Show Client Screen");

            StageManager.showClientScreen();
        });

    }

    // ===========================================================================================
    // Additional Methods
    // ===========================================================================================


}