package server;

import chess.*;

public class ServerMain {
    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);
        var server = new Server();
        var port = server.run(8080);
        System.out.println("Server started on port " + port);
    }
}
