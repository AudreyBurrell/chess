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
}
