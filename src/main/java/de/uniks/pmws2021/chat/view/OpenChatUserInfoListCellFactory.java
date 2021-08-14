package de.uniks.pmws2021.chat.view;

import de.uniks.pmws2021.chat.controller.ClientScreenController;
import de.uniks.pmws2021.chat.model.OpenChatUserInfo;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

import static de.uniks.pmws2021.chat.Constants.COM_CHANNEL_ALL;

public class OpenChatUserInfoListCellFactory implements Callback<ListView<OpenChatUserInfo>, ListCell<OpenChatUserInfo>> {


    @Override
    public ListCell<OpenChatUserInfo> call(ListView<OpenChatUserInfo> param) {
        return new OpenChatUserInfoListCell();
    }

    private static class OpenChatUserInfoListCell extends ListCell<OpenChatUserInfo> {

        @Override
        protected void updateItem(OpenChatUserInfo openChatUserInfo, boolean empty) {
            HBox cell = new HBox();
            VBox textCell = new VBox();
            HBox notificationCell = new HBox();
            HBox nameCell = new HBox();
            HBox lastMessageCell = new HBox();
            HBox buttonCell = new HBox();
            Label name = new Label();
            Label lastMessage = new Label();
            Button buttonClose = new Button();

            super.updateItem(openChatUserInfo, empty);
            if (empty || openChatUserInfo == null) {
                setItem(null);
                setGraphic(null);
            } else {
                //textCell.setAlignment(Pos.CENTER);
                //textCell.setSpacing(5);

                int notificationCircleSize = 20;
                notificationCell.setAlignment(Pos.CENTER);
                notificationCell.setMinHeight(notificationCircleSize);
                notificationCell.setMaxHeight(notificationCircleSize);
                notificationCell.setMinWidth(notificationCircleSize);
                notificationCell.setMaxWidth(notificationCircleSize);
                HBox.setMargin(notificationCell, new Insets(0, 10, 0, 10));

                textCell.setAlignment(Pos.CENTER);
                textCell.setPrefHeight(30);
                VBox.setMargin(textCell, new Insets(0, 0, 0, 0));


                if (openChatUserInfo.getUnreadMessagesCounter() > 0) {
                    Circle background = new Circle(notificationCircleSize / 2, Color.RED);

                    Label numberText = new Label();
                    numberText.setTextFill(Color.WHITE);
                    numberText.setText(String.valueOf(openChatUserInfo.getUnreadMessagesCounter()));

                    StackPane stackPaneUnreadMessages = new StackPane(background, numberText);

                    notificationCell.getChildren().add(stackPaneUnreadMessages);
                }
                nameCell.setAlignment(Pos.CENTER_LEFT);
                lastMessageCell.setAlignment(Pos.CENTER_LEFT);

                name.setText(openChatUserInfo.getUserName());
                name.setStyle("-fx-font-weight: bold");
                nameCell.getChildren().add(name);

                if (openChatUserInfo.getLastMessage() != null) {
                    lastMessage.setText(openChatUserInfo.getLastMessage().getSender() + ": " + openChatUserInfo.getLastMessage().getContent());
                    lastMessageCell.getChildren().add(lastMessage);
                    textCell.getChildren().addAll(nameCell, lastMessageCell);
                } else {
                    textCell.getChildren().add(nameCell);
                }

                int buttonSize = 30;
                buttonCell.setAlignment(Pos.CENTER);
                buttonCell.setMinHeight(buttonSize);
                buttonCell.setMaxHeight(buttonSize);
                buttonCell.setMinWidth(buttonSize);
                buttonCell.setMaxWidth(buttonSize);
                HBox.setMargin(buttonCell, new Insets(0, 10, 0, 10));


                buttonClose.setText("X");
                buttonClose.setMinHeight(buttonSize);
                buttonClose.setMaxHeight(buttonSize);
                buttonClose.setMinWidth(buttonSize);
                buttonClose.setMaxWidth(buttonSize);
                buttonClose.setStyle("-fx-text-fill: white; -fx-background-color: #2E64FE; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5;");

                buttonClose.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ClientScreenController.closeChat(openChatUserInfo);
                    }
                });


                if (openChatUserInfo.getUnreadMessagesCounter() > 0 && openChatUserInfo.getUserName().equals(COM_CHANNEL_ALL)) {
                    cell.getChildren().addAll(notificationCell, textCell);
                    textCell.setPrefWidth(135);
                } else if (openChatUserInfo.getUnreadMessagesCounter() == 0 && openChatUserInfo.getUserName().equals(COM_CHANNEL_ALL)) {
                    cell.getChildren().addAll(textCell);
                    textCell.setPrefWidth(180);

                } else if (openChatUserInfo.getUnreadMessagesCounter() > 0 && !openChatUserInfo.getUserName().equals(COM_CHANNEL_ALL)) {
                    cell.getChildren().addAll(notificationCell, textCell, buttonClose);
                    textCell.setPrefWidth(110);
                } else if (openChatUserInfo.getUnreadMessagesCounter() == 0 && !openChatUserInfo.getUserName().equals(COM_CHANNEL_ALL)) {
                    cell.getChildren().addAll(textCell, buttonClose);
                    textCell.setPrefWidth(150);
                }

                setGraphic(cell);
            }
        }
    }
}
