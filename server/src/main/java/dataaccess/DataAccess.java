package dataaccess;

import model.*;

import java.util.List;

public interface DataAccess {
    //used in create user
    UserData createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    //auth
    AuthData createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    //clear
    void clearEverything() throws DataAccessException;
    //game
    int createGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;



}
