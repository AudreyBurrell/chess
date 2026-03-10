package dataaccess;

import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
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
    //get user tests
    @Test
    public void getUserPositiveTest() throws DataAccessException {
        dataAccess.createUser(new UserData("testUser", "password", "test@email.com"));
        UserData result = dataAccess.getUser("testUser");
        assertNotNull(result);
        assertEquals("test@email.com", result.email());
    }
    @Test
    public void getUserNegativeTest() throws DataAccessException {
        UserData result = dataAccess.getUser("fakeUsername");
        assertNull(result);
    }
    //create auth tests
    @Test
    public void createAuthPositiveTest() throws DataAccessException {
        AuthData auth = dataAccess.createAuth("testUser");
        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertEquals("testUser", auth.username());
    }
    @Test
    public void createAuthNegativeTest() throws DataAccessException {
        dataAccess.createAuth("testUser");
        AuthData returned = dataAccess.getAuth("wrongAuth");
        assertNull(returned);
    }
    //get auth test
    @Test
    public void getAuthPositiveTest() throws DataAccessException {
        AuthData auth = dataAccess.createAuth("testUser");
        AuthData retrieved = dataAccess.getAuth(auth.authToken());
        assertNotNull(retrieved);
        assertEquals("testUser", retrieved.username());
    }
    @Test
    public void getAuthNegativeTest() throws DataAccessException {
        AuthData retrieved = dataAccess.getAuth("fakeToken");
        assertNull(retrieved);
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
    //get game test
    @Test
    public void getGamePositiveTest() throws DataAccessException {
        GameData game = new GameData(0, null, null, "testGame", null);
        int gameID =  dataAccess.createGame(game);
        GameData retrieved = dataAccess.getGame(gameID);
        assertNotNull(retrieved);
        assertEquals("testGame", retrieved.gameName());
        assertEquals(1, retrieved.gameID());
    }
    @Test
    public void getGameNegativeTest() throws DataAccessException {
        GameData retrieved = dataAccess.getGame(1234567890);
        assertNull(retrieved);
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
    //create game
    @Test
    public void updateGamePositiveTest() throws DataAccessException {
        GameData game = new GameData(0, null, null, "testGame", null);
        int gameID = dataAccess.createGame(game);
        GameData updatedGame = new GameData(gameID, "testUser", null, "testGame", null);
        dataAccess.updateGame(updatedGame);
        GameData retrieved = dataAccess.getGame(gameID);
        assertEquals(1, retrieved.gameID());
        assertEquals("testUser", retrieved.whiteUsername());

    }
    @Test
    public void updateGameNegativeTest() throws DataAccessException {
        GameData game = new GameData(9999999, "testUser", null, "fakeGame", null);
        dataAccess.updateGame(game);
        assertNull(dataAccess.getGame(9999999));
    }

}
