package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSettings {
    public static final int PORT = 1337;
    public static ServerSocket serverSocket;

    static {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ConcurrentHashMap<String, Socket> onlineUsers = new ConcurrentHashMap<String, Socket>();

}
