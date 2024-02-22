package server.dao;

import java.sql.Timestamp;
import java.util.Optional;

public interface StorageDAO<T> {

    Optional<T> createUser(String username, String password);

    void logUserLogout(String username);
    void logUserLogin(String username);
    void updateUserLogMessageSent(String username,int state);

    Timestamp fetchLastLogoutTimestamp(String username);

    void deleteById(int id);

    String[] fetchAllByNameUnsentMessages(String username);

    boolean isUserAuthorizedInRoom(String username, String msg, int room);

    void saveMessage(String username, String msg, int room);

    String[] getRoomUsers(int roomId);

}
