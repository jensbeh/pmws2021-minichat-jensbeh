package de.uniks.pmws2021.chat.network.client;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.util.JsonUtil;

import javax.json.JsonObject;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import static de.uniks.pmws2021.chat.Constants.COM_NOOP;

public class WebSocketClient extends Endpoint {

    private Session session;
    private Timer noopTimer;

    private final ChatEditor model;

    private WSCallback callback;

    public WebSocketClient(ChatEditor model, URI endpoint, WSCallback callback) {
        this.model = model;
        this.noopTimer = new Timer();

        try {
            System.out.println(model.getChat().getCurrentUsername());
            ClientEndpointConfig clientConfig = ClientEndpointConfig.Builder.create()
                    .configurator(new CustomWebSocketConfigurator(model.getChat().getCurrentUsername()))
                    .build();

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(
                    this,
                    clientConfig,
                    endpoint
            );
            this.callback = callback;
        } catch (Exception e) {
            System.err.println("Error during establishing websocket connection:");
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        // Store session
        this.session = session;
        // add MessageHandler
        this.session.addMessageHandler(String.class, this::onMessage);

        this.noopTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Send NOOP Message
                System.out.println("##### NOOP MESSAGE #####");
                try {
                    sendMessage(COM_NOOP);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000 * 30);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        // cancel timer
        this.noopTimer.cancel();
        // set session null
        this.session = null;
    }

    private void onMessage(String message) {
        // Process Message
        JsonObject jsonObject = JsonUtil.parse(message);
        // Use callback to handle it
        this.callback.handleMessage(jsonObject);
    }

    public void sendMessage(String message) throws IOException {
        // check if session is still open
        if (this.session != null && this.session.isOpen()) {
            // send message
            this.session.getBasicRemote().sendText(message);
            this.session.getBasicRemote().flushBatch();
        }
    }

    public void stop() throws IOException {
        // cancel timer
        this.noopTimer.cancel();
        // close session
        this.session.close();
    }
}
