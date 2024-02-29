package server.util;

import common.Command;
import server.dao.DataSourcePool;
import server.dao.StorageDAO;
import server.dao.StorageDAOImpl;

import java.util.Map;

import static server.ServerSettings.onlineUsers;

public class Util {
    public static void pushFriendsList() {
        StorageDAO DAO = new StorageDAOImpl(DataSourcePool.instanceOf());
        while (true) {
            for (var user : onlineUsers.entrySet()) {
                Map<String, String> friends = DAO.getFriends(user.getKey());
                user.getValue().getTextOutput().println(appendFriends(friends));
            }

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

    public static void pullFriends(String username) {
        var printWriter = onlineUsers.get(username).getTextOutput();
        StorageDAO DAO = new StorageDAOImpl(DataSourcePool.instanceOf());
        Map<String, String> friendsAndStatus = DAO.getFriends(username);
        printWriter.println(appendFriends(friendsAndStatus));
    }

    private static String appendFriends(Map<String, String> friends) {
        StringBuffer sb = new StringBuffer();
        for (var friend : friends.entrySet()) {
            if (sb.isEmpty()) {
                sb.append(Command.PUSH_FRIENDS.name());
            }
            sb.append(",");
            sb.append(friend.getValue());
            sb.append("   ");
            sb.append(friend.getKey());
        }
        return sb.toString();
    }

}
