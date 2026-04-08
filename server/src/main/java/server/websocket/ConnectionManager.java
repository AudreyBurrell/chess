package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();
    public void add(int gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet());
        connections.get(gameID).add(session);
    }
    public void remove(int gameID, Session session) {
        if (connections.containsKey(gameID)) {
            connections.get(gameID).remove(session);
        }
    }
    public void broadcast(int gameID, Session excludeSession, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        var sessions = connections.get(gameID);
        if (sessions != null) {
            for (Session session : sessions) {
                if (session.isOpen() && !session.equals(excludeSession)) {
                    session.getRemote().sendString(msg);
                }
            }
        }

    }
}
