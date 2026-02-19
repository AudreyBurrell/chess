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
    //register
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
    //login test ideas
    //positive test: user gets in
    @Test
    public void loginUserPositive() throws DataAccessException {
        //adding data to the dataAccess
        UserData user = new UserData("testUser", "password", "test@email.com");
        dataAccess.createUser(user);
        //I want to use an assert true to make sure it is in the dataAccess
        UserData result = dataAccess.loginUser(user);
        assertTrue(result != null);
        assertEquals("testUser", result.username());
    }
    //negative test: username doesn't match
    @Test
    public void loginUserNoMatch() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@email.com");
        dataAccess.createUser(user);
        UserData username = new UserData("testuser", "password", "test@email.com");
        assertThrows (
                DataAccessException.class,
                () -> dataAccess.loginUser(username)
        );
    }
    //testing auth data stuff


    //logout

    //list game tests

    //create game tests

    //join game tests

    //clear tests

}
