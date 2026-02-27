package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import model.GameData;
import model.UserData;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        var dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        clearService = new ClearService(dataAccess);

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        //javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);


    }
    private void clear(io.javalin.http.Context ctx) {
        try {
            clearService.clearService();
            ctx.status(200);
            ctx.json("{}");
        } catch (DataAccessException error) {
            ctx.status(500);
            ctx.json("{\"message\": \"Error: " + error.getMessage() + "\"}");
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
            if(error.getMessage().contains("already exists")) {
                ctx.status(403);
                ctx.json("{\"message\": \"Error: Username already exists\"}");
            } else {
                ctx.status(500);
                ctx.json("{\"message\": \"Error: " + error.getMessage() + "\"}");
            }
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
            if(error.getMessage().contains("already exists")) {
                ctx.status(500);
                ctx.json("{\"message\": \"Error: Username does not exist\"}");
            } else {
                ctx.status(401);
                ctx.json("{\"message\": \"Error: " + error.getMessage() + "\"}");
            }
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
            ctx.status(500);
            ctx.json("{\"message\": \"Error: " + error.getMessage() + "\"}");
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
            if(error.getMessage().contains("Auth token does not exist")) {
                ctx.status(401);
                ctx.json("{\"message\": \"Error: unauthorized\"}");
            } else {
                ctx.status(500);
                ctx.json("{\"message\": \"Error: " + error.getMessage() + "\"}");
            }
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
            if(error.getMessage().contains("already taken")) {
                ctx.status(403);
                ctx.json("{\"message\": \"Error: color already taken\"}");
            } else if(error.getMessage().contains("Game does not exist")) {
                ctx.status(400);
                ctx.json("{\"message\": \"Error: bad request\"}");
            }else if(error.getMessage().contains("does not exist")) {
                ctx.status(401);
                ctx.json("{\"message\": \"Error: unauthorized\"}");
            } else {
                ctx.status(500);
                ctx.json("{\"message\": \"Error: " + error.getMessage() + "\"}");
            }

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
