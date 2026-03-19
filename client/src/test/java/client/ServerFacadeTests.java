package client;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(port);
    }
    @BeforeEach
    public void clearEverything() throws Exception {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    //register tests
    @Test
    public void registerPositiveTest() throws Exception {
        AuthData result = serverFacade.register("testUser", "testUser", "testUser@email.com");
        assertNotNull(result);
    }
    @Test
    public void registerNegativeTest() throws Exception {
        serverFacade.register("testUser", "testUser", "testUser@email.com");
        assertThrows(
                Exception.class,
                () -> serverFacade.register("testUser", "testUser", "testUser@email.com")
        );
    }
    //login tests
    @Test
    public void loginPositiveTest() throws Exception {
        serverFacade.register("testUser", "testUser", "testUser@email.com");
        AuthData result = serverFacade.login("testUser", "testUser");
        assertNotNull(result);
    }
    @Test
    public void loginNegativeTest() throws Exception {
        assertThrows(
                Exception.class,
                () -> serverFacade.login("testUser", "testUser")
        );
    }
    //logout tests
    @Test
    public void logoutPositiveTest() throws Exception {
        AuthData auth = serverFacade.register("testUser", "testUser", "testUser@email.com");
        serverFacade.logout(auth.authToken());
    }
    @Test
    public void logoutNegativeTest() throws Exception {
        assertThrows (
                Exception.class,
                () -> serverFacade.logout("fakeToken")
        );
    }
    //create game test
    @Test
    public void createGamePositiveTest() throws Exception {
        AuthData auth = serverFacade.register("testUser", "testUser", "testUser@email.com");
        int gameID = serverFacade.createGame(auth.authToken(), "testGame");
        assert(gameID > 0);
    }
    @Test
    public void createGameNegativeTest() throws Exception {
        assertThrows(
                Exception.class,
                () -> serverFacade.createGame("fakeToken", "testGame")
        );
    }
    //list game test
    @Test
    public void listGamePositiveTest() throws Exception {
        AuthData auth = serverFacade.register("testUser", "testUser", "testUser@email.com");
        serverFacade.createGame(auth.authToken(), "game1");
        serverFacade.createGame(auth.authToken(), "game2");
        serverFacade.createGame(auth.authToken(), "game3");
        List<GameData> gameList = new ArrayList(serverFacade.listGames(auth.authToken()));
        assertNotNull(gameList);
        assertEquals(3, gameList.size());
    }
    @Test
    public void listGameNegativeTest() throws Exception {
        assertThrows(
                Exception.class,
                () -> serverFacade.listGames("fakeToken")
        );
    }
    //join game test
    @Test
    public void joinGamePositiveTest() throws Exception {
        AuthData auth = serverFacade.register("testUser", "testUser", "testUser@email.com");
        int gameID = serverFacade.createGame(auth.authToken(), "testGame");
        serverFacade.joinGame(auth.authToken(), gameID, "WHITE");
    }
    @Test
    public void joinGameNegativeTest() throws Exception {
        AuthData auth = serverFacade.register("testUser", "testUser", "testUser@email.com");
        int gameID = serverFacade.createGame(auth.authToken(), "testGame");
        serverFacade.joinGame(auth.authToken(), gameID, "WHITE");
        assertThrows(
                Exception.class,
                () -> serverFacade.joinGame(auth.authToken(), gameID, "WHITE")
        );
    }
    //clear test
    @Test
    public void clearPositiveTest() throws Exception {
        AuthData auth = serverFacade.register("testUser", "testUser", "testUser@email.com");
        serverFacade.createGame(auth.authToken(), "testGame");
        serverFacade.createGame(auth.authToken(), "testGame2");
        serverFacade.clear();
    }

}
