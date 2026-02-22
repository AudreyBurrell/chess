package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import model.GameData;

public class ClearService {
    private final DataAccess dataAccess;
    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public void clearService() throws DataAccessException {
        dataAccess.clearEverything();
    }
}
