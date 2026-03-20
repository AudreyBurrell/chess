package client;

import java.net.http.HttpClient;
import com.google.gson.Gson;
import model.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var request = buildRequest("POST", "/user", new UserData(username, password, email), null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(String username, String password) throws Exception {
        var request = buildRequest("POST", "/session", new UserData(username, password, null), null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }
    public void logout(String authToken) throws Exception {
        var request = buildRequest("DELETE","/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    //helper functions to get the game body and id
    public final class GameBodyRequest {
        private final String gameName;
        public GameBodyRequest(String gameName) {
            this.gameName = gameName;
        }
        public String getGameName() {
            return gameName;
        }
    }
    public final class GameIDResponse {
        private final int gameID;
        public GameIDResponse(int gameID) {
            this.gameID = gameID;
        }
        public int getGameID() {
            return gameID;
        }
    }
    public int createGame(String authToken, String gameName) throws Exception {
        var request = buildRequest("POST", "/game", new GameBodyRequest(gameName), authToken);
        var response = sendRequest(request);
        return handleResponse(response, GameIDResponse.class).getGameID();
    }

    //deserializing list data
    public final class ListGameResponse {
        private final List<GameData> games;
        public ListGameResponse(List<GameData> games) {
            this.games = games;
        }
        public List<GameData> getGames() {
            return games;
        }
    }
    public List<GameData> listGames(String authToken) throws Exception {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListGameResponse.class).getGames();
    }

    //deserializing gameID and playerCoor
    public final class JoinGameBodyRequest {
        private final int gameID;
        private final String playerColor;
        public JoinGameBodyRequest(int gameID, String playerColor) {
            this.gameID = gameID;
            this.playerColor = playerColor;
        }

    }
    public void joinGame(String authToken, int gameID, String playerColor) throws Exception {
        var request = buildRequest("PUT", "/game", new JoinGameBodyRequest(gameID, playerColor), authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clear() throws Exception {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }


    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            System.out.println("DEBUG sendRequest exception: " + ex.getClass().getName() + " - " + ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new Exception(body);
            }

            throw new Exception("other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        System.out.println("DEBUG response body: " + response.body());
        System.out.println("DEBUG status: " + status);

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }



}
