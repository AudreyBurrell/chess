package client;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;
import model.*;

import static ui.EscapeSequences.*;

public class ChessRepl {
    private String authToken = null;
    private final ServerFacade serverFacade;
    private State state = State.SIGNEDOUT;

    public ChessRepl(int port) {
        serverFacade = new ServerFacade(port);
    }
    public void run() {
        System.out.println("Welcome to Chess.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                if(result == null) {
                    result = "";
                }
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }
    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                //all the cases goes here
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                default -> help();
            };
        } catch (Exception e) {
            //System.out.println("DEBUG ERROR: " + e.getClass().getName() + " - " + e.getMessage());
            return e.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        if(params.length != 3) {
            return "Expected: <USERNAME> <PASSWORD> <EMAIL>";
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];
        AuthData auth = serverFacade.register(username, password, email);
        authToken = auth.authToken();
        state = State.SIGNEDIN;
        return "Welcome " + username + "\n";
    }
    public String login(String... params) throws Exception {
        if(params.length != 2) {
            return "Expected: <USERNAME> <PASSWORD>";
        }
        String username = params[0];
        String password = params[1];
        AuthData auth = serverFacade.login(username, password);
        authToken = auth.authToken();
        state = State.SIGNEDIN;
        return "Welcome " + username + ". Type 'help' to view commands \n";
    }
    public String logout() throws Exception {
        assertSignedIn();
        serverFacade.logout(authToken);
        state = State.SIGNEDOUT;
        return "You have logged out.";
    }
    public String createGame(String... params) throws Exception {
        assertSignedIn();
        if(params.length != 1) {
            return "Expected: <GAME NAME>";
        }
        int gameID = serverFacade.createGame(authToken, params[0]);
        return "You have created " + params[0] + " with ID = " + gameID;
    }
    public String listGames() throws Exception {
        assertSignedIn();
        List<GameData> gameList = serverFacade.listGames(authToken);
        var result = new StringBuilder();
        int i = 1;
        for(GameData game : gameList) {
            result.append(i).append(". ")
                    .append(game.gameName())
                    .append(" | White: ").append(game.whiteUsername() != null ? game.whiteUsername() : "empty")
                    .append(" | Black: ").append(game.blackUsername() != null ? game.blackUsername() : "empty")
                    .append("\n");
            i++;
        }
        return "Here are the games: \n" + result.toString();
    }
    public String joinGame(String... params) throws Exception {
        assertSignedIn();
        if (params.length != 2) {
            return "Expected: <ID> <WHITE or BLACK>";
        }
        int gameNumber = Integer.parseInt(params[0]);
        String playerColor = params[1].toUpperCase();
        List<GameData> gamesList = serverFacade.listGames(authToken);
        GameData selectedGame = gamesList.get(gameNumber - 1);
        serverFacade.joinGame(authToken, selectedGame.gameID(), playerColor);
        //drawing the board
        return "Joined game " + selectedGame.gameName() + " as " + playerColor;
    }
    public String observeGame(String... params) throws Exception {
        assertSignedIn();
        if(params.length != 1) {
            return "Expected: <ID>";
        }
        int gameNumber = Integer.parseInt(params[0]);
        List<GameData> gamesList = serverFacade.listGames(authToken);
        GameData selectedGame = gamesList.get(gameNumber - 1);
        //drawing the board
        return "Observing game " + selectedGame.gameName();
    }

    public String help() {
        if(state == State.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to log in to account
                    quit - stop playing chess
                    help - display possible commands
                    """;
        } else {
            return """
                    create <GAME NAME> - create a game with name
                    list - list all games
                    join <ID> <WHITE or BLACK> - join a game as white or black
                    observe <ID> - observe a game
                    logout - exit account
                    quit - stop playing chess
                    help - display possible commands
                    """;
        }
    }
    private void assertSignedIn() throws Exception {
        if (state == State.SIGNEDOUT) {
            throw new Exception("You must sign in");
        }
    }
    /*to do:
    * register DONE
    * login DONE
    * logout DONE
    * create game DONE
    * list agme DONE
    * join game DONE
    * observe game
    * quit (for both states)
    * */

}
