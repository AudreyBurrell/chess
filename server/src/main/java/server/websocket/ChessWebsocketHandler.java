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
import websocket.messages.ServerMessage;

import java.io.IOException;

public class ChessWebsocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

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

        } catch (Exception e) {

        }
    }

    private void makeMove(WsMessageContext ctx, UserGameCommand command) {

    }

    private void leave(WsMessageContext ctx, UserGameCommand command) throws IOException {

    }

    private void resign(WsMessageContext ctx, UserGameCommand command) {

    }

}
