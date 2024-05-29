package server.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StorageDAO<T> {

    Optional<T> createUser(String username, String password);

    void logUserLogout(String username);

    void logUserLogin(String username);

    void updateUserLogMessageSent(String username, int state);

    Timestamp fetchLastLogoutTimestamp(String username);

    void deleteById(int id);

    String[] fetchAllByNameUnsentMessages(String username);

    boolean isUserAuthorizedInRoom(String username, String msg, int room);

    void saveMessage(String username, String msg, int room);

    String[] getRoomUsers(int roomId);

    Map<String, String> getFriends(String username);

    void saveContact(String username1, String username2);

    String getRoomIdAndRoomName(String username);

    List<String> fetchSearchResults(String username);

    void createChatRoom(String sender, String accepter);

    boolean isFriends(String user1, String user2);

}
