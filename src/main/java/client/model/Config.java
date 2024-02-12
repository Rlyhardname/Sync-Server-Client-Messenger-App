package client.model;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Config {
    private InetAddress host;
    private final int PORT = 1337;
    private PrintWriter output;
    private BufferedReader input;
    private DataOutputStream outputFile;
    private DataInputStream inputFile;
    private Socket link;

    //private boolean started;

    public Config() {
        try {
            //setStarted(false);
            host = InetAddress.getLocalHost();
            link = new Socket(host, PORT);
            System.err.println(host);
            input = new BufferedReader(new InputStreamReader(link.getInputStream()));
            output = new PrintWriter(link.getOutputStream(), true);
            outputFile = new DataOutputStream(link.getOutputStream());
            inputFile = new DataInputStream(link.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Host ID not found");
        }

    }

    public InetAddress getHost() {
        return host;
    }

    public void setHost(InetAddress host) {
        this.host = host;
    }

    public int getPORT() {
        return PORT;
    }

    public PrintWriter getOutput() {
        return output;
    }

    public BufferedReader getInput() {
        return input;
    }

    public DataOutputStream getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(DataOutputStream outputFile) {
        this.outputFile = outputFile;
    }

    public DataInputStream getInputFile() {
        return inputFile;
    }

    public void setInputFile(DataInputStream inputFile) {
        this.inputFile = inputFile;
    }

    public Socket getLink() {
        return link;
    }
}
