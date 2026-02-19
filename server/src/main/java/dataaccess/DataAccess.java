package dataaccess;

import model.*;

public interface DataAccess {
    UserData createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    AuthData createAuth(String username) throws DataAccessException;
}
