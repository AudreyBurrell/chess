package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    private final DataAccess dataAccess;
    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public int createGame(String authToken, String gameName) throws DataAccessException {
        if(dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Auth token does not exist");
        }
        GameData data = new GameData(0, null, null, gameName, null);
        return dataAccess.createGame(data);
    }
    public List<GameData> listGames(String authToken) throws DataAccessException {
        if(dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Auth token does not exist");
        }
        List<GameData> gamesList = new ArrayList<>();
        gamesList.addAll(dataAccess.listGames());
        return gamesList;
    }
    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException {
        if(dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Auth token does not exist");
        }
        GameData data = dataAccess.getGame(gameID);
        if(data == null) {
            throw new DataAccessException("Game does not exist");
        }
        if(playerColor.equals("WHITE")) {
            if(data.whiteUsername() != null) {
                throw new DataAccessException("White is already taken");
            }
            dataAccess.updateGame(new GameData(data.gameID(), dataAccess.getAuth(authToken).username(), data.blackUsername(), data.gameName(), data.game()));
        }
        if(playerColor.equals("BLACK")) {
            if(data.blackUsername() != null) {
                throw new DataAccessException("Black is already taken");
            }
            dataAccess.updateGame(new GameData(data.gameID(), data.whiteUsername(),  dataAccess.getAuth(authToken).username(), data.gameName(), data.game()));
        }
    }
}
