package client;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
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

    }
    @Test
    public void createGameNegativeTest() throws Exception {

    }
    //list game test
    @Test
    public void listGamePositiveTest() throws Exception {

    }
    @Test
    public void listGameNegativeTest() throws Exception {

    }
    //join game test
    @Test
    public void joinGamePositiveTest() throws Exception {

    }
    @Test
    public void joinGameNegativeTest() throws Exception {

    }
    //clear test
    @Test
    public void clearPositiveTest() throws Exception {

    }

}
