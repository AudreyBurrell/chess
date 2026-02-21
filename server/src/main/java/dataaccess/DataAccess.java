package dataaccess;

import model.*;

public interface DataAccess {
    //used in create user
    UserData createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    //    UserData loginUser(UserData user) throws DataAccessException;
    //auth
    AuthData createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;


//    clear: A method for clearing all data from the database. This is used during testing.
//    createUser: Create a new user. DONE
//    getUser: Retrieve a user with the given username. DONE
//    createGame: Create a new game.
//    getGame: Retrieve a specified game with the given game ID.
//    listGames: Retrieve all games.
//    updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made.
//    createAuth: Create a new authorization. DONE
//    getAuth: Retrieve an authorization given an authToken. DONE
//    deleteAuth: Delete an authorization so that it is no longer valid.

}
