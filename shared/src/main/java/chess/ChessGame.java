package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board;
    TeamColor team_turn;
    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.team_turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
//        throw new RuntimeException("Not implemented");
        return this.team_turn;
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
        throw new RuntimeException("Not implemented");

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
//        throw new RuntimeException("Not implemented");
        ChessPosition king_position = null;
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPosition testing_position = new ChessPosition(row, col);
                ChessPiece testing_piece = board.getPiece(testing_position);
                if(testing_piece != null && testing_piece.getPieceType() == ChessPiece.PieceType.KING){
                    king_position = new ChessPosition(row, col);
                    break;
                }
            }
        }
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPosition testing_position = new ChessPosition(row, col);
                ChessPiece testing_piece = board.getPiece(testing_position);
                if(testing_piece != null && testing_piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> opponent_moves = testing_piece.pieceMoves(board, testing_position);
                    for(ChessMove move : opponent_moves) {
                        if(move.getEndPosition().equals(king_position)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
//        throw new RuntimeException("Not implemented");
        if(!isInCheck(teamColor)) {
            return false;
        }
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPosition testing_position = new ChessPosition(row, col);
                ChessPiece testing_piece = board.getPiece(testing_position);
                if(testing_piece != null && testing_piece.getTeamColor() == teamColor){
                    Collection<ChessMove> possible_moves = testing_piece.pieceMoves(board, testing_position);
                    if(!possible_moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
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
        if(isInCheck(teamColor)) {
            return false;
        }
        //Collection<ChessMove> possible_moves;
        List<ChessMove> possible_moves = new ArrayList<>();
        //go through each square, see if there is a piece and it's the same color, add moves to possible_moves
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPosition testing_position = new ChessPosition(row, col);
                ChessPiece testing_piece = board.getPiece(testing_position);
                if(testing_piece != null && testing_piece.getTeamColor() == teamColor) {
                    possible_moves.addAll(testing_piece.pieceMoves(board, testing_position));
                }
            }
        }
        if(possible_moves.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
//        throw new RuntimeException("Not implemented");
        this.board = board;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && team_turn == chessGame.team_turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, team_turn);
    }
}

