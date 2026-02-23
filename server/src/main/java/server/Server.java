package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
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
            ctx.status(500);
            ctx.json("{\"message\": \"Error: " + error.getMessage() + "\"}");
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
            ctx.status(401);
            ctx.json("{\"message\": \"Error: " + error.getMessage() + "\"}");
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
