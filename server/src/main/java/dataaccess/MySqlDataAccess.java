package dataaccess;

import com.google.gson.Gson;
//import exception.DataAccessException;
import model.*;

import java.sql.*;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlDataAccess implements DataAccess {

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            username varchar(256) NOT NULL,
            password varchar(256) NOT NULL,
            email varchar(256) NOT NULL,
            PRIMARY KEY (username)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
            gameID int NOT NULL AUTO_INCREMENT,
            gameName varchar(256) NOT NULL,
            whiteUsername varchar(256),
            blackUsername varchar(256),
            game TEXT NOT NULL,
            PRIMARY KEY (gameID)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
            authToken varchar(256) NOT NULL,
            username varchar(256) NOT NULL,
            PRIMARY KEY (authToken)
            )
            """

    };
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: " + ex.getMessage());
        }
    }

    @Override
    public UserData createUser(UserData user) throws dataaccess.DataAccessException {
        return null;
    }

    @Override
    public UserData getUser(String username) throws dataaccess.DataAccessException {
        return null;
    }

    @Override
    public AuthData createAuth(String username) throws dataaccess.DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws dataaccess.DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws dataaccess.DataAccessException {

    }

    @Override
    public void clearEverything() throws dataaccess.DataAccessException {

    }

    @Override
    public int createGame(GameData game) throws dataaccess.DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws dataaccess.DataAccessException {
        return null;
    }

    @Override
    public List<GameData> listGames() throws dataaccess.DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws dataaccess.DataAccessException {

    }
}
