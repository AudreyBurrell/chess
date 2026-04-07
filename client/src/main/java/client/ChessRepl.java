package client;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import chess.*;
import model.*;
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
                case "move" -> makeMove(params);
                case "highlight" -> highlight(params);
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
    private void drawBoard(ChessGame game, String playerColor, ChessPosition highlightedPiecePosition, Collection<ChessMove> validHighlightedMoves) {
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
        String squareColor;
        for(int row = startRow; whitePerspective ? row >= endRow : row <= endRow; row += rowStep) {
            System.out.print(RESET_BG_COLOR + SET_TEXT_COLOR_WHITE + " " + row + " ");
            for(int col = startCol; whitePerspective ? col <= endCol : col >= endCol; col += colStep) {
                //check if it is highlighted select square, else do this other stuff
                ChessPosition testPosition = new ChessPosition(row, col);
                if (testPosition.equals(highlightedPiecePosition)) {
                    squareColor = SET_BG_COLOR_MAGENTA;
                } else if (validHighlightedMoves != null && checkHighlightSquare(validHighlightedMoves, testPosition)) {
                    squareColor = SET_BG_COLOR_BLUE;
                } else {
                    boolean placeLightSquare = (row + col) % 2 != 0;
                    if (placeLightSquare) {
                        squareColor = SET_BG_COLOR_LIGHT_GREY;
                    } else {
                        squareColor = SET_BG_COLOR_DARK_GREY;
                    }
                }
                System.out.print(squareColor + " " + placePieces(board, row, col, highlightedPiecePosition, validHighlightedMoves));
            }
            System.out.println(RESET_BG_COLOR + SET_TEXT_COLOR_WHITE + " " + row + " ");
        }
        System.out.print(RESET_BG_COLOR + SET_TEXT_COLOR_WHITE + colLabels);
    }
    private String placePieces (ChessBoard board, int row, int col, ChessPosition highlightedPiecePosition, Collection<ChessMove> validHighlightedMoves) {
        //CHANGE THE TEXT COLOR OF POTENTIAL HIGHLIGHTED ITEMS HERE
        var piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            return "     ";
        }
        String color;
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        boolean highlightSquare = false;
        if (validHighlightedMoves != null) {
            highlightSquare = checkHighlightSquare(validHighlightedMoves, new ChessPosition(row, col));
        }
        if ((highlightedPiecePosition != null && row == highlightedPiecePosition.getRow() &&
                col == highlightedPiecePosition.getColumn()) || highlightSquare) {
            color = SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK;
        } else if(isWhite) {
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

    private boolean checkHighlightSquare(Collection<ChessMove> validMoves, ChessPosition position) {
        for (ChessMove move : validMoves) {
            ChessPosition endPosition = move.getEndPosition();
            if (endPosition.getRow() == position.getRow() && endPosition.getColumn() == position.getColumn()) {
                return true;
            }
        }
        return false;
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
        ws.connect(authToken, selectedGame.gameID());
        currentPlayerColor = "white";
        currentGameID = selectedGame.gameID();
        state = State.OBSERVER;
        return "\n Observing game " + selectedGame.gameName();
    }
    public String quit() {
        return "quit";
    }

    public String redraw() throws Exception {
        assertPlayer();
        drawBoard(currentGame, currentPlayerColor, null, null);
        return "\n";
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

    private ChessPosition getChessLocation(String square) {
        char[] colLetters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int col = 0;
        for (int i = 0; i < colLetters.length; i++) {
            if (colLetters[i] == Character.toLowerCase(square.charAt(0))) {
                col = i + 1;
                break;
            }
        }
        int row = Character.getNumericValue(square.charAt(1)); //the number represents the row
        ChessPosition position = new ChessPosition(row, col);
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
        ChessPosition startPosition = getChessLocation(params[0]);
        ChessPosition endPosition = getChessLocation(params[1]);
        ChessPiece piece = currentGame.getBoard().getPiece(startPosition);
        if (piece == null) {
            return "No piece at location " + params[0];
        }
        Collection<ChessMove> validMoves = currentGame.validMoves(startPosition);
        if (validMoves == null || validMoves.isEmpty()) {
            return "No valid moves for this piece.";
        }
        boolean containsEndPosition = false;
        for (ChessMove move : validMoves) {
            if (move.getEndPosition().equals(endPosition)) {
                containsEndPosition = true;
                break;
            }
        }
        if (!containsEndPosition) {
            return "End position is not a valid move.";
        }
        //now the actual movement logic
        ChessPiece.PieceType promotionPiece = null;
        if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && endPosition.getRow() == 8) ||
                (piece.getTeamColor() == ChessGame.TeamColor.BLACK && endPosition.getRow() == 1)) {
            System.out.print("The piece is eligible for promotion. Promote to <QUEEN> <ROOK> <BISHOP> or <KNIGHT>:");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().toUpperCase();
            promotionPiece = ChessPiece.PieceType.valueOf(input);
        }
        ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
        ws.makeMove(authToken, currentGameID, move);
        return "";
    }

    public String highlight(String... params) throws Exception {
        assertPlayer();
        if (params.length != 1) {
            return "Expected: <LOCATION>";
        }
        if (!checkSquare(params[0])) {
            return "Expected: letter representing the column followed by the number representing the row. Example: a3";
        }
        ChessPosition piecePosition = getChessLocation(params[0]);
        ChessPiece piece = currentGame.getBoard().getPiece(piecePosition);
        if (piece == null) {
            return "No piece at location " + params[0];
        }
        Collection<ChessMove> validMoves = currentGame.validMoves(piecePosition);
        if (validMoves == null || validMoves.isEmpty()) {
            return "No valid moves for this piece.";
        }
        //redraw the board (but don't send it to both users), with the validMoves and position highlighted too
        drawBoard(currentGame, currentPlayerColor, piecePosition, validMoves);
        return "";
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
                    highlight <LOCATION> - highlights the legal moves of piece at location
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
            drawBoard(currentGame, currentPlayerColor, null, null);
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
