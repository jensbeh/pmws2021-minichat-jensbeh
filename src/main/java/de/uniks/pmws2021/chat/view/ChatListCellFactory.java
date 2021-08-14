package de.uniks.pmws2021.chat.view;

import de.uniks.pmws2021.chat.model.Message;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class ChatListCellFactory implements Callback<ListView<Message>, ListCell<Message>> {
    private static String currentUser;

    public ChatListCellFactory(String currentUser) {
        this.currentUser = currentUser;
    }

    public void setCurrentUser2(String currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public ListCell<Message> call(ListView<Message> param) {
        return new ChatListCellFactory.ChatListCell();
    }

    private static class ChatListCell extends ListCell<Message> {

        @Override
        protected void updateItem(Message message, boolean empty) {
            HBox messageCell = new HBox();

            Label nameLabel = new Label();
            Label messageLabel = new Label();

            super.updateItem(message, empty);
            if (empty || message == null) {
                setItem(null);
                setGraphic(null);

            } else {
                messageCell.setSpacing(5);

                String sender = message.getSender();
                String messageTxt = message.getContent();


                if (currentUser.equals(sender)) {
                    messageCell.setAlignment(Pos.CENTER_RIGHT);
                    messageCell.getChildren().addAll(nameLabel, messageLabel);
                } else {
                    messageCell.setAlignment(Pos.CENTER_LEFT);
                    messageCell.getChildren().addAll(nameLabel, messageLabel);
                }

                nameLabel.setText(sender + ":");
                nameLabel.setStyle("-fx-font-weight: bold");
                nameLabel.setFont(new Font(15));
                messageLabel.setText(messageTxt);

                setGraphic(messageCell);
            }
        }
    }
}
