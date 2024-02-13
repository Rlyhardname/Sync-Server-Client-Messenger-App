package server.dao;

import java.sql.Timestamp;
import java.util.Optional;

public interface StorageDAO<T> {

    Optional<T> createUser(String username, String password);

    void logUserActivity(String username);

    void updateUserLog(String username);

    Timestamp fetchTimestamp(String username);

    void deleteById(int id);

    String[] fetchAllByNameUnsentMessages(String username);

    boolean storeMessage(String username, String msg, int room);

    String[] getRoomUsers(int roomId);

}
