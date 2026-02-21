package dataaccess;

import model.*;

import java.util.List;

public interface DataAccess {
    //used in create user
    UserData createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    //    UserData loginUser(UserData user) throws DataAccessException;
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


//    clear: A method for clearing all data from the database. This is used during testing. DONE
//    createUser: Create a new user. DONE
//    getUser: Retrieve a user with the given username. DONE
//    createGame: Create a new game. DONE
//    getGame: Retrieve a specified game with the given game ID. DONE
//    listGames: Retrieve all games. DONE
//    updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made. DONE
//    createAuth: Create a new authorization. DONE
//    getAuth: Retrieve an authorization given an authToken. DONE
//    deleteAuth: Delete an authorization so that it is no longer valid. DONE

}
