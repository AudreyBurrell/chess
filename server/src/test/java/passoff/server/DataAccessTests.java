package dataaccess;

import model.UserData;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    //create and get auth positive and negative tests
    @Test
    public void createAuthPositiveTest() throws DataAccessException {
        AuthData auth = dataAccess.createAuth("testUser");
        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertEquals("testUser", auth.username());
    }
    @Test
    public void createAuthNegativeTest() throws DataAccessException {
        AuthData auth = dataAccess.createAuth("testUser");
        AuthData returned = dataAccess.getAuth("wrongAuth");
        assertNull(returned);
    }
    //delete auth
    @Test
    public void deleteAuthPositiveTest() throws DataAccessException {
        AuthData auth = dataAccess.createAuth("testUser");
        dataAccess.deleteAuth(auth.authToken());
        assertNull(dataAccess.getAuth(auth.authToken()));

    }
    @Test
    public void deleteAuthNegativeTest() throws DataAccessException {
        dataAccess.deleteAuth("randomAuth");
        assertNull(dataAccess.getAuth("randomAuth"));
    }
    //create game tests
    @Test
    public void createGamePositiveTest() throws DataAccessException {
        GameData game = new GameData(0, null, null, "testGame", null);
        int gameID = dataAccess.createGame(game);
        assertTrue(gameID > 0);
        GameData retrieved = dataAccess.getGame(gameID);
        assertNotNull(retrieved);
        assertEquals("testGame", retrieved.gameName());
    }
    @Test
    public void createGameNegativeTest() throws DataAccessException {
        assertThrows(
                DataAccessException.class,
                () -> dataAccess.createGame(new GameData(0, null, null, null, null))
        );
    }
    //list games
    @Test
    public void listGamesPositiveTest() throws DataAccessException {
        GameData game = new GameData(0, null, null, "testGame1", null);
        GameData game2 = new GameData(1, null, null, "testGame2", null);
        GameData game3 = new GameData(2, null, null, "testGame3", null);
        dataAccess.createGame(game);
        dataAccess.createGame(game2);
        dataAccess.createGame(game3);
        List<GameData> gamesList = dataAccess.listGames();
        assertNotNull(gamesList);
        assertEquals(3, gamesList.size());
    }
    @Test
    public void listGamesNegativeTest() throws DataAccessException {
        List<GameData> gamesList = dataAccess.listGames();
        assertEquals(0, gamesList.size());
    }

}
