package de.uniks.pmws2021.chat.view;

import de.uniks.pmws2021.chat.model.User;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class OnlineUserListCellFactory implements Callback<ListView<User>, ListCell<User>> {


    @Override
    public ListCell<User> call(ListView<User> param) {
        return new UserListCell();
    }

    private static class UserListCell extends ListCell<User> {

        @Override
        protected void updateItem(User user, boolean empty) {
            HBox textCell = new HBox();
            Label name = new Label();
            Label status = new Label();

            super.updateItem(user, empty);
            if (empty || user == null) {
                setItem(null);
                setGraphic(null);

            } else {
                textCell.setAlignment(Pos.CENTER);
                textCell.setSpacing(5);

                name.setText(user.getName());
                name.setStyle("-fx-font-weight: bold");
                if (user.getStatus()) {
                    status.setText("Online");
                } else {
                    status.setText("Offline");
                }

                textCell.getChildren().addAll(name, status);
                setGraphic(textCell);
            }
        }
    }
}
