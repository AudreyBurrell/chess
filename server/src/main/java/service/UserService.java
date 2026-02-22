package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import model.GameData;

public class UserService {
    private final DataAccess dataAccess;
    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public AuthData register(UserData user) throws DataAccessException {
        if(user.username() == null || user.password() == null) {
            throw new DataAccessException("Username and password must be filled out");
        }
        if(dataAccess.getUser(user.username()) != null) {
            throw new DataAccessException("Username already exists");
        }
        dataAccess.createUser(user);
        AuthData auth = dataAccess.createAuth(user.username());
        return auth;
    }
    public AuthData login(UserData user) throws DataAccessException {
        if(dataAccess.getUser(user.username()) == null) {
            throw new DataAccessException("Username does not exist");
        }
        UserData experimentUser = dataAccess.getUser(user.username());
        if(!experimentUser.password().equals(user.password())) {
            throw new DataAccessException("Incorrect password");
        }
        AuthData auth = dataAccess.createAuth(user.username());
        return auth;
    }
    public void logout(String authToken) throws DataAccessException {
        if(dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Auth does not exist");
        }
        dataAccess.deleteAuth(authToken);
    }


}
