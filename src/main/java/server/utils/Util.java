package server.utils;

import common.Command;
import server.dao.DataSourcePool;
import server.dao.StorageDAO;
import server.dao.StorageDAOImpl;

import java.util.Map;

import static server.configurations.ServerSettings.onlineUsers;

public class Util {
    public static void pushChatRooms() {
        StorageDAO DAO = new StorageDAOImpl(DataSourcePool.instanceOf());
        while (true) {
            for (var user : onlineUsers.entrySet()) {
                pullFriends(DAO, user.getKey());
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private static String pullGroupChats(String username) {
        StorageDAO DAO = new StorageDAOImpl(DataSourcePool.instanceOf());
        String gcIdAndName = DAO.getRoomIdAndRoomName(username);

        return gcIdAndName;
    }

    public static void pullFriends(StorageDAO DAO, String username) {
        var printWriter = onlineUsers.get(username).getTextOutput();
        Map<String, String> friendsAndStatus = DAO.getFriends(username);
        String friends = appendFriends(friendsAndStatus);
        String groupChats = pullGroupChats(username);
        printWriter.println(friends + groupChats);
    }

    private static String appendFriends(Map<String, String> friends) {
        StringBuffer sb = new StringBuffer();
        for (var friend : friends.entrySet()) {
            if (sb.isEmpty()) {
                sb.append(Command.PUSH_FRIENDS);
            }
            sb.append(",");
            sb.append(friend.getValue());
            sb.append("   ");
            sb.append(friend.getKey());
        }
        return sb.toString();
    }

}
