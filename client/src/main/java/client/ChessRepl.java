package client;

import java.util.Arrays;
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
                default -> help();
            };
        } catch (Exception e) {
            System.out.println("DEBUG ERROR: " + e.getClass().getName() + " - " + e.getMessage());
            return e.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        System.out.println("DEBUG: starting register");
        if(params.length != 3) {
            return "Expected: <USERNAME> <PASSWORD> <EMAIL>";
        }
        System.out.println("DEBUG: params ok");
        String username = params[0];
        String password = params[1];
        String email = params[2];
        System.out.println("DEBUG: calling serverFacade.register");
        AuthData auth = serverFacade.register(username, password, email);
        System.out.println("DEBUG: got auth: " + auth);
        authToken = auth.authToken();
        state = State.SIGNEDIN;
        return "Welcome " + username + "\n";
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
                    create <NAME> - create a game with name
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
}
