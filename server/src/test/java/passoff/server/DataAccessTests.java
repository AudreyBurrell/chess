package dataaccess;

import model.UserData;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    private MySqlDataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        dataAccess.clearEverything();
    }


    //create user positive and negative test
    @Test
    public void createUserPositiveTest() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@email.com");
        dataAccess.createUser(user);
        UserData retrieved = dataAccess.getUser("testUser");
        assertNotNull(retrieved);
        assertEquals("testUser", retrieved.username());
    }
    @Test
    public void createUserNegativeTest() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@email.com");
        dataAccess.createUser(user);
        assertThrows(
                DataAccessException.class,
                () -> dataAccess.createUser(user)
        );
    }
}
