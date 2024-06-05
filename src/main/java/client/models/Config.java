package client.models;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Config {
    public static final int PORT = 1337;
    private InetAddress hostIP;
    private BufferedReader messageInput;
    private PrintWriter messageOutput;
    private DataOutputStream fileOutput;
    private DataInputStream fileInput;
    private Socket link;

    public Config() {
        try {
            hostIP = InetAddress.getLocalHost();
            link = new Socket(hostIP, PORT);
            System.err.println("HOST NAME " + hostIP);
            messageInput = new BufferedReader(new InputStreamReader(link.getInputStream()));
            messageOutput = new PrintWriter(link.getOutputStream(), true);
            fileInput = new DataInputStream(link.getInputStream());
            fileOutput = new DataOutputStream(link.getOutputStream());
            System.err.println("New Connection is running");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Host ID not found");
        }

    }

    public PrintWriter getMessageOutput() {
        return messageOutput;
    }

    public BufferedReader getMessageInput() {
        return messageInput;
    }

    public DataOutputStream getFileOutput() {
        return fileOutput;
    }

    public DataInputStream getFileInput() {
        return fileInput;
    }

    public Socket getLink() {
        return link;
    }
}
