package client;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import model.*;
import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class ChessRepl implements client.websocket.NotificationHandler {
    private String authToken = null;
    private final ServerFacade serverFacade;
    private State state = State.SIGNEDOUT;
    private client.websocket.WebSocketFacade ws;
    private ChessGame currentGame; //used for redrawing the game
    private String currentPlayerColor; //stores the user's player color
    private String currentUsername;
    private int currentGameID;

    public ChessRepl(int port) throws Exception {
        serverFacade = new ServerFacade(port);
        ws = new WebSocketFacade("http://localhost:8080", this);
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
                System.out.print(SET_TEXT_COLOR_MAGENTA + result);
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }
    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_MAGENTA);
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
                case "quit" -> quit();
                //need to add cases for move, highlight, redraw, leave, resign, help---------------------------------
                case "move" -> makeMove(params);
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "resign" -> resign();
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
        currentUsername = username;
        return "Welcome " + username + ". Type 'help' to view commands \n";
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
        currentUsername = username;
        return "Welcome " + username + ". Type 'help' to view commands \n";
    }
    public String logout() throws Exception {
        assertSignedIn();
        serverFacade.logout(authToken);
        currentUsername = "";
        state = State.SIGNEDOUT;
        return "You have logged out.";
    }
    public String createGame(String... params) throws Exception {
        assertSignedIn();
        if(params.length != 1) {
            return "Expected: <GAME NAME>";
        }
        int gameID = serverFacade.createGame(authToken, params[0]);
        return "You have created " + params[0];
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
        return "Here are the games: \n" + result;
    }
    private void drawBoard(ChessGame game, String playerColor) {
        var board = game.getBoard();
        boolean whitePerspective = !playerColor.equals("BLACK");
        String colLabels;
        int startRow;
        int endRow;
        int rowStep;
        int startCol;
        int endCol;
        int colStep;
        if(!whitePerspective) {
            startRow = 1;
            endRow = 8;
            rowStep = 1;
            startCol = 8;
            endCol = 1;
            colStep = -1;
            colLabels = "      h     g     f     e     d     c     b     a";
        } else {
            startRow = 8;
            endRow = 1;
            rowStep = -1;
            startCol = 1;
            endCol = 8;
            colStep = 1;
            colLabels = "      a     b     c     d     e     f     g     h";
        }
        System.out.print(RESET_BG_COLOR + SET_TEXT_COLOR_WHITE + colLabels);
        System.out.println();
        for(int row = startRow; whitePerspective ? row >= endRow : row <= endRow; row += rowStep) {
            System.out.print(RESET_BG_COLOR + SET_TEXT_COLOR_WHITE + " " + row + " ");
            for(int col = startCol; whitePerspective ? col <= endCol : col >= endCol; col += colStep) {
                boolean placeLightSquare = (row + col) % 2 != 0;
                String squareColor = placeLightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                System.out.print(squareColor + " " + placePieces(board, row, col));
            }
            System.out.println(RESET_BG_COLOR + SET_TEXT_COLOR_WHITE + " " + row + " ");
        }
        System.out.print(RESET_BG_COLOR + SET_TEXT_COLOR_WHITE + colLabels);
    }
    private String placePieces (chess.ChessBoard board, int row, int col) {
        var piece = board.getPiece(new chess.ChessPosition(row, col));
        if (piece == null) {
            return "     ";
        }
        String color;
        boolean isWhite = piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE;
        if(isWhite) {
            color = SET_TEXT_BOLD + SET_TEXT_COLOR_GREEN;
        } else {
            color = SET_TEXT_BOLD + SET_TEXT_COLOR_YELLOW;
        }
        return color + switch(piece.getPieceType()) {
            case KING -> " Ki  ";
            case QUEEN -> "  Q  ";
            case BISHOP -> "  B  ";
            case KNIGHT -> " Kn  ";
            case ROOK -> "  R  ";
            case PAWN -> "  P  ";
        };

    }
    public String joinGame(String... params) throws Exception {
        assertSignedIn();
        if (params.length != 2) {
            return "Expected: <ID> <WHITE or BLACK>";
        }
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (NumberFormatException error) {
            return "<ID> must be a number";
        }
        String playerColor = params[1].toUpperCase();
        if(!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            return "Color must be WHITE or BLACK";
        }
        List<GameData> gamesList = serverFacade.listGames(authToken);
        if(gameNumber < 1 || gameNumber > gamesList.size()) {
            return "Game at index " + gameNumber + " does not exist.";
        }
        GameData selectedGame = gamesList.get(gameNumber - 1);
        serverFacade.joinGame(authToken, selectedGame.gameID(), playerColor);
        //ws = new client.websocket.WebSocketFacade("http://localhost:8080", this);
        ws.connect(authToken, selectedGame.gameID());
        //drawing the board (actually going to be handled by the notify)
        currentGame = selectedGame.game();
        currentPlayerColor = playerColor;
        currentGameID = selectedGame.gameID();
        //updating the user's state to 'INGAME'
        state = State.INGAME;
        return "\n Joined game " + selectedGame.gameName() + " as " + playerColor;
    }
    public String observeGame(String... params) throws Exception {
        assertSignedIn();
        if(params.length != 1) {
            return "Expected: <ID>";
        }
        int gameNumber = Integer.parseInt(params[0]);
        List<GameData> gamesList = serverFacade.listGames(authToken);
        if(gameNumber < 1 || gameNumber > gamesList.size()) {
            return "Game at index " + gameNumber + " does not exist.";
        }
        GameData selectedGame = gamesList.get(gameNumber - 1);
        //ws = new client.websocket.WebSocketFacade("http://localhost:8080", this);
        ws.connect(authToken, selectedGame.gameID());
        currentPlayerColor = "white";
        currentGameID = selectedGame.gameID();
        state = State.OBSERVER;
        //drawing the board
        drawBoard(selectedGame.game(), "WHITE");
        return "\n Observing game " + selectedGame.gameName();
    }
    public String quit() {
        return "quit";
    }

    public String redraw() throws Exception {
        assertPlayer();
        drawBoard(currentGame, currentPlayerColor);
        return "\n Board redrawn";
    }

    public String leave() throws Exception {
        assertPlayer();
        ws.leave(authToken, currentGameID);
        currentGame = null;
        state = State.SIGNEDIN;
        currentPlayerColor = null;
        return "Leaving game...";
    }

    public String resign() throws Exception {
        assertPlayer();
        ws.resign(authToken, currentGameID);
        return "Resigning game...";
    }

    private chess.ChessPosition getChessLocation(String square) {
        char[] colLetters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int col = 0;
        for (int i = 0; i < colLetters.length; i++) {
            if (colLetters[i] == Character.toLowerCase(square.charAt(0))) {
                col = i;
                break;
            }
        }
        int row = Character.getNumericValue(square.charAt(1)); //the number represents the row
        chess.ChessPosition position = new chess.ChessPosition(row, col);
        return position;
    }
    private boolean checkSquare(String square) {
        if (square.length() != 2) {
            return false;
        }
        char letter = Character.toLowerCase(square.charAt(0));
        char number = square.charAt(1);
        char[] validCols = {'a','b','c','d','e','f','g','h'};
        char[] validRows = {'1','2','3','4','5','6','7','8'};
        boolean validLetter = false;
        for (char c : validCols) {
            if (c == letter) {
                validLetter = true;
                break;
            }
        }
        boolean validNumber = false;
        for (char n : validRows) {
            if (n == number) {
                validNumber = true;
                break;
            }
        }
        return validLetter && validNumber;
    }
    public String makeMove(String... params) throws Exception {
        assertPlayer();
        if (params.length != 2) {
            return "Expected: <START LOCATION> <END LOCATION>";
        }
        if (!checkSquare(params[0]) || !checkSquare(params[1])) {
            return "Expected: letter representing the column followed by the number representing the row. Example: a3";
        }
        chess.ChessPosition startPosition = getChessLocation(params[0]);
        chess.ChessPosition endPosition = getChessLocation(params[1]);
    }

    public String help() {
        if(state == State.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to log in to account
                    quit - stop playing chess
                    help - display possible commands
                    """;
        } else if (state == State.SIGNEDIN) {
            return """
                    create <GAME NAME> - create a game with name
                    list - list all games
                    join <ID> <WHITE or BLACK> - join a game as white or black
                    observe <ID> - observe a game
                    logout - exit account
                    quit - stop playing chess
                    help - display possible commands
                    """;
        } else {
            return """
                    move <START LOCATION> <END LOCATION> - moves the piece at start location to end location
                    highlight <PIECE> - highlights the legal moves of piece
                    redraw - redraws the board
                    leave - exit the game
                    resign - forefit the game
                    help - display possible commands
                    """;
        }
    }
    private void assertSignedIn() throws Exception {
        if (state == State.SIGNEDOUT) {
            throw new Exception("You must sign in");
        }
    }
    private void assertInGame() throws Exception {
        if (state == State.SIGNEDIN || state == State.SIGNEDOUT || state == State.OBSERVER) {
            throw new Exception("You must be a player in a game");
        }
    }
    private void assertPlayer() throws Exception {
        if (state != State.INGAME) {
            throw new Exception("You must be a player in a game");
        }
    }
    private void assertObserver() throws Exception {
        if (state != State.OBSERVER) {
            throw new Exception("You must be an observer");
        }
    }

    @Override
    public void notify(websocket.messages.ServerMessage notification) {
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            //reprint the board
            websocket.messages.LoadGameMessage loadGameMessage = (websocket.messages.LoadGameMessage) notification;
            currentGame = loadGameMessage.getGameNotification();
            System.out.println("\n");
            drawBoard(currentGame, currentPlayerColor);
        } else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            //display notification
            websocket.messages.NotificationMessage notificationMessage = (websocket.messages.NotificationMessage) notification;
            System.out.println(SET_TEXT_COLOR_RED + notificationMessage.getNotification());
        } else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            //display the error
            websocket.messages.ErrorMessage errorMessage = (websocket.messages.ErrorMessage) notification;
            System.out.println(SET_TEXT_COLOR_RED + errorMessage.getErrorMessage());
        }
        printPrompt();
    }

}
