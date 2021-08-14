package de.uniks.pmws2021.chat;

import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public class StageManagerTest extends ApplicationTest {
    private Stage stage;
    private StageManager app;

    @Override
    public void start(Stage stage) {
        // start application
        this.stage = stage;
        app = new StageManager();
        app.start(stage);
        this.stage.centerOnScreen();
    }

    @Test
    public void changeViewTest() {
        // Start
        Assert.assertEquals("MiniChat - Start", stage.getTitle());
        clickOn("#button_server");

        // Server
        Assert.assertEquals("MiniChat - Server", stage.getTitle());
        clickOn("#button_dcAll");
        clickOn("#button_dcOne");
        clickOn("#button_closeServer");

        // Start
        Assert.assertEquals("MiniChat - Start", stage.getTitle());
        clickOn("#button_client");
        write("userName\n");
        ChatEditor chatEditor = this.app.getChatEditor();
        Assert.assertEquals("userName", chatEditor.getChat().getAvailableUser().get(0).getName());
        Assert.assertEquals(true, chatEditor.getChat().getAvailableUser().get(0).getStatus());

        // Client
        Assert.assertEquals("MiniChat - Client", stage.getTitle());
        TextField inputMessage = lookup("#tf_message").query();
        inputMessage.setText("Hello there!");
        String message = inputMessage.getText();
        Assert.assertEquals(message, "Hello there!");
        //clickOn("#button_send");
        //String message2 = inputMessage.getText();
        //Assert.assertEquals(message2, "");
        clickOn("#button_leave");

        // Start
        Assert.assertEquals("MiniChat - Start", stage.getTitle());
    }
}