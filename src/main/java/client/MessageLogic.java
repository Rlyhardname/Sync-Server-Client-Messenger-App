package client;

import client.model.Config;
import client.model.User;
import common.Command;

import java.io.*;
import java.util.Arrays;

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
           // this.clientGUI = clientGUI;
           // handleServer();
    }

    public void handleServer(AppGUI clientGUI) {
        this.clientGUI = clientGUI;
        do {
            try {
                BufferedReader input =  connection.getInput();
                String line = "";
                while ((line = input.readLine())!=null){
                    if(line.equals("")){
                        continue;
                    }
                    String[] pack = line.split(",");
                    String command = pack[0];
                    String username = pack[1];
                    String room = pack[2];
                    String message = pack[3];

                if(command.equals(Command.LOGIN_SUCCESS.name())){
                    System.out.println("successfully logged in!");
                    continue;
                }
                    if(command.equals(Command.TEXT_MESSAGE.name())){
                        System.out.println("at least we know we received it..");
                        clientGUI.concatArea(username+ ": "+ message);
                        continue;
                    }
                }

                if (line == null) {
                    System.out.println("handle server failed...");
                    // TODO logout or some way to handle
                    continue;
                }





                System.out.println("message length " + line.length() + line);
                // TODO why is this here?
//                if (received.length < 4) {
//                    continue;
//                }

//                if (received[3].equals(Command.RECEIVE_FILE.name())) {
//                    String path = FileTransfer.pickDirectory();
//                    FileTransfer.receiveFile(path, connection);
//                    clientGUI.concatArea(Arrays.toString(received));
//                } else if (received[3].equals(Command.TEXT_MESSAGE.name())) {
//                    clientGUI.concatArea(received[0]);
//                }

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

//    handleCommands(String[] block){
//        if(block)
//    }

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
        System.out.println(" received commands "  + Arrays.toString(received));
        if (received[0].equals(Command.LOGIN_SUCCESS.name())) {
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
        System.out.println("Command " + action );
        connection.getOutput().println(action);
    }

    public AppGUI getClientGUI() {
        return clientGUI;
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
