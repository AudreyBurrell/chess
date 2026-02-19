package service;

import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.DataAccessException;
import model.UserData;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private MemoryDataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = new MemoryDataAccess();
    }

    @Test
    public void createUserPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@email.com");
        dataAccess.createUser(user);
        UserData retrieved = dataAccess.getUser("testUser");
        assertNotNull(retrieved);
    }
    @Test
    public void createUserAlreadyExists() throws DataAccessException {
        UserData user = new UserData("userA", "password", "userA@email.com");
        dataAccess.createUser(user); //adding the user, shouldn't throw an exception
        UserData user2 = new UserData("userA", "password2", "userB@email.com");
        assertThrows(
                DataAccessException.class,
                () -> dataAccess.createUser(user2)
        );
    }
}
