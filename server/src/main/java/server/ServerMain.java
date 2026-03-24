package server;

import chess.*;

public class ServerMain {
    public static void main(String[] args) {
        var server = new Server();
        var port = server.run(8080);
        System.out.println("Server started on port " + port);
    }
}
