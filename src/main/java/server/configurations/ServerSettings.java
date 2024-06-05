package server.configurations;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSettings {
    private static ServerSettings serverSettings;
    private final int PORT = 1337;
    private ServerSocket serverSocket;

    private ServerSettings() throws IOException {
        serverSocket = new ServerSocket(PORT);
    }

    public static ServerSettings instanceOf() {
        return serverSettings;
    }


    public static void startServer(){
        try {
            if (serverSettings == null) {
                serverSettings = new ServerSettings();
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public Socket acceptConnection() throws IOException {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Socket closeConnection() throws IOException {
        return serverSocket.accept();
    }

}
