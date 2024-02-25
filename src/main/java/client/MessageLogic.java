package client;

import client.model.Config;
import client.model.User;
import common.Command;

import java.io.*;
import java.util.Arrays;

public class MessageLogic {
    private AppGUI userGUI;
    private LoginGUI login;
    private Config connection;
    private User user;

    MessageLogic() {
        {
            connection = new Config();
        }
    }

    public void handleServer(AppGUI clientGUI) {
        this.userGUI = clientGUI;
        do {
            try {
                BufferedReader input = connection.getInput();
                String line = "";
                while ((line = input.readLine()) != null) {
                    System.out.println(line.length());
                    if (line.equals("")) {
                        continue;
                    }

                    String[] pack = line.split(",");
                    if (pack.length == 1) {
                        continue;
                    }

                    handleCommands(pack);
                }

            } catch (IOException e) {
                System.out.println("exception 1");
                e.printStackTrace();
                break;
            } catch (NullPointerException e1) {
                System.out.println("exception 2");
                e1.printStackTrace();
                break;
            }

        } while (true);
    }

    private int handleCommands(String[] block) {
        String command = block[0];
        if (block.length == 4) {
            String username = block[1];
            String room = block[2];
            String message = block[3];
            System.out.println("current incoming message: " + Arrays.toString(block));

            if (command.equals(Command.TEXT_MESSAGE.name())) {
                System.out.println("at least we know we received it..");
                userGUI.concatArea(username + ": " + message);
                return 1;
            }

            if (command.equals(Command.RECEIVE_FILE.name())) {
                String path = FileTransfer.pickDirectory();
                if (!path.equals("") || !path.equals(null)) {
                    FileTransfer.receiveFile(path, connection);
                    userGUI.concatArea(username + ": sending file " + message);
                    return 1;
                }

            }

            if (command.equals(Command.LOGIN_SUCCESS.name())) {
                System.out.println("successfully logged in!");
                return 1;
            }
        }

        if (command.equals(Command.PUSH_FRIENDS.name())) {
            String[] listOfFriends = constructJList(block);
            userGUI.setjList(listOfFriends);
        }

        return -1;
    }

    private String[] constructJList(String... block) {
        String[] listOfFriends = new String[block.length - 1];
        String filler = "                          ";
        int fillerLength = filler.length();
        for (int i = 0; i < listOfFriends.length; i++) {
            int listItemLength = block[i+1].length();
            listOfFriends[i] = "    "+block[i+1]+filler.substring(0,fillerLength-listItemLength);
        }

        return listOfFriends;
    }

    boolean accessServer(String command, String username, String password) {
        loginMessage(command, username, password);
        if (isLoginSuccess()) {
            user = new User(username, password);
            return true;
        }

        return false;
    }

    public boolean isLoginSuccess() {
        String[] received = receiveMessage();
        System.out.println(" received commands " + Arrays.toString(received));
        if (received[0].equals(Command.LOGIN_SUCCESS.name())) {
            return true;
        }
        if (received[0].equals(Command.REGISTER_SUCCESS.name())) {
            return true;
        }

        return false;
    }

    public void sendMessage(String message) {
        connection.getOutput().println(message);
    }

    public String[] receiveMessage() {
        String[] arr;
        try {
            arr = connection.getInput().readLine().split(",");
            return arr;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String[0];
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
        String action = command.equals(Command.LOGIN.name()) ? Command.LOGIN.name() + "," + msg : Command.SIGN_UP.name() + "," + msg;
        System.out.println("Command " + action);
        sendMessage(action);
    }

    public AppGUI getUserGUI() {
        return userGUI;
    }

    public LoginGUI getLogin() {
        return login;
    }

    public Config getConnection() {
        return connection;
    }

    public User getUser() {
        return user;
    }

}
