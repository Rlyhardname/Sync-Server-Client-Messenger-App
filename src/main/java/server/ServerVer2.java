package server;

import client.models.User;
import common.Command;
import server.dao.*;
import server.dao.AuthDAO;
import server.dao.AuthenticationDAO;
import server.util.Util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ServerVer2 {
    private User user;
    private Socket link;
    private PrintWriter textOutput;
    private BufferedReader textInput;
    private DataOutputStream fileOutput;
    private DataInputStream fileInput;


    ServerVer2() {
        if (hasInitialized()) {
            connectClient();
        }
    }

    private boolean hasInitialized() {
        try {
            link = ServerSettings.serverSocket.accept();
            System.out.println("server created!");
            textInput = new BufferedReader(new InputStreamReader(link.getInputStream()));
            textOutput = new PrintWriter(link.getOutputStream(), true);
            fileInput = new DataInputStream(link.getInputStream());
            fileOutput = new DataOutputStream(link.getOutputStream());
        } catch (IOException e1) {
            try {
            } catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            }
            e1.printStackTrace();
            return false;
        } finally {
            new Thread(() -> {
                new ServerVer2();
            }).start();
        }
        return true;
    }

    void connectClient() {
        try {
            System.out.println("Client Connected");
            authentication();
            syncClientWithServerDB();
            handleClient();

        } catch (NullPointerException e1) {
            e1.printStackTrace();
        }
    }

    private void authentication() {
        while (true) {
            String[] commandUserPass;
            String msg = "";
            try {
                msg = textInput.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e1) {
                e1.printStackTrace();
                break;
            }

            concatIncomingMessage(msg);
            commandUserPass = msg.split(",");

            try {
                user = new User(commandUserPass[1], commandUserPass[2]);
            } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                e.printStackTrace();
                // TODO add exception handling
                sendMessage("UsernameException" + "," + "sorry");
                continue;
            }

            System.out.println("Client returned username : " + user.username());
            if (commandUserPass[0].equals(Command.LOGIN.name())) {
                if (!loginUserExists()) {
                    continue;
                }

                if (!correctLoginInfo()) {
                    continue;
                }

                login();
                StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
                storageDAO.logUserLogin(user.username());
                break;
            } else if (commandUserPass[0].equals(Command.SIGN_UP.name())) {
                try {
                    isAccountCreated();
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();

                }

            }
        }

    }

    private void syncClientWithServerDB() {
        StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
        String[] batch = storageDAO.fetchAllByNameUnsentMessages(user.username());
        if (ServerSettings.onlineUsers.get(user.username()) != null) {
            for (String string : batch) {
                sendMessage(string);
            }

            storageDAO.updateUserLogMessageSent(user.username(), 1);
        }
    }

    // TODO merge the two functions to disguise internal server logic
    private boolean loginUserExists() {
        AuthDAO dao = new AuthenticationDAO(DataSourcePool.instanceOf());
        boolean condition = dao.isUserRegistered(user.username());
        if (!condition) {
            String msg = Command.LOGIN_FAIL.name() + "," + "There is no user: " + user.username() + " in our databases!";
            sendMessage(msg);
            return false;
        }

        return true;
    }

    private boolean correctLoginInfo() {
        AuthDAO dao = new AuthenticationDAO(DataSourcePool.instanceOf());
        if (!dao.passwordIsCorrect(user.username(), user.password())) {
            String msg = Command.LOGIN_FAIL.name() + "," + "Password doesn't match for username " + user.username();
            sendMessage(msg);
            return false;
        }
        return true;
    }

    private void login() {
        System.out.println("login message? " + Command.LOGIN_SUCCESS.name());
        String msg = Command.LOGIN_SUCCESS.name() + "," + "Successfully logged in!";
        sendMessage(msg);
        ServerSettings.onlineUsers.put(user.username(), this);

    }

    private boolean isAccountCreated() throws IOException {
        do {
            if (!userDataIsValid(20, user.username())) {
                return false;
            }

            AuthDAO authDAO = new AuthenticationDAO(DataSourcePool.instanceOf());
            if (authDAO.isUserRegistered(user.username())) {
                sendMessage(Command.NICKNAME_UNAVAILABLE.name());
                return false;
            }

            if (!userDataIsValid(32, user.password())) {
                return false;
            }

            StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
            Optional<User> userOpt = storageDAO.createUser(user.username(), user.password());
            if (userOpt.isPresent()) {
                String msg = Command.REGISTER_SUCCESS.name() + "," + "Account Successfully created!";
                sendMessage(msg);
            } else {
                String msg = Command.REGISTER_FAIL.name() + "," + "Database error, try again!";
                sendMessage(msg);
            }

            return true;
        } while (true);

    }

    private boolean userDataIsValid(int typeLength, String data) {
        if (data.length() > typeLength) {
            String msg = Command.TOO_MANY_CHARACTERS.name() + "," + data + "," + "Is too long!";
            sendMessage(msg);
            return false;
        }

        String[] forbiddenSymbols = {"#", "$", ",", "%", "!", "@", "^", "*", "(", ")", "+", "{", "}", "[", "]", "'",
                "\"", " Insert ", " Update ", " Delete "};
        for (String string : forbiddenSymbols) {
            if (data.contains(string)) {
                String msg = Command.FORBIDDEN_SYMBOL.name() + "," + data + "," + "Contrains forbidden symbol!" + ","
                        + string;
                sendMessage(msg);
                return false;
            }

        }

        return true;
    }

    /**
     * <pre>
     *      * userMsg[0] - msgType;
     *      * userMsg[1] - username;
     *      * userMsg[2] - Chat_room_ID;
     *      * userMsg[3] = message;
     *
     * userMsg[0] - message;
     * userMsg[1] - username;
     * userMsg[2] - Chat_room_ID;
     * userMsg[3] = "msgType";
     * </pre>
     */

    private void handleClient() {
        String msg = "";
        do {
            try {
                msg = textInput.readLine();
                System.out.println(Thread.currentThread());
                System.out.println("message + " + msg);

                if (Objects.isNull(msg)) {
                    break;
                }

                if ((msg.startsWith(",") || msg.equals(""))) {
                    continue;
                }

                String[] userMsg = msg.split(",");

                if (userMsg[0].equals(Command.PULL_FRIENDS.name())) {
                    StorageDAO DAO = new StorageDAOImpl(DataSourcePool.instanceOf());
                    Util.pullFriends(DAO, user.username());
                    continue;
                }

                if (userMsg[0].equals(Command.SEARCH_PERSON.name())) {
                    StorageDAO DAO = new StorageDAOImpl(DataSourcePool.instanceOf());
                    List<String> fetchResults = DAO.fetchSearchResults(userMsg[1]);
                    fetchResults.add(0, Command.SEARCH_PERSON.name());
                    String result = String.join(",", fetchResults);
                    System.out.println("print search result " + result);
                    sendMessage(result);
                    continue;
                }

                if (userMsg[0].equals(Command.ACCEPT_FRIEND.name())) {
                    String sender = userMsg[1];
                    StorageDAO DAO = new StorageDAOImpl(DataSourcePool.instanceOf());
                    if(!DAO.isFriends(user.username(),sender)){
                        // create new chat_room
                        // generate name(from the two people's names concatenated eg. Acc1|Acc2, add 2 users in room by default
                        DAO.createChatRoom(sender,user.username());
                    }



                    // add both users to chat_room_warehouse with the new ID created(probably best doing that with trigger)
                    // eather by trigger or programatically add them also to friends table, with the id of the room and the default room name
                    // on next server push update the user with new friend/or do individual push only for the 2 users.
                    DAO.saveContact(user.username(), userMsg[1]);
                    DAO.saveContact(userMsg[1], user.username());
                    //sendMessage(result);
                    continue;
                }


                String command = userMsg[0];
                String username = userMsg[1];
                int room = Integer.parseInt(userMsg[2]);
                System.out.println("current room " + room);
                String message = userMsg[3];

                if (command.equals(Command.CLOSING_CONNECTION.name())) {
                    ServerSettings.onlineUsers.remove(username);
                    break;
                }

                if (command.equals(Command.SEND_FILE.name())) { // SEND FILE logic
                    reSendFile(room, username, message); // message = file name in this case;
                }

                if (isTextMessage(command)) {
                    System.err.println("server receices" + Arrays.toString(userMsg));
                    // SEND MSG LOGIC
                    StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
                    boolean isUserAuthorizedInRoom = storageDAO.isUserAuthorizedInRoom(username, message, room);
                    if (!isUserAuthorizedInRoom) {
                        continue;
                    }

                    storageDAO.saveMessage(username, message, room);
                    sendMsgOnlineRoomUsers(room, username, message);
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (NullPointerException e1) {
                e1.printStackTrace();
            }

        } while (true);

        StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
        storageDAO.logUserLogout(user.username());
        try {
            textOutput.close();
            textInput.close();
            link.close();
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean isTextMessage(String msg) {
        if (msg.equals(Command.TEXT_MESSAGE.name())) {
            return true;
        }
        return false;
    }

    private void sendMsgOnlineRoomUsers(int room, String user, String message) {
        StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
        String[] users = storageDAO.getRoomUsers(room);
        for (String roomUser : users) {
            if (ServerSettings.onlineUsers.get(roomUser) != null) {
                ServerSettings.onlineUsers.get(roomUser).getTextOutput().println(Command.TEXT_MESSAGE.name() + "," + user + "," + room + "," + message);
            } else {
                storageDAO.updateUserLogMessageSent(roomUser, 0);
            }

        }

    }

    private void sendMessage(String msg) {
        textOutput.println(msg);
    }

    private void reSendFile(int room, String usernameSendingFile, String fileName) {
        StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
        String[] usersInRoom = storageDAO.getRoomUsers(room);
        for (String user : usersInRoom) {
            ServerVer2 onlineUser = ServerSettings.onlineUsers.get(user);
            if (onlineUser != null && !usernameSendingFile.toLowerCase().equals(user)) {
                int bytes = 0;
                onlineUser.getTextOutput().println(Command.RECEIVE_FILE.name() + "," + usernameSendingFile + "," + room + "," + fileName);
                DataOutputStream sendFile = onlineUser.getFileOutput();
                try {
                    byte[] buffer = new byte[4 * 1024];
                    long size = fileInput.readLong();
                    while ((size > 0 && (bytes = fileInput.read(buffer)) != -1)) {
                        sendFile.write(buffer, 0, bytes);
                        System.out.println("size " + size + " Buffer " + buffer);
                        size -= bytes;
                        sendFile.flush();
                        System.out.println("start sending file... ");
                        if (bytes < 4096) {
                            break;
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        sendFile.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

        }

    }

    private void concatIncomingMessage(String str) {
        ServerGUI.textArea.append(str);
    }

    public static synchronized void printActiveUsers() {
        ServerGUI.printArea();
    }

    public User getUser() {
        return user;
    }

    public Socket getLink() {
        return link;
    }

    public PrintWriter getTextOutput() {
        return textOutput;
    }

    public BufferedReader getTextInput() {
        return textInput;
    }

    public DataOutputStream getFileOutput() {
        return fileOutput;
    }

    public DataInputStream getFileInput() {
        return fileInput;
    }
}
