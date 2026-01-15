package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

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
        List<ChessMove> king_moves = new ArrayList<>();
        int current_row = myPosition.getRow();
        int current_col = myPosition.getColumn();
        int[][] directions = {
                //1 square in any direction including diagonal
                {1,-1},{1,0},{1,1},
                {0,-1},{0,1},
                {-1,-1},{-1,0},{-1,1}

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
                {1,-1},{1,0},{1,1},
                {0,-1},{0,1},
                {-1,-1},{-1,0},{-1,1}
        };
        for (int[] direction_test : directions) {
            int suggested_row = direction_test[0];
            int suggested_col = direction_test[1];
            int new_row = suggested_row + current_row;
            int new_col = suggested_col + current_col;
            while (new_row >= 1 && new_row <=8 && new_col >=1 && new_col <=8){
                ChessPosition new_position = new ChessPosition(new_row, new_col);
                ChessPiece target_piece = board.getPiece(new_position);
                if (target_piece == null) {
                    queen_moves.add(new ChessMove(myPosition, new_position, null));
                } else if (target_piece.getTeamColor() != piece_color) {
                    queen_moves.add(new ChessMove(myPosition, new_position, null));
                    break;
                } else {
                    break;
                }
                new_row += suggested_row;
                new_col += suggested_col;
            }

        }

        return queen_moves;
    }
    //BISHOP
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> bishop_moves = new ArrayList<>();
        int current_row = myPosition.getRow();
        int current_col = myPosition.getColumn();
        int[][] directions = {
                //move in diagonal lines as long as there is space
               {1,-1},{1,1}, {-1,-1}, {-1,1}
        };
        for (int[] direction_test : directions){
            int suggested_row = direction_test[0];
            int suggested_col = direction_test[1];
            int new_row = suggested_row + current_row;
            int new_col = suggested_col + current_col;
            while (new_row >= 1 && new_row <=8 && new_col >=1 && new_col <=8) {
                ChessPosition new_position = new ChessPosition(new_row, new_col);
                ChessPiece target_piece = board.getPiece(new_position);
                if (target_piece == null) {
                    bishop_moves.add(new ChessMove(myPosition, new_position, null));
                } else if (target_piece.getTeamColor() != piece_color) {
                    bishop_moves.add(new ChessMove(myPosition, new_position, null));
                } else {
                    break;
                }
                new_row += suggested_row;
                new_col += suggested_col;
            }

        }
        return bishop_moves;
    }
    //KNIGHT
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> knight_moves = new ArrayList<>();
        int current_row = myPosition.getRow();
        int current_col = myPosition.getColumn();
        int[][] directions = {
                //moving 2 squares in one direction and 1 square in the other direction
                {-2,1}, {-2,-1}, {2,1}, {2,-1}, {-1, -2}, {1, -2}, {-1,2}, {1,2}
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
                knight_moves.add(new ChessMove(myPosition, new_position, null));
            }
        }
        return knight_moves;
    }
    //ROOK
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> rook_moves = new ArrayList<>();
        int current_row = myPosition.getRow();
        int current_col = myPosition.getColumn();
        int[][] directions = {
                //straight lines (can be more than just one square
                {1,0}, {0,1}, {-1,0}, {0,-1}
        };
        for (int[] direction_test : directions) {
            int suggested_row = direction_test[0];
            int suggested_col = direction_test[1];
            int new_row = suggested_row + current_row;
            int new_col = suggested_col + current_col;
            while (new_row >= 1 && new_row <=8 && new_col >=1 && new_col <=8) {
                ChessPosition new_position = new ChessPosition(new_row, new_col);
                ChessPiece target_piece = board.getPiece(new_position);
//                if (target_piece == null || target_piece.getTeamColor() != piece_color) {
//                    rook_moves.add(new ChessMove(myPosition, new_position, null));
//                } else {
//                    break;
//                }
                if (target_piece == null) {
                    rook_moves.add(new ChessMove(myPosition, new_position, null));
                } else if (target_piece.getTeamColor() != piece_color) {
                    rook_moves.add(new ChessMove(myPosition, new_position, null));
                    break;
                } else {
                    break;
                }
                new_row += suggested_row;
                new_col += suggested_col;
            }

        }
        return rook_moves;
    }
    //PAWN
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> pawn_moves = new ArrayList<>();
        int current_row = myPosition.getRow();
        int current_col = myPosition.getColumn();
        //determine direction based off color
        int forward;
        if (piece_color == ChessGame.TeamColor.WHITE){
            forward = 1;
        } else {
            forward = -1;
        }
        //determining if it's the piece's first move based on if it's currently in starting position
        boolean first_move = (piece_color == ChessGame.TeamColor.WHITE && current_row == 2) ||
                (piece_color == ChessGame.TeamColor.BLACK && current_row == 7);
        //getting the options for ways to move
        int[][] directions;
        if (first_move){
            directions = new int[][]{
                    {2*forward, 0}, {forward, 0}, {forward, -1}, {forward, 1}
            };
        } else{
            directions = new int[][]{
                    {forward, 0}, {forward, -1}, {forward, 1}
            };
        }
        //making sure they are valid and adding to list
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
                pawn_moves.add(new ChessMove(myPosition, new_position, null));
            }
        }
        return pawn_moves;
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
                chess_moves.addAll(bishopMoves(board, myPosition));
                break;
            case KNIGHT:
                chess_moves.addAll(knightMoves(board, myPosition));
                break;
            case ROOK:
                chess_moves.addAll(rookMoves(board, myPosition));
                break;
            case PAWN:
                chess_moves.addAll(pawnMoves(board, myPosition));
                break;
        }
        return chess_moves;
        //return List.of(); //just having this here so I can see which tests I am passing
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return piece_color == that.piece_color && piece_type == that.piece_type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece_color, piece_type);
    }
}
