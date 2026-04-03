package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
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
                    ServerMessage messageInfo = new Gson().fromJson(message, ServerMessage.class);
                    ServerMessage notification;
                    if (messageInfo.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        notification = new Gson().fromJson(message, websocket.messages.LoadGameMessage.class);
                    } else if (messageInfo.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                        notification = new Gson().fromJson(message, websocket.messages.ErrorMessage.class);
                    } else {
                        notification = new Gson().fromJson(message, websocket.messages.NotificationMessage.class);
                    }
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
    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception {
        var command = new MakeMoveCommand(authToken, gameID, move);
        session.getBasicRemote().sendText(new Gson().toJson(command));
    }
    public void leave(String authToken, int gameID) throws Exception {
        var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        session.getBasicRemote().sendText(new Gson().toJson(command));
    }
    public void resign(String authToken, int gameID) throws Exception {
        var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        session.getBasicRemote().sendText(new Gson().toJson(command));
    }



    //DON'T DELETE THIS
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
