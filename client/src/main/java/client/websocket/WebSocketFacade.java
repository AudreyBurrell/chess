package client.websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new Exception(e.getMessage());
        }
    }

    //classes that correspond to those in WebsocketHandler
    public void connect(String authToken, int gameID) throws Exception {
        var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    //DON'T DELETE THIS
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
