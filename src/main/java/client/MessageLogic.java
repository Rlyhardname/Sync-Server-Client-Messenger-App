package client;

import client.model.Config;
import client.model.User;
import common.Command;
import server.ServerSettings;

import java.io.*;

public class MessageLogic {
    private AppGUI clientGUI;
    private LoginGUI login;
    private Config connection;
    private User user;

    MessageLogic() {
        {
            connection = new Config();
        }
    }

    public void runHandleServer() {
        if (!ServerSettings.onlineUsers.isEmpty()) {
            handleServer();
        }
    }

    public void handleServer() {
        do {
            try {
                String received = connection.getInput().readLine();
                if (received == null) {
                    break;
                }

                String[] splitMessage = received.split(",");
                // TODO why is this here?
                if (splitMessage.length < 4) {
                    continue;
                }


                if (splitMessage[3].equals(Command.RECEIVE_FILE.name())) {
                    String path = FileTransfer.pickDirectory();
                    FileTransfer.receiveFile(path, connection);
                    clientGUI.concattArea(received);
                } else if (splitMessage[3].equals(Command.TEXT_MESSAGE.name())) {
                    clientGUI.concattArea(splitMessage[0]);
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (NullPointerException e1) {
                e1.printStackTrace();
                break;
            }

        } while (true);
    }

    boolean accessServer(String string, String username, String password) {
        loginMessage(string, username, password);
        if (isLoginSuccess()) {
            user = new User(username, password);
            return true;
        }
        return false;
    }

    public boolean isLoginSuccess() {
        String received = receiveMessage();
        if (received.equals(Command.LOGIN_SUCCESS.name()+",Successfully logged in!")) {
            return true;
        }
        System.out.println(received);
        return false;

    }

    public void sendMessage(String message) {
        connection.getOutput().println(message);
    }

    public String receiveMessage() {
        try {
            String msg = connection.getInput().readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean login() {
        do {
            try {
                String serverMsg = connection.getInput().readLine();
                if (serverMsg.equals("Username")) {
                    System.out.println(serverMsg);
                    connection.getOutput().println(user.getUsername());
                } else if (serverMsg.equals("Password")) {
                    System.out.println(serverMsg);
                    connection.getOutput().println(user.getPassword());
                } else if (serverMsg.equals(Command.LOGIN_SUCCESS.name() + "," + "Successfully logged in!")) {
                    System.out.println(serverMsg);
                    break;
                } else {
                    System.out.println(serverMsg);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } while (true);

        return true;
    }

    public StringBuffer concatStrings(String... data) {
        StringBuffer concat = new StringBuffer();
        for (String string : data) {
            concat.append(string);
            concat.append(",");
        }

        concat.deleteCharAt(concat.length() - 1);
        return concat;
    }

    public void loginMessage(String command, String user, String pass) {
        String msg = concatStrings(user, pass).toString();
        String action = command.equals("login") ? "LOGIN" + "," + msg : "SIGN UP" + "," + msg;
        connection.getOutput().println(action);
    }


    // TODO does this make sense?


    public AppGUI getClientGUI() {
        return clientGUI;
    }

    public void setClientGUI(AppGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    public LoginGUI getLogin() {
        return login;
    }

    public void setLogin(LoginGUI login) {
        this.login = login;
    }

    public Config getConnection() {
        return connection;
    }

    public void setConnection(Config connection) {
        this.connection = connection;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
