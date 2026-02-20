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
    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
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
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
//        throw new RuntimeException("Not implemented");
        return pieceType;
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
        List<ChessMove> kingMoves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        int[][] directions = {
                //1 square in any direction including diagonal
                {1,-1},{1,0},{1,1},
                {0,-1},{0,1},
                {-1,-1},{-1,0},{-1,1}

        };
        for (int[] directionTest : directions) {
            int suggestedRow = directionTest[0];
            int suggestedCol = directionTest[1];
            int newRow = suggestedRow + currentRow;
            int newCol = suggestedCol + currentCol;
            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8){
                continue;
            }
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessPiece targetPiece = board.getPiece(newPosition);
            if (targetPiece == null || targetPiece.getTeamColor() != pieceColor) {
                kingMoves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
        return kingMoves;
    }
    //QUEEN
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> queenMoves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        int[][] directions = {
                //move in straight lines and diagonals as far as there is open space
                //can't hard code like king because it's not just one square
                {1,-1},{1,0},{1,1},
                {0,-1},{0,1},
                {-1,-1},{-1,0},{-1,1}
        };
        for (int[] directionTest : directions) {
            int suggestedRow = directionTest[0];
            int suggestedCol = directionTest[1];
            int newRow = suggestedRow + currentRow;
            int newCol = suggestedCol + currentCol;
            while (newRow >= 1 && newRow <=8 && newCol >=1 && newCol <=8){
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(newPosition);
                if (targetPiece == null) {
                    queenMoves.add(new ChessMove(myPosition, newPosition, null));
                } else if (targetPiece.getTeamColor() != pieceColor) {
                    queenMoves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                } else {
                    break;
                }
                newRow += suggestedRow;
                newCol += suggestedCol;
            }

        }

        return queenMoves;
    }
    //BISHOP
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> bishopMoves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        int[][] directions = {
                //move in diagonal lines as long as there is space
               {1,-1},{1,1},{-1,-1},{-1,1}
        };
        for (int[] directionTest : directions){
            int suggestedRow = directionTest[0];
            int suggestedCol = directionTest[1];
            int newRow = suggestedRow + currentRow;
            int newCol = suggestedCol + currentCol;
            while (newRow >= 1 && newRow <=8 && newCol >=1 && newCol <=8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(newPosition);
                if (targetPiece == null) {
                    bishopMoves.add(new ChessMove(myPosition, newPosition, null));
                } else if (targetPiece.getTeamColor() != pieceColor) {
                    bishopMoves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                } else {
                    break;
                }
                newRow += suggestedRow;
                newCol += suggestedCol;
            }

        }
        return bishopMoves;
    }
    //KNIGHT
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> knightMoves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        int[][] directions = {
                //moving 2 squares in one direction and 1 square in the other direction
                {-2,1}, {-2,-1}, {2,1}, {2,-1}, {-1, -2}, {1, -2}, {-1,2}, {1,2}
        };
        for (int[] directionTest : directions) {
            int suggestedRow = directionTest[0];
            int suggestedCol = directionTest[1];
            int newRow = suggestedRow + currentRow;
            int newCol = suggestedCol + currentCol;
            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8){
                continue;
            }
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessPiece targetPiece = board.getPiece(newPosition);
            if (targetPiece == null || targetPiece.getTeamColor() != pieceColor) {
                knightMoves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
        return knightMoves;
    }
    //ROOK
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> rookMoves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        int[][] directions = {
                //straight lines (can be more than just one square
                {1,0}, {0,1}, {-1,0}, {0,-1}
        };
        for (int[] directionTest : directions) {
            int suggestedRow = directionTest[0];
            int suggestedCol = directionTest[1];
            int newRow = suggestedRow + currentRow;
            int newCol = suggestedCol + currentCol;
            while (newRow >= 1 && newRow <=8 && newCol >=1 && newCol <=8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(newPosition);
                if (targetPiece == null) {
                    rookMoves.add(new ChessMove(myPosition, newPosition, null));
                } else if (targetPiece.getTeamColor() != pieceColor) {
                    rookMoves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                } else {
                    break;
                }
                newRow += suggestedRow;
                newCol += suggestedCol;
            }

        }
        return rookMoves;
    }
    //PAWN
    //(a mini method that will add promotion pieces to the list
    private List<ChessMove> promotionMoves(ChessPosition startPosition, ChessPosition endPosition) {
        List<ChessMove> promotionMoves = new ArrayList<>();
        promotionMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
        promotionMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
        promotionMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
        promotionMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
        return promotionMoves;
    }
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        //need to add promotion stuff somewhere
        List<ChessMove> pawnMoves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        //determine direction based off color
        int forward;
        if (pieceColor == ChessGame.TeamColor.WHITE){
            forward = 1;
        } else {
            forward = -1;
        }
        boolean firstMove = (pieceColor == ChessGame.TeamColor.WHITE && currentRow == 2)
                || (pieceColor == ChessGame.TeamColor.BLACK && currentRow == 7);
        int oneForward = currentRow + forward;
        if (oneForward >= 1 && oneForward <= 8) {
            ChessPosition oneForwardPosition = new ChessPosition(currentRow + forward, currentCol);
            ChessPosition twoForwardPositionWhite = new ChessPosition(oneForward+1, currentCol);
            ChessPosition twoForwardPositionBlack = new ChessPosition(oneForward - 1, currentCol);
            ChessPiece pieceAhead = board.getPiece(oneForwardPosition);
            if (pieceAhead == null){
                //check for promotion
                if (pieceColor == ChessGame.TeamColor.WHITE) {
                    if (oneForwardPosition.getRow() == 8) {
                        //promote
                        pawnMoves.addAll(promotionMoves(myPosition, oneForwardPosition));
                    } else {
                        //don't promote
                        pawnMoves.add(new ChessMove(myPosition, oneForwardPosition, null));
                    }
                    //Chceking if first move and adding the two forward option
                    if (firstMove) {
                        if (board.getPiece(twoForwardPositionWhite) == null && board.getPiece(oneForwardPosition) == null) {
                            pawnMoves.add(new ChessMove(myPosition, twoForwardPositionWhite, null));
                        }
                    }
                }
                if (pieceColor == ChessGame.TeamColor.BLACK) {
                    if(oneForwardPosition.getRow() == 1) {
                        pawnMoves.addAll(promotionMoves(myPosition, oneForwardPosition));
                    } else {
                        pawnMoves.add(new ChessMove(myPosition, oneForwardPosition, null));
                    }
                    if(firstMove){
                        if(board.getPiece(twoForwardPositionBlack) == null && board.getPiece(oneForwardPosition) == null) {
                            pawnMoves.add(new ChessMove(myPosition, twoForwardPositionBlack, null));
                        }
                    }
                }
            }
            //step 3: diagonal captures
            int[][] directions;
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                directions = new int[][] {
                        {1,-1}, {1,1}
                };
            } else {
                directions = new int[][] {
                        {-1,-1}, {-1,1}
                };
            }
            for(int[] directionTest : directions) {
                int suggestedRow = directionTest[0];
                int suggestedCol = directionTest[1];
                int newRow = currentRow + suggestedRow;
                int newCol = currentCol + suggestedCol;
                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8){
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece targetPiece = board.getPiece(newPosition);
                    if (targetPiece != null && targetPiece.getTeamColor() != pieceColor) {
                        if((pieceColor == ChessGame.TeamColor.WHITE && newRow == 8) || (pieceColor == ChessGame.TeamColor.BLACK && newRow == 1)) {
                            pawnMoves.addAll(promotionMoves(myPosition, newPosition));
                        } else {
                            pawnMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }
            }

        }

        return pawnMoves;
    }
    //PUTTING IT TOGETHER
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
//        throw new RuntimeException("Not implemented");
        List<ChessMove> chessMoves = new ArrayList<>();
        PieceType currentPiece = getPieceType();
        switch(currentPiece){
            //if it's king, do something. If it's queen, do something else.
            case KING:
                chessMoves.addAll(kingMoves(board, myPosition));
                break;
            case QUEEN:
                chessMoves.addAll(queenMoves(board, myPosition));
                break;
            case BISHOP:
                chessMoves.addAll(bishopMoves(board, myPosition));
                break;
            case KNIGHT:
                chessMoves.addAll(knightMoves(board, myPosition));
                break;
            case ROOK:
                chessMoves.addAll(rookMoves(board, myPosition));
                break;
            case PAWN:
                chessMoves.addAll(pawnMoves(board, myPosition));
                break;
        }
        return chessMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }
}
