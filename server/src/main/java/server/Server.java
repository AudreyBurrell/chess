package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import io.javalin.*;
import model.GameData;
import model.UserData;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.List;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        try {
            var dataAccess = new MySqlDataAccess();
            userService = new UserService(dataAccess);
            gameService = new GameService(dataAccess);
            clearService = new ClearService(dataAccess);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);


    }
    private void handleError(io.javalin.http.Context ctx, DataAccessException error) {
        String message = error.getMessage();
        if (message.contains("already taken")) {
            ctx.status(403);
            ctx.json("{\"message\": \"Error: color already taken\"}");
        } else if (message.contains("already exists")) {
            ctx.status(403);
            ctx.json("{\"message\": \"Error: already taken\"}");
        } else if (message.contains("Auth token does not exist")) {
            ctx.status(401);
            ctx.json("{\"message\": \"Error: unauthorized\"}");
        } else if (message.contains("Game does not exist")) {
            ctx.status(400);
            ctx.json("{\"message\": \"Error: bad request\"}");
        } else if (message.contains("does not exist") || message.contains("Incorrect password")) {
            ctx.status(401);
            ctx.json("{\"message\": \"Error: unauthorized\"}");
        } else {
            ctx.status(500);
            ctx.json("{\"message\": \"Error: " + message + "\"}");
        }
    }
    private void clear(io.javalin.http.Context ctx) {
        try {
            clearService.clearService();
            ctx.status(200);
            ctx.json("{}");
        } catch (DataAccessException error) {
            handleError(ctx, error);
        }
    }
    private void register(io.javalin.http.Context ctx) {
        try {
            var user = new Gson().fromJson(ctx.body(), UserData.class);
            if(user.username() == null || user.password() == null || user.email() == null){
                ctx.status(400);
                ctx.json("{\"message\": \"Error: bad request\"}");
                return;
            }
            var auth = userService.register(user);
            ctx.status(200);
            ctx.json(new Gson().toJson(auth));
        } catch (DataAccessException error) {
            handleError(ctx, error);
        }
    }
    private void login(io.javalin.http.Context ctx) {
        try {
            var user = new Gson().fromJson(ctx.body(), UserData.class);
            if(user.username() == null || user.password() == null) {
                ctx.status(400);
                ctx.json("{\"message\": \"Error: bad request\"}");
                return;
            }
            var auth = userService.login(user);
            ctx.status(200);
            ctx.json(new Gson().toJson(auth));
        } catch (DataAccessException error) {
            handleError(ctx, error);
        }
    }
    private void logout(io.javalin.http.Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if(authToken == null) {
                ctx.status(401);
                ctx.json("{\"message\": \"Error: unauthorized\"}");
                return;
            }
            userService.logout(authToken);
            ctx.status(200);
            ctx.json("{}");
        } catch (DataAccessException error) {
            handleError(ctx, error);
        }
    }
    private void createGame(io.javalin.http.Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if(authToken == null) {
                ctx.status(401);
                ctx.json("{\"message\": \"Error: unauthorized\"}");
                return;
            }
            GameData gameData = new Gson().fromJson(ctx.body(), GameData.class);
            String gameName = gameData.gameName();
            if(gameName == null) {
                ctx.status(400);
                ctx.json("{\"message\": \"Error: bad request\"}");
                return;
            }
            int gameID = gameService.createGame(authToken, gameName);
            ctx.status(200);
            ctx.json("{\"gameID\": " + gameID + "}");
        } catch (DataAccessException error) {
            handleError(ctx, error);
        }
    }
    private void joinGame(io.javalin.http.Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if(authToken == null) {
                ctx.status(401);
                ctx.json("{\"message\": \"Error: unauthorized\"}");
                return;
            }
            record JoinGameReq(String playerColor, int gameID) {}
            JoinGameReq request = new Gson().fromJson(ctx.body(), JoinGameReq.class);
            if(request.playerColor() == null || (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK"))) {
                ctx.status(400);
                ctx.json("{\"message\": \"Error: bad request\"}");
                return;
            }
            gameService.joinGame(authToken, request.gameID(), request.playerColor());
            ctx.status(200);
            ctx.json("{}");
        } catch (DataAccessException error) {
            handleError(ctx, error);
        }
    }
    private void listGames(io.javalin.http.Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if(authToken == null) {
                ctx.status(401);
                ctx.json("{\"message\": \"Error: unauthorized\"}");
                return;
            }
            List<GameData> gamesList = gameService.listGames(authToken);
            ctx.status(200);
            ctx.json(new Gson().toJson(Map.of("games", gamesList)));
        } catch (DataAccessException error) {
            handleError(ctx, error);
        }
    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
