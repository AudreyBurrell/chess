package dataaccess;
import model.*;

import java.util.HashMap;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess {
    private int idNum = 1;
    private final HashMap<String, UserData> usersMap = new HashMap<>();
    private final HashMap<String, AuthData> authDataMap = new HashMap<>();

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
        authDataMap.put(username, authData);
        return authData;
    }

    @Override
    public UserData loginUser(UserData user) throws DataAccessException {
        if(getUser(user.username()) != null) {
            return user;
        } else {
            throw new DataAccessException("Username required");
        }
    }
    //I need to write something to get the authData

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        return authDataMap.get(username);
    }
}
