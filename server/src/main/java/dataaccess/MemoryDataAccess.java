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
}
