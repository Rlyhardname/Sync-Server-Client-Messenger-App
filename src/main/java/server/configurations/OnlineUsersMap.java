package server.configurations;

import server.services.ServerVer2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OnlineUsersMap {
    Map<String, ServerVer2> onlineUsers;

    public OnlineUsersMap() {
        onlineUsers = new ConcurrentHashMap<>();
    }

    public static OnlineUsersMap instanceOf() {
        return new OnlineUsersMap();
    }

    public Map<String, ServerVer2> getOnlineUsers() {
        return onlineUsers;
    }
}