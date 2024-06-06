package server.utils;

import client.models.User;
import common.enums.Command;
import server.dao.DataSourcePool;
import server.dao.StorageDAO;
import server.dao.StorageDAOImpl;

import java.util.Map;

import static server.configurations.ApplicationContext.APPLICATION_CONTEXT;

public class Util {
    public static void recurringPushToChatRooms() {
        StorageDAO<User> DAO = new StorageDAOImpl(DataSourcePool.instanceOf());
        while (true) {
            APPLICATION_CONTEXT.getOnlineUsersHashMap().getOnlineUsers().forEach((key, value) -> pullFriends(DAO, key));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

    public static void pullFriends(StorageDAO<User> DAO, String username) {
        var printWriter = APPLICATION_CONTEXT.fetchServerInstance(username).getTextOutput();
        Map<String, String> friendsAndStatus = DAO.getFriends(username);
        String friends = appendFriends(friendsAndStatus);
        String groupChats = pullGroupChats(username);
        printWriter.println(friends + groupChats);
    }

    private static String pullGroupChats(String username) {
        StorageDAO<User> DAO = new StorageDAOImpl(DataSourcePool.instanceOf());

        return DAO.getRoomIdAndRoomName(username);
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
