package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        var repl = new ChessRepl(8080);
        repl.run();
    }
}
