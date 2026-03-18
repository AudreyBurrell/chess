package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    //register tests
    @Test
    public void registerPositiveTest() throws DataAccessException {

    }
    @Test
    public void registerNegativeTest() throws DataAccessException {

    }
    //login tests
    @Test
    public void loginPositiveTest() throws DataAccessException {

    }
    @Test
    public void loginNegativeTest() throws DataAccessException {

    }
    //logout tests
    @Test
    public void logoutPositiveTest() throws DataAccessException {

    }
    @Test
    public void logoutNegativeTest() throws DataAccessException {

    }
    //create game test
    @Test
    public void createGamePositiveTest() throws DataAccessException {

    }
    @Test
    public void createGameNegativeTest() throws DataAccessException {

    }
    //list game test
    @Test
    public void listGamePositiveTest() throws DataAccessException {

    }
    @Test
    public void listGameNegativeTest() throws DataAccessException {

    }
    //join game test
    @Test
    public void joinGamePositiveTest() throws DataAccessException {

    }
    @Test
    public void joinGameNegativeTest() throws DataAccessException {

    }
    //clear test
    @Test
    public void clearPositiveTest() throws DataAccessException {

    }

}
