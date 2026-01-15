package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

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
        ChessPosition king_location = null;
        for(int row = 1; row <= 8; row++) {
            for(int col=1; col<=8; col++){
                ChessPosition piece_position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(piece_position);
                if(piece.getPieceType() == ChessPiece.PieceType.KING) {
                    if(piece.getTeamColor() == team_color){
                        king_location = piece_position;
                    }
                }
            }
        }
        //determining if that location is in check
        ChessGame.TeamColor opponent_color;
        if (teamColor == ChessGame.TeamColor.WHITE){
            opponent_color = ChessGame.TeamColor.BLACK;
        } else {
            opponent_color = ChessGame.TeamColor.WHITE;
        }
        for (int row = 1; row <= 8; row++){
            for (int col=1; col<=8; col++){
                ChessPosition piece_position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(piece_position);
                if (piece != null && piece.getTeamColor() == opponent_color) {
                    Collection<ChessMove> opponent_moves = piece.pieceMoves(board, piece_position);
                    for (ChessMove move : opponent_moves) {
                        if (move.getEndPosition().equals(king_location)){
                            is_in_check = true;
                            break;
                        }
                    }
                }
            }
        }
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
//        throw new RuntimeException("Not implemented");
        boolean is_in_check = isInCheck(teamColor);
        boolean is_stalemate = false;
        if (is_in_check) {
            return is_stalemate;
        }
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPosition piece_position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(piece_position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possible_moves = piece.pieceMoves(board, piece_position);
                    if (possible_moves.isEmpty()) {
                        continue;
                    } else {
                        return is_stalemate;
                    }
                }
            }
        }
        is_stalemate = true;
        return is_stalemate;

    }

    /**
     * Sets this game's chessboard with a given board
     */
    public void setBoard() {
//        throw new RuntimeException("Not implemented");
        //this.board = board;
        this.board = new ChessBoard();
        board.resetBoard();


    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
//        throw new RuntimeException("Not implemented");
        return board;
    }


}
