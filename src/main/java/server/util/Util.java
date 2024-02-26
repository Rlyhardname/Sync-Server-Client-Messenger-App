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
            StringBuffer sb = new StringBuffer();
            for (var user : onlineUsers.entrySet()) {
                Map<String, String> friends = DAO.getFriends(user.getKey());
                for (var friend : friends.entrySet()) {
                    if (sb.isEmpty()) {
                        sb.append(Command.PUSH_FRIENDS.name());
                    }
                    sb.append(",");
                    sb.append(friend.getValue());
                    sb.append("   ");
                    sb.append(friend.getKey());
                }
                user.getValue().getTextOutput().println(sb.toString());
                sb = new StringBuffer();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

}
