package de.uniks.pmws2021.chat;

import de.uniks.pmws2021.chat.controller.ClientScreenController;
import de.uniks.pmws2021.chat.controller.ServerScreenController;
import de.uniks.pmws2021.chat.controller.StartScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StageManager extends Application {
    //Variables
    private static Stage stage;
    private static ChatEditor chatEditor;

    // Controller
    private static StartScreenController startCtrl;
    private static ServerScreenController serverCtrl;
    private static ClientScreenController clientCtrl;

    @Override
    public void start(Stage primaryStage) {
        // start application
        stage = primaryStage;
        showStartScreen();
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        cleanup();
    }

    public static void showStartScreen() {
        cleanup();
        // show Start screen
        try {
            //Editor
            chatEditor = new ChatEditor();

            //View
            Parent root = FXMLLoader.load(StageManager.class.getResource("view/StartScreen.fxml"));
            Scene scene = new Scene(root);

            //Controller
            startCtrl = new StartScreenController(root, chatEditor);
            startCtrl.init();

            //display
            stage.setTitle("MiniChat - Start");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(false);
        } catch (Exception e) {
            System.err.println("error on showing StartScreen");
            e.printStackTrace();
        }
    }

    public static void showServerScreen() {
        cleanup();
        // show Server screen
        try {
            //View
            Parent root = FXMLLoader.load(StageManager.class.getResource("view/ServerScreen.fxml"));
            Scene scene = new Scene(root);

            //Controller
            serverCtrl = new ServerScreenController(root, chatEditor);
            serverCtrl.init();

            //display
            stage.setTitle("MiniChat - Server");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(false);
        } catch (Exception e) {
            System.err.println("error on showing ServerScreen");
            e.printStackTrace();
        }
    }

    public static void showClientScreen() {
        cleanup();
        // show client screen
        try {
            //View
            Parent root = FXMLLoader.load(StageManager.class.getResource("view/ClientScreen.fxml"));
            Scene scene = new Scene(root);

            //Controller
            clientCtrl = new ClientScreenController(root, chatEditor);
            clientCtrl.init();

            //display
            stage.setTitle("MiniChat - Client");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(false);
        } catch (Exception e) {
            System.err.println("error on showing ClientScreen");
            e.printStackTrace();
        }
    }

    private static void cleanup() {
        // call cascading stop
        if (startCtrl != null) {
            startCtrl.stop();
            startCtrl = null;
        }
        if (serverCtrl != null) {
            serverCtrl.stop();
            serverCtrl = null;
        }
        if (clientCtrl != null) {
            clientCtrl.stop();
            clientCtrl = null;
        }
    }

    public ChatEditor getChatEditor() {
        return chatEditor;
    }

}

