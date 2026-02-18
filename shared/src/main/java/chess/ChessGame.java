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
    TeamColor teamTurn;
    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
//        throw new RuntimeException("Not implemented");
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
//        throw new RuntimeException("Not implemented");
        this.teamTurn = team;
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
        ChessPiece testingPiece = board.getPiece(startPosition);
        if(testingPiece == null) {
            return null;
        }
        Collection<ChessMove> allMoves = testingPiece.pieceMoves(board, startPosition);
        List<ChessMove> validMoves = new ArrayList<>();
        //for each item in valid moves, test if moving there would put the king in danger of check
        for(ChessMove move : allMoves) {
            ChessPiece potentialCapturedPiece = board.getPiece(move.getEndPosition());
            ChessPiece movingPiece = board.getPiece(move.getStartPosition());
            ChessPiece pieceToPlace = movingPiece;
            if(move.getPromotionPiece() != null) {
                pieceToPlace = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
            }
            board.addPiece(move.getEndPosition(), pieceToPlace);
            board.addPiece(move.getStartPosition(), null);
            if(!isInCheck(testingPiece.getTeamColor())) {
                validMoves.add(move);
            }
            board.addPiece(move.getStartPosition(), movingPiece);
            board.addPiece(move.getEndPosition(), potentialCapturedPiece);
        }
        return validMoves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
//        throw new RuntimeException("Not implemented");
        ChessPiece currentPiece = board.getPiece(move.getStartPosition());
        if(currentPiece == null) {
            throw new InvalidMoveException("Start position is null");
        }
        if(currentPiece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Piece color does not equal piece turn");
        }
        Collection<ChessMove> valid_moves = validMoves(move.getStartPosition());
        if(!valid_moves.contains(move)) {
            throw new InvalidMoveException("Move not valid");
        }
        ChessPiece pieceToPlace;
        if(move.getPromotionPiece() != null) {
            pieceToPlace = new ChessPiece(currentPiece.getTeamColor(), move.getPromotionPiece());
        } else {
            pieceToPlace = currentPiece;
        }
        board.addPiece(move.getEndPosition(), pieceToPlace);
        board.addPiece(move.getStartPosition(), null);
        if(currentPiece.getTeamColor() == TeamColor.WHITE) {
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
//        throw new RuntimeException("Not implemented");
        ChessPosition kingPosition = null;
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPosition testingPosition = new ChessPosition(row, col);
                ChessPiece testingPiece = board.getPiece(testingPosition);
                if(testingPiece != null && testingPiece.getPieceType() == ChessPiece.PieceType.KING && testingPiece.getTeamColor() == teamColor){
                    kingPosition = new ChessPosition(row, col);
                    break;
                }
            }
        }
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPosition testingPosition = new ChessPosition(row, col);
                ChessPiece testingPiece = board.getPiece(testingPosition);
                if(testingPiece != null && testingPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> opponent_moves = testingPiece.pieceMoves(board, testingPosition);
                    for(ChessMove move : opponent_moves) {
                        if(move.getEndPosition().equals(kingPosition)) {
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
                ChessPosition testingPosition = new ChessPosition(row, col);
                ChessPiece testingPiece = board.getPiece(testingPosition);
                if(testingPiece != null && testingPiece.getTeamColor() == teamColor){
                    Collection<ChessMove> possibleMoves = validMoves(testingPosition);
                    if(!possibleMoves.isEmpty()) {
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
        //Collection<ChessMove> possibleMoves;
        List<ChessMove> possibleMoves = new ArrayList<>();
        ChessPosition kingPosition = null;
        //go through each square, see if there is a piece and it's the same color, add moves to possibleMoves
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPosition testingPosition = new ChessPosition(row, col);
                ChessPiece testingPiece = board.getPiece(testingPosition);
                if(testingPiece != null && testingPiece.getTeamColor() == teamColor) {
                    if(testingPiece.getPieceType() == ChessPiece.PieceType.KING) {
                        kingPosition = testingPosition;
                    }
                    Collection<ChessMove> moves = validMoves(testingPosition);
                    if(!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        //checking if the king is in immediate danger = false
        List<ChessMove> opponentMoves = new ArrayList<>();
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPosition testingPosition = new ChessPosition(row, col);
                ChessPiece opponentPiece = board.getPiece(testingPosition);
                if(opponentPiece != null && opponentPiece.getTeamColor() != teamColor) {
                    opponentMoves.addAll(opponentPiece.pieceMoves(board, testingPosition));
                }
            }
        }
        //checking to see if the king is pinned
        Collection<ChessMove> king_moves = validMoves(kingPosition);
        if(king_moves.isEmpty()) {
            return true;
        }
        //if king is not in immediate danger, then check to see if there are any moves
        if(possibleMoves.isEmpty() && !opponentMoves.contains(kingPosition)) {
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
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}

