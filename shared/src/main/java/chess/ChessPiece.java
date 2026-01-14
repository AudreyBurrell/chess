package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor piece_color;
    ChessPiece.PieceType piece_type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.piece_color = pieceColor;
        this.piece_type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
//        throw new RuntimeException("Not implemented");
        return piece_color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
//        throw new RuntimeException("Not implemented");
        return piece_type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    //KING
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
//        List<ChessMove> king_moves = new ArrayList<>();
//        int current_row = myPosition.getRow();
//        int current_col = myPosition.getColumn();
//        int[][] directions = {
//                //1 square in any direciton, including diagonal
//                {current_row - 1, current_col - 1}, {current_row - 1, current_col}, {current_row - 1, current_col + 1},
//                {current_row, current_col - 1}, {current_row, current_col + 1},
//                {current_row + 1, current_col - 1}, {current_row + 1, current_col}, {current_row + 1, current_col + 1}
//        };
//        //determining which directions are actually valid
//        for (int[] square : directions) {
//            int new_row = square[0];
//            int new_col = square[1];
//            if (new_row < 1 || new_row > 8 || new_col < 1 || new_col > 8){
//                continue; //going to the next square
//            }
//            ChessPosition new_position = new ChessPosition(new_row, new_col);
//            ChessPiece target_piece = board.getPiece(new_position);
//            if (target_piece == null || target_piece.getTeamColor() != piece_color){
//                king_moves.add(new ChessMove(myPosition, new_position, null));
//            }
//        }
//        return king_moves;
        List<ChessMove> king_moves = new ArrayList<>();
        int current_row = myPosition.getRow();
        int current_col = myPosition.getColumn();
        int[][] directions = {
                //1 square in any direction including diagonal
                {1,-1},{1,0},{1,1},
                {0,-1},{0,1},
                {-1,-1},{0,-1},{-1,1}
        };
        for (int[] direction_test : directions) {
            int suggested_row = direction_test[0];
            int suggested_col = direction_test[1];
            int new_row = suggested_row + current_row;
            int new_col = suggested_col + current_col;
            if (new_row < 1 || new_row > 8 || new_col < 1 || new_col > 8){
                continue;
            }
            ChessPosition new_position = new ChessPosition(new_row, new_col);
            ChessPiece target_piece = board.getPiece(new_position);
            if (target_piece == null || target_piece.getTeamColor() != piece_color) {
                king_moves.add(new ChessMove(myPosition, new_position, null));
            }
        }
        return king_moves;
    }
    //QUEEN
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> queen_moves = new ArrayList<>();
        int current_row = myPosition.getRow();
        int current_col = myPosition.getColumn();
        int[][] directions = {
                //move in straight lines and diagonals as far as there is open space
                //can't hard code like king because it's not just one square
                {1, 0}, {-1,0}, {0,1},{0,-1},{1,1}, {1,-1},{-1,1},{-1,1}
        };
        for (int[] direction_test : directions) {
            int suggested_row = direction_test[0];
            int suggested_col = direction_test[1];
            int new_row = suggested_row + current_row;
            int new_col = suggested_col + current_col;
            while (new_row >= 1 && new_row <=8 && new_col >=1 && new_col <=8){
                ChessPosition new_position = new ChessPosition(new_row, new_col);
                ChessPiece target_piece = board.getPiece(new_position);
                if (target_piece == null || target_piece.getTeamColor() != piece_color) {
                    queen_moves.add(new ChessMove(myPosition, new_position, null));
                    break;
                } else {
                    break;
                }
            }
            new_row += suggested_row;
            new_col += suggested_col;
        }

        return queen_moves;
    }
    //PUTTING IT TOGETHER
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
//        throw new RuntimeException("Not implemented");
        List<ChessMove> chess_moves = new ArrayList<>();
        PieceType current_piece = getPieceType();
        switch(current_piece){
            //if it's king, do something. If it's queen, do something else.
            case KING:
                chess_moves.addAll(kingMoves(board, myPosition));
                break;
            case QUEEN:
                chess_moves.addAll(queenMoves(board, myPosition));
                break;
            case BISHOP:
                //stuff goes here
                break;
            case KNIGHT:
                //stuff goes here
                break;
            case ROOK:
                //stuff goes here
                break;
            case PAWN:
                //stuff goes here
                break;
        }
        return chess_moves;
        //return List.of(); //just having this here so I can see which tests I am passing
    }
}
