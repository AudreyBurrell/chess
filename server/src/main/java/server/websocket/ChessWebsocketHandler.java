package server.websocket;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class ChessWebsocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final dataaccess.DataAccess dataAccess;

    public ChessWebsocketHandler(dataaccess.DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed.");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket connected!");
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(ctx, command);
            case MAKE_MOVE -> makeMove(ctx, command);
            case LEAVE -> leave(ctx, command);
            case RESIGN -> resign(ctx, command);
        }
    }

    private void connect(WsMessageContext ctx, UserGameCommand command) throws IOException {
        try {
            String authToken = command.getAuthToken();
            int gameID = command.getGameID();
            AuthData auth = dataAccess.getAuth(authToken);
            if (auth == null) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: unauthorized")));
                return;
            }
            GameData game = dataAccess.getGame(gameID);
            if (game == null) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: game not found")));
                return;
            }
            connections.add(gameID, ctx.session);
            //should send LOAD_GAME to root client
            ctx.send(new Gson().toJson(new LoadGameMessage(game.game())));
            //sending notification
            String username = auth.username();
            String notification;
            if (username.equals(game.whiteUsername())) {
                notification = username + " joined game as WHITE";
            } else if (username.equals(game.blackUsername())) {
                notification = username + " joined game as BLACK";
            } else {
                notification = username = " is observing the game";
            }
            connections.broadcast(gameID, ctx.session, new NotificationMessage(notification));
        } catch (Exception e) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private void makeMove(WsMessageContext ctx, UserGameCommand command) {

    }

    private void leave(WsMessageContext ctx, UserGameCommand command) throws IOException {

    }

    private void resign(WsMessageContext ctx, UserGameCommand command) {

    }

}
