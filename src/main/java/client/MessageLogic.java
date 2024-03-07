package client;

import client.model.Config;
import client.model.User;
import common.Command;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class MessageLogic {
    private AppGUI userGUI;
    private LoginGUI login;
    private Config connection;
    private User user;
    private String activeChat;

    MessageLogic() {
        {
            connection = new Config();
        }
    }

    public void handleServer(AppGUI clientGUI) {
        this.userGUI = clientGUI;
        pullFriendsRequest();
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

    private void pullFriendsRequest() {
        sendMessage(Command.PULL_FRIENDS.name());
    }

    private int handleCommands(String[] block) {
        String command = block[0];
        if (command.equals(Command.TEXT_MESSAGE.name())
                || command.equals(Command.RECEIVE_FILE.name())
                || command.equals(Command.LOGIN_SUCCESS.name())) {
            String username = block[1];
            String room = block[2];
            String message = block[3];
            System.out.println("current incoming message: " + Arrays.toString(block));

            if (command.equals(Command.TEXT_MESSAGE.name())) {
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

        if (command.equals(Command.SEARCH_PERSON.name())) {
            String[] friendArray = Arrays.stream(block).filter((x) -> !x.equals(Command.SEARCH_PERSON.name())).toArray(String[]::new);
            userGUI.getSearchBar().removeAllItems();
            DefaultComboBoxModel model = (DefaultComboBoxModel) userGUI.getSearchBar().getModel();
            model.addAll(List.of(friendArray));
            SwingUtilities.invokeLater(() -> userGUI.getSearchBar().setPopupVisible(true));
        }


        if (command.equals(Command.PUSH_FRIENDS.name())) {
            String[] listOfFriends = constructJList(block);
            userGUI.setjList(listOfFriends);
        }

        return -1;
    }

    private String[] constructJList(String... block) {

        List<String> listOfFriends = new LinkedList<>();
        String filler = "                          ";
        ArrayDeque<String> randomizedFriendList = fillArrayDeque(block.length - 1, block);
        while (!randomizedFriendList.isEmpty()) {
            String currentFriend = randomizedFriendList.poll();
            int listItemLength = currentFriend.length();
            listOfFriends.add("    " + currentFriend + filler.substring(0, filler.length() - listItemLength));
        }

        System.out.println("JLIST constructing" + Arrays.toString(listOfFriends.toArray()));
        return listOfFriends.toArray(new String[0]);
    }

    private ArrayDeque<String> fillArrayDeque(int length, String[] block) {
        ArrayDeque<String> randomizedFriendList = new ArrayDeque<>();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            String currentFriend = block[i + 1];
            int randNumber;

            if (currentFriend.contains("|")) {
                String[] roomNumRoomName = currentFriend.split("\\|");
                int id = Integer.parseInt(roomNumRoomName[0]);
                userGUI.getRooms().putIfAbsent(roomNumRoomName[1], id);
                if (rand.nextInt(21) < 4) {
                    randomizedFriendList.push(roomNumRoomName[1]);
                } else {
                    randomizedFriendList.add(roomNumRoomName[1]);
                }

                continue;
            }

            if (currentFriend.codePointAt(0) == 128473) {
                randNumber = rand.nextInt(0, 4);
            } else {
                randNumber = rand.nextInt(4, 9);
            }

            if (randNumber < 3) {
                randomizedFriendList.push(currentFriend);
            } else {
                randomizedFriendList.add(currentFriend);
            }

        }


        return randomizedFriendList;
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

    public String getActiveChat() {
        return activeChat;
    }

}
