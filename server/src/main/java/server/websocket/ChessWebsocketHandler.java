package server.websocket;

import chess.*;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

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

    private record ValidatedData (AuthData auth, GameData gameData) {}
    private ValidatedData validate(WsMessageContext ctx, UserGameCommand command) throws Exception {
        String authToken = command.getAuthToken();
        AuthData auth = dataAccess.getAuth(authToken);
        int gameID = command.getGameID();
        GameData gameData = dataAccess.getGame(gameID);
        if (auth == null) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: unauthorized")));
            return null;
        }
        if (gameData == null) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: game not found")));
            return null;
        }
        return new ValidatedData(auth, gameData);
    }

    private void connect(WsMessageContext ctx, UserGameCommand command) throws IOException {
        try {
            ValidatedData data = validate(ctx, command);
            if (data == null) {
                return;
            }
            GameData gameData = data.gameData();
            int gameID = gameData.gameID();
            AuthData auth = data.auth();
            connections.add(gameID, ctx.session);
            //should send LOAD_GAME to root client
            ctx.send(new Gson().toJson(new LoadGameMessage(gameData.game())));
            //sending notification
            String username = auth.username();
            String notification;
            if (username.equals(gameData.whiteUsername())) {
                notification = username + " joined game as WHITE";
            } else if (username.equals(gameData.blackUsername())) {
                notification = username + " joined game as BLACK";
            } else {
                notification = username + " is observing the game";
            }
            connections.broadcast(gameID, ctx.session, new NotificationMessage(notification));
        } catch (Exception e) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private String getSquareString(ChessPosition position) {
        char[] colLetters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        char col = colLetters[position.getColumn() - 1];
        int row = position.getRow();
        return "" + col + row;
    }

    private void makeMove(WsMessageContext ctx, UserGameCommand command) {
        try {
            ValidatedData data = validate(ctx, command);
            if (data == null) {
                return;
            }
            GameData gameData = data.gameData();
            int gameID = gameData.gameID();
            AuthData auth = data.auth();
            String username = auth.username();
            ChessGame.TeamColor turn = gameData.game().getTeamTurn();
            if (!((username.equals(gameData.whiteUsername()) && turn == ChessGame.TeamColor.WHITE) ||
                    (username.equals(gameData.blackUsername()) && turn == ChessGame.TeamColor.BLACK))) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: wrong player turn")));
                return;
            }
            if (gameData.game().isOver()) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: game is over")));
                return;
            }
            ChessGame chessGame = gameData.game();
            MakeMoveCommand makeMoveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
            ChessMove move = makeMoveCommand.getMove();
            chessGame.makeMove(move);
            GameData updatedGame = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), chessGame);
            dataAccess.updateGame(updatedGame);
            connections.broadcast(gameID, null, new LoadGameMessage(chessGame));
            String notification = auth.username() + " moved from " + getSquareString(move.getStartPosition())
                    + " to " + getSquareString(move.getEndPosition());
            connections.broadcast(gameID, ctx.session, new NotificationMessage(notification));
            ChessGame.TeamColor opponentColor = chessGame.getTeamTurn();
            //determining the username of the opponent
            String opponentUsername;
            if (!username.equals(gameData.whiteUsername())) {
                opponentUsername = gameData.whiteUsername();
            } else {
                opponentUsername = gameData.blackUsername();
            }
            if (chessGame.isInCheckmate(opponentColor)) {
                connections.broadcast(gameID, null, new NotificationMessage(auth.username() +
                        " has put " + opponentUsername + " in checkmate!"));
            } else if (chessGame.isInStalemate(opponentColor)) {
                connections.broadcast(gameID, null, new NotificationMessage("Stalemate"));
            } else if (chessGame.isInCheck(opponentColor)) {
                connections.broadcast(gameID, null, new NotificationMessage(opponentUsername + " is in check"));
            }
        } catch (InvalidMoveException e) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: invalid move " + e.getMessage())));
        } catch (Exception e) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private void leave(WsMessageContext ctx, UserGameCommand command) throws InvalidMoveException {
        try {
            ValidatedData data = validate(ctx, command);
            if (data == null) {
                return;
            }
            GameData gameData = data.gameData();
            int gameID = gameData.gameID();
            AuthData auth = data.auth();
            //making the white/black part of game data null
            String username = auth.username();
            String whiteUsername = gameData.whiteUsername();
            String blackUsername = gameData.blackUsername();
            if (username.equals(gameData.whiteUsername())) {
                whiteUsername = null;
            } else if (username.equals(gameData.blackUsername())) {
                blackUsername = null;
            }
            GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameData.gameName(), gameData.game());
            dataAccess.updateGame(updatedGame);
            connections.remove(gameID, ctx.session);
            String notification = auth.username() + " has left game.";
            connections.broadcast(gameID, null, new NotificationMessage(notification));
        } catch (Exception e) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private void resign(WsMessageContext ctx, UserGameCommand command) throws Exception {
        try {
            ValidatedData data = validate(ctx, command);
            if (data == null) {
                return;
            }
            GameData gameData = data.gameData();
            if (gameData.game().isOver()) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: game is already over")));
                return;
            }
            int gameID = gameData.gameID();
            AuthData auth = data.auth();
            String username = auth.username();
            if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: only players can resign")));
                return;
            }
            String winnerUsername;
            if (username.equals(gameData.whiteUsername())) {
                winnerUsername = gameData.blackUsername();
            } else {
                winnerUsername = gameData.whiteUsername();
            }
            ChessGame chessGame = gameData.game();
            chessGame.setOver(true);
            GameData updatedGame = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), chessGame);
            dataAccess.updateGame(updatedGame);
            String notification = username + " has resigned. " + winnerUsername + " has won the game.";
            connections.broadcast(gameID, null, new NotificationMessage(notification));
        } catch (Exception e) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

}
