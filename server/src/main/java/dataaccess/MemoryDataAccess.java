package dataaccess;
import chess.ChessGame;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
        usersMap.put(user.username(), user);
        return user;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return usersMap.get(username);
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String randomAuth = UUID.randomUUID().toString();
        AuthData authData = new AuthData(randomAuth, username);
        authDataMap.put(randomAuth, authData);
        return authData;
    }

//    @Override
//    public UserData loginUser(UserData user) throws DataAccessException {
//        if(getUser(user.username()) != null) {
//            return user;
//        } else {
//            throw new DataAccessException("Username required");
//        }
//    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDataMap.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authDataMap.remove(authToken);
    }

    @Override
    public void clearEverything() throws DataAccessException {
        usersMap.clear();
        authDataMap.clear();
        gamesMap.clear();
        idNum = 1;
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        int gameID = idNum++;
        GameData newGame = new GameData(
                gameID, null, null, game.gameName(), new ChessGame()
        );
        gamesMap.put(gameID, newGame);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return gamesMap.get(gameID);
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
//        return null;
        List<GameData> gameData = new ArrayList<>();
        for(int i = 1; i < idNum; i++) {
            gameData.add(getGame(i));
        }
        return gameData;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        gamesMap.put(game.gameID(), game);
    }
}
