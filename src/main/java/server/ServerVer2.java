package server;

import client.model.User;
import common.Command;
import server.dao.*;
import server.dao.AuthDAO;
import server.dao.AuthenticationDAO;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Optional;

public class ServerVer2 {
    private User user;
    private Socket link;
    private PrintWriter textOutput;
    private BufferedReader textInput;
    private DataOutputStream fileOutput;
    private DataInputStream fileInput;


    ServerVer2() {
        hasInitialized();
        connectClient();
    }

    private boolean hasInitialized() {
        try {
            link = ServerSettings.serverSocket.accept();
            System.out.println("server created!");
            textOutput = new PrintWriter(link.getOutputStream(), true);
            textInput = new BufferedReader(new InputStreamReader(link.getInputStream()));
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
            // syncClientWithServerDB();
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

            System.out.println("Client returned username : " + user.getUsername());
            if (commandUserPass[0].equals(Command.LOGIN.name())) {
                if (!loginUserExists()) {
                    continue;
                }

                if (!correctLoginInfo()) {
                    continue;
                }

                login();
                StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
                storageDAO.logUserActivity(user.getUsername());
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
        String[] batch = storageDAO.fetchAllByNameUnsentMessages(user.getUsername());
        if (ServerSettings.onlineUsers.get(user.getUsername()) != null) {
            for (String string : batch) {
                sendMessage(string);
            }

        }
    }

    // TODO merge the two functions to disguise internal server logic
    private boolean loginUserExists() {
        AuthDAO dao = new AuthenticationDAO(DataSourcePool.instanceOf());
        boolean condition = dao.isUserRegistered(user.getUsername());
        if (!condition) {
            String msg = Command.LOGIN_FAIL.name() + "," + "There is no user: " + user.getUsername() + " in our databases!";
            sendMessage(msg);
            return false;
        }

        return true;
    }

    private boolean correctLoginInfo() {
        AuthDAO dao = new AuthenticationDAO(DataSourcePool.instanceOf());
        if (!dao.passwordIsCorrect(user.getUsername(), user.getPassword())) {
            String msg = Command.LOGIN_FAIL.name() + "," + "Password doesn't match for username " + user.getUsername();
            sendMessage(msg);
            return false;
        }
        return true;
    }

    private void login() {
        System.out.println("login message? " + Command.LOGIN_SUCCESS.name());
        String msg = Command.LOGIN_SUCCESS.name() + "," + "Successfully logged in!";
        sendMessage(msg);
        ServerSettings.onlineUsers.put(user.getUsername(), this);

    }

    private boolean isAccountCreated() throws IOException {
        do {
            if (!userDataIsValid(20, user.getUsername())) {
                return false;
            }

            AuthDAO authDAO = new AuthenticationDAO(DataSourcePool.instanceOf());
            if (authDAO.isUserRegistered(user.getUsername())) {
                sendMessage(Command.NICKNAME_UNAVAILABLE.name());
                return false;
            }

            if (!userDataIsValid(32, user.getPassword())) {
                return false;
            }

            StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
            Optional<User> userOpt = storageDAO.createUser(user.getUsername(), user.getPassword());
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
                if ((msg == null) || (msg.startsWith(",") || msg.equals(""))) {
                    continue;
                }
                String[] userMsg = msg.split(",");
                String command = userMsg[0];
                String username = userMsg[1];
                int room = Integer.parseInt(userMsg[2]);
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
                    //      new Thread(() -> {
                    // SEND MSG LOGIC
                    StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
                    boolean isMessageStored = storageDAO.storeMessage(username, message, room);
                    if (isMessageStored) {
                        sendMsgOnlineRoomUsers(room, username, message);
                    }
//                    }
//                    ).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e1) {
                e1.printStackTrace();
            }
        } while (true);
        StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
        storageDAO.logUserActivity(user.getUsername());
        try {
            textOutput.close();
            textInput.close();
            link.close();
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

        System.err.println("Client dc'ed");
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
                storageDAO.logUserActivity(roomUser);
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
