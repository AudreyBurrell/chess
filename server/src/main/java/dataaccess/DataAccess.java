package dataaccess;

import model.*;

public interface DataAccess {
    //used in create user
    UserData createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    AuthData createAuth(String username) throws DataAccessException;
    //login
    UserData loginUser(UserData user) throws DataAccessException;
    AuthData getAuth(String username) throws DataAccessException;

}
