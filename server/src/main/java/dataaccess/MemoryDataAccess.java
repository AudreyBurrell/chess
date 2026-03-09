package dataaccess;
import chess.ChessGame;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class MemoryDataAccess implements DataAccess {
    private int idNum = 1;
    private final HashMap<String, UserData> usersMap = new HashMap<>();
    private final HashMap<String, AuthData> authDataMap = new HashMap<>();
    private final HashMap<Integer, GameData> gamesMap = new HashMap<>();

    @Override
    public UserData createUser(UserData user) throws DataAccessException {
        if(getUser(user.username()) != null) {
            throw new DataAccessException("User already exists");
        }
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        UserData newUserInfo = new UserData(user.username(), hashedPassword, user.email());
        usersMap.put(user.username(), newUserInfo);
        return user;
    }

    @Override
    public UserData getUser(String username) {
        return usersMap.get(username);
    }

    @Override
    public AuthData createAuth(String username) {
        String randomAuth = UUID.randomUUID().toString();
        AuthData authData = new AuthData(randomAuth, username);
        authDataMap.put(randomAuth, authData);
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authDataMap.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        authDataMap.remove(authToken);
    }

    @Override
    public void clearEverything() {
        usersMap.clear();
        authDataMap.clear();
        gamesMap.clear();
        idNum = 1;
    }

    @Override
    public int createGame(GameData game) {
        int gameID = idNum++;
        GameData newGame = new GameData(
                gameID, null, null, game.gameName(), new ChessGame()
        );
        gamesMap.put(gameID, newGame);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) {
        return gamesMap.get(gameID);
    }

    @Override
    public List<GameData> listGames() {
        List<GameData> gameData = new ArrayList<>();
        for(int i = 1; i < idNum; i++) {
            gameData.add(getGame(i));
        }
        return gameData;
    }

    @Override
    public void updateGame(GameData game) {
        gamesMap.put(game.gameID(), game);
    }
}
