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
import java.util.Optional;

public class ServerVer2 {
    private User user;
    private Socket link;
    private PrintWriter output;
    private BufferedReader input;
    private DataOutputStream outputFile;
    private DataInputStream inputFile;


    ServerVer2() {
        hasInitialized();
        connectClient();
    }

    private boolean hasInitialized() {
        try {
            link = ServerSettings.serverSocket.accept();
            new Thread(() -> {
                new ServerVer2();
            }).start();
            output = new PrintWriter(link.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(link.getInputStream()));
            inputFile = new DataInputStream(link.getInputStream());
        } catch (IOException e1) {
            try {
            } catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            }
            e1.printStackTrace();
            return false;
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
                msg = input.readLine();
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
                output.println("UsernameException" + "," + "sorry");
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
                    createAccount();
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
                output.println(string);
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
        String msg = Command.LOGIN_SUCCESS.name() + "," + "Successfully logged in!";
        ServerSettings.onlineUsers.put(user.getUsername(), link);
        sendMessage(msg);
    }

    private void createAccount() throws IOException {
        do {
            if (!userDataIsValid(20, user.getUsername())) {
                break;
            }

            AuthDAO authDAO = new AuthenticationDAO(DataSourcePool.instanceOf());
            if (authDAO.isUserRegistered(user.getUsername())) {
                output.println(Command.NICKNAME_UNAVAILABLE.name());
                break;
            }

            if (!userDataIsValid(32, user.getPassword())) {
                break;
            }

            StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
            Optional<User> userOpt = storageDAO.createUser(user.getUsername(), user.getPassword());
            if (userOpt.isPresent()) {
                String msg = Command.REGISTER_SUCCESS + "," + "Account Successfully created!";
                sendMessage(msg);
            } else {
                String msg = Command.REGISTER_FAIL + "," + "Database error, try again!";
                sendMessage(msg);
            }

            break;
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
                msg = input.readLine();
                if ((msg == null) || (msg.startsWith(","))) {
                    continue;
                }
                String[] userMsg = msg.split(",");

                if (userMsg[0].equals("ClosingClient")) {
                    break;
                }

                if (userMsg[3].equals("sendFile")) { // SEND FILE logic
                    reSendFile(userMsg[2], userMsg[1]);

                }

                new Thread(() -> {
                    if (isTextMessage(userMsg[3])) { // SEND MSG LOGIC
                        StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
                        boolean isMessageStored = storageDAO.storeMessage(userMsg[1], userMsg[0],
                                Integer.parseInt(userMsg[2]));
                        if (isMessageStored) {
                            sendMsgOnlineRoomUsers(userMsg[2], userMsg[1], userMsg[0]);
                        }
                    }
                }).start();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e1) {
                e1.printStackTrace();
            }
        } while (true);
        StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
        storageDAO.logUserActivity(user.getUsername());
        try {
            output.close();
            input.close();
            link.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.err.println("Client dc'ed");
    }

    private boolean isTextMessage(String msg) {
        if (msg.equals("TextMessage")) {
            return true;
        }
        return false;
    }

    private void sendMsgOnlineRoomUsers(String room, String user, String message) {
        StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
        String[] users = storageDAO.getRoomUsers(Integer.parseInt(room));
        for (String roomUser : users) {
            if (ServerSettings.onlineUsers.get(roomUser) != null) {
                Socket userSocket = ServerSettings.onlineUsers.get(roomUser);
                PrintWriter distribute = null;
                try {
                    distribute = new PrintWriter(userSocket.getOutputStream(), true);
                    distribute.println(message + "," + room + "," + roomUser + "," + "TextMessage");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                storageDAO.logUserActivity(roomUser);
            }

        }

    }

    private void sendMessage(String msg) {
        output.println(msg);
    }

    private void reSendFile(String room, String usernameSendingFile) {
        StorageDAO storageDAO = new StorageDAOImpl(DataSourcePool.instanceOf());
        String[] usersInRoom = storageDAO.getRoomUsers(Integer.parseInt(room));
        for (String user : usersInRoom) {

            Socket onlineUser = ServerSettings.onlineUsers.get(user);
            if (onlineUser != null && !usernameSendingFile.toLowerCase().equals(user)) {
                int bytes = 0;
                PrintWriter resend = null;
                try {
                    resend = new PrintWriter(onlineUser.getOutputStream(), true);
                } catch (IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
                resend.println(
                        usernameSendingFile + "," + " is sendingFile in room " + "," + "room" + "," + "ReceiveFile");
                DataOutputStream outputFile1 = null;
                try {
                    outputFile1 = new DataOutputStream(onlineUser.getOutputStream());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                try {

                    byte[] buffer = new byte[4 * 1024];
                    long size = inputFile.readLong();

                    while ((size > 0 && (bytes = inputFile.read(buffer)) != -1)) {
                        outputFile1.write(buffer, 0, bytes);
                        System.out.println("size " + size + " Buffer " + buffer);
                        size -= bytes;
                        outputFile1.flush();
                        if (bytes < 4096) {
                            break;
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                resend.flush();
                //outputFile1.close();
                // resend.close();
            }

        }

    }

    private void concatIncomingMessage(String str) {
        ServerGUI.textArea.append(str);
    }

    public static synchronized void printActiveUsers() {
        ServerGUI.printArea();
    }
}
