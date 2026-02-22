package service;

import dataaccess.MemoryDataAccess;
import model.UserData;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.DataAccessException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private MemoryDataAccess dataAccess;
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
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
//    @Test
//    public void loginUserPositive() throws DataAccessException {
//        //adding data to the dataAccess
//        UserData user = new UserData("testUser", "password", "test@email.com");
//        dataAccess.createUser(user);
//        //I want to use an assert true to make sure it is in the dataAccess
//        UserData result = dataAccess.loginUser(user);
//        assertTrue(result != null);
//        assertEquals("testUser", result.username());
//    }
    //negative test: username doesn't match
//    @Test
//    public void loginUserNoMatch() throws DataAccessException {
//        UserData user = new UserData("testUser", "password", "test@email.com");
//        dataAccess.createUser(user);
//        UserData username = new UserData("testuser", "password", "test@email.com");
//        assertThrows (
//                DataAccessException.class,
//                () -> dataAccess.loginUser(username)
//        );
//    }
    //testing auth data stuff
    //creating auth data
    @Test
    public void createAuth() throws DataAccessException {
        AuthData authData = dataAccess.createAuth("testUser");
        assertNotNull(authData);
        assertNotNull(authData.authToken());
    }
    //getting auth data
    @Test
    public void getAuthData() throws DataAccessException {
        AuthData auth = dataAccess.createAuth("testUser");
        AuthData retrievedAuth = dataAccess.getAuth(auth.authToken());
        assertEquals(auth, retrievedAuth);
    }
    //removing auth data
    @Test
    public void removeAuthData() throws DataAccessException {
        AuthData auth = dataAccess.createAuth("testUser");
        dataAccess.deleteAuth(auth.authToken());
        assertNull(dataAccess.getAuth(auth.authToken()));
    }
    //list game tests
    @Test
    public void listGamesTest() throws DataAccessException {
        GameData game1 = new GameData(1, null, null, "testGame", null);
        GameData game2 = new GameData(2, null, null, "testGame2", null);
        GameData game3 = new GameData(3, null, null, "testGame3", null);
        int gameID1 = dataAccess.createGame(game1);
        int gameID2 = dataAccess.createGame(game2);
        int gameID3 = dataAccess.createGame(game3);
        List<GameData> gamesList = dataAccess.listGames();
        assertNotNull(gamesList);
        assertEquals(3, gamesList.size());
        assertEquals("testGame", gamesList.get(0).gameName());
        assertEquals("testGame2", gamesList.get(1).gameName());
        assertEquals("testGame3", gamesList.get(2).gameName());
    }
    //create game tests
    @Test
    public void createGameTest() throws DataAccessException {
        GameData testingGame = new GameData(1, null, null, "testGame", null);
        int gameID = dataAccess.createGame(testingGame);
        assertTrue(gameID > 0);
    }
    //get game test
    @Test
    public void getGameTest() throws DataAccessException {
        GameData testingGame = new GameData(1, null, null, "testGame", null);
        int gameID = dataAccess.createGame(testingGame);
        GameData retrievedGame = dataAccess.getGame(testingGame.gameID());
        assertNotNull(retrievedGame);
        assertEquals(testingGame.gameName(), retrievedGame.gameName());
    }
    //update game tests
    @Test
    public void updateGameTest() throws DataAccessException {
        GameData originalGame = new GameData(0, null, null, "testGame", null);
        int gameID = dataAccess.createGame(originalGame);
        GameData updatedGame = new GameData(gameID, "testUser", null, "testGame", null);
        dataAccess.updateGame(updatedGame);
        GameData retrieved = dataAccess.getGame(gameID);
        assertEquals("testUser", retrieved.whiteUsername());
    }
    //clear tests
    @Test
    public void clearUsersTest() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@email.com");
        dataAccess.createUser(user);
        UserData user2 = new UserData("testUser2", "password2", "test2@email.com");
        dataAccess.createUser(user2);
        dataAccess.clearEverything();
        assertNull(dataAccess.getUser("testUser"));
        assertNull(dataAccess.getUser("testUser2"));
    }
    @Test
    public void clearAuthTest() throws DataAccessException {
        AuthData auth = dataAccess.createAuth("testUser");
        AuthData auth2 = dataAccess.createAuth("testUser2");
        dataAccess.clearEverything();
        assertNull(dataAccess.getAuth(auth.authToken()));
        assertNull(dataAccess.getAuth(auth2.authToken()));
    }
    @Test
    public void clearGamesTest() throws DataAccessException {
        GameData game1 = new GameData(1, null, null, "testGame", null);
        int gameID = dataAccess.createGame(game1);
        GameData game2 = new GameData(2, null, null, "testGame2", null);
        int gameID2 = dataAccess.createGame(game2);
        dataAccess.clearEverything();
        assertEquals(0, dataAccess.listGames().size());
    }
    //USER SERVICE TESTS
    //register
    @Test
    public void registerTest() throws DataAccessException {
        UserData data = new UserData("testUser", "testUser", "testUser@email.com");
        AuthData result = userService.register(data);
        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
    }
    //login
    @Test
    public void loginTest() throws DataAccessException {
        UserData data = new UserData("testUser", "testUser", "testUser@email.com");
        userService.register(data);
        AuthData result = userService.login(data);
        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
    }
    //logout
    @Test
    public void logoutTest() throws DataAccessException {
        UserData data = new UserData("testUser", "testUser", "testUser@email.com");
        AuthData auth = userService.register(data);
        userService.logout(auth.authToken());
        assertNull(dataAccess.getAuth(auth.authToken()));
    }
    //GAME SERVICE TESTS
    //create game
    @Test
    public void createGameServiceTest() throws DataAccessException {
        UserData userData = new UserData("testUser", "testUser", "testUser@email.com");
        AuthData auth = userService.register(userData);
        int gameID = gameService.createGame(auth.authToken(), "testGame");
        assertEquals(1, gameID);
    }
    //list game
    @Test
    public void listGameServiceTest() throws DataAccessException {
        GameData game1 = new GameData(1, null, null, "testGame", null);
        GameData game2 = new GameData(2, null, null, "testGame2", null);
        GameData game3 = new GameData(3, null, null, "testGame3", null);
        int gameID1 = dataAccess.createGame(game1);
        int gameID2 = dataAccess.createGame(game2);
        int gameID3 = dataAccess.createGame(game3);
        UserData data = new UserData("testUser", "testUser", "testUser@email.com");
        AuthData auth = userService.register(data);
        List<GameData> gamesList = gameService.listGames(auth.authToken());
        assertNotNull(gamesList);
        assertEquals(3, gamesList.size());
    }
    //join game
    @Test
    public void joinGameWhiteTestPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "testUser", "testUser@email.com");
        AuthData auth = userService.register(user);
        int gameID = gameService.createGame(auth.authToken(), "testGame");
        gameService.joinGame(auth.authToken(),gameID,"WHITE");
        GameData game = dataAccess.getGame(gameID);
        assertEquals("testUser", game.whiteUsername());
    }

    @Test
    public void joinGameWhiteTestNegative() throws DataAccessException {
        //white username is already taken
        UserData user1 = new UserData("testUser1", "testUser1", "testUser1@email.com");
        AuthData auth1 = userService.register(user1);
        int gameID = gameService.createGame(auth1.authToken(), "testGame");
        gameService.joinGame(auth1.authToken(), gameID, "WHITE");
        UserData user2 = new UserData("testUser2", "testUser2", "testUser2@email.com");
        AuthData auth2 = userService.register(user2);
        assertThrows (
                DataAccessException.class,
                () -> gameService.joinGame(auth2.authToken(), gameID, "WHITE")
        );
    }

}
