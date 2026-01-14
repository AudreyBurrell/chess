package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor team_turn;
    TeamColor team_color;
    ChessBoard board;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
//        throw new RuntimeException("Not implemented");
        return team_turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
//        throw new RuntimeException("Not implemented");
        this.team_turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
//        throw new RuntimeException("Not implemented");
        ChessPiece current_piece = board.getPiece(startPosition);
        if (current_piece == null) {
            return null;
        }
        return current_piece.pieceMoves(getBoard(), startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
//        throw new RuntimeException("Not implemented");
        Collection<ChessMove> move_options = validMoves(move.getStartPosition());
        if (move_options == null || !move_options.contains(move)){
            throw new InvalidMoveException("Invalid move");
        }
        ChessPiece piece = getBoard().getPiece(move.getStartPosition());
        ChessPiece captured_piece = getBoard().getPiece(move.getEndPosition());
        //IF THERE IS A PIECE, REMOVE IT
        getBoard().addPiece(move.getEndPosition(), piece);
        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        boolean is_in_check = false;
        //throw new RuntimeException("Not implemented");
        //if the king current position is in any move that the other team can make
        //get the piece moves for the current team
        List<ChessMove> all_moves = new ArrayList<>();
        //getting all the moves NEED TO DO
        //checking if king is there
        //for each item in that list (to get the position), get a piece in that position. If it exists, check if it's king and the opposite  color, then set opposite color in check
        return is_in_check;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");

    }

    /**
     * Sets this game's chessboard with a given board
     */
    public void setBoard() {
//        throw new RuntimeException("Not implemented");
        //this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
//        throw new RuntimeException("Not implemented");
        return this.board;
    }
}
