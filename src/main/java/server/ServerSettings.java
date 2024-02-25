package server;

import common.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSettings {
    public static final int PORT = 1337;
    public static ServerSocket serverSocket;

    static {
        try {
            if (serverSocket == null) {
                serverSocket = new ServerSocket(PORT);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ConcurrentHashMap<String, ServerVer2> onlineUsers = new ConcurrentHashMap<>();

//            Character.toString(0x00002705), //check mark
//            Character.toString(0x1F60D), //cool face
//            Character.toString(0x0001F5D9),// x symbol

    public void pushFriendsList() {
        String one = "1 ROOM                    ";
        String two = "2 ROOM                    ";
        String three = "3 ROOM                    ";
        Random rand = new Random();
        String[] testArr;
        while (true) {
            int order = rand.nextInt(3);
            if (order == 0) {
                testArr = new String[]{one, two, three};
            } else if (order == 1) {
                testArr = new String[]{two, three, one};
            } else {
                testArr = new String[]{three, one, two};
            }

            var friends = String.join(",", testArr);
            for (var user : onlineUsers.entrySet()) {
                user.getValue().getTextOutput().println(Command.PUSH_FRIENDS + "," + friends);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }

    }

}
