package server.dao;

import client.model.User;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class StorageDAOImpl implements StorageDAO<User> {
    private final DataSource dataSource;

    public StorageDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Optional<User> createUser(String username, String password) {
        String sql = "INSERT INTO User " + "VALUES(?,?)";
        ResultSet rs = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setString(1, username);
            prep.setString(2, password);
            prep.executeUpdate();

            sql = "SELECT username FROM User " + "WHERE username=?";
            prep.addBatch(sql);
            prep.setString(1, username);
            rs = prep.executeQuery();
            if (rs.next()) {
                String name = rs.getString(1);
                String pass = rs.getString(2);
                return Optional.of(new User(name, password));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Optional.of(null);
    }

    @Override
    public void logUserActivity(String username) {
        String sql = "INSERT INTO user_log " + "(username,logout_time) " + "VALUES(?,NOW())";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setString(1, username);
            prep.executeLargeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void updateUserLog(String username) {
        Timestamp dateTimeStamp = fetchTimestamp(username);
        String sql = "UPDATE User_Log " + "SET allMessagesSent = ? " + "WHERE Logout_time = ? and username = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setInt(1, 0);
            prep.setTimestamp(2, dateTimeStamp);
            prep.setString(3, username);
            prep.executeLargeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Timestamp fetchTimestamp(String username) {
        String sql = "SELECT Logout_time " + "FROM User_Log " + "WHERE username = ?";
        Timestamp dateTimeStamp = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setString(1, username);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {

                Timestamp timestamp = rs.getTimestamp(1);

                if (timestamp != null) {
                    dateTimeStamp = timestamp;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dateTimeStamp;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM chat_room_warehouse " + "WHERE chat_room_id=1";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public String[] fetchAllByNameUnsentMessages(String username) {
        // TODO Auto-generated method stub

        ResultSet rs = null;
        Timestamp timestamp = fetchTimestamp(username);
        boolean isUnsend = false;

        String sql = "SELECT allMessagesSent FROM User_log " + "WHERE logout_time = ? and allMessagesSent = ? ";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setTimestamp(1, timestamp);
            prep.setInt(2, 0);
            rs = prep.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    isUnsend = true;
                }
            }

            if (isUnsend) {
                String sql2 = "SELECT message_text,username,chat_room_id " + "FROM message_data "
                        + "WHERE username != ? and timeLOG > ?";
                ArrayList<String> messages = new ArrayList<String>();
                try {
                    prep.addBatch(sql2);
                    prep.setString(1, username);
                    prep.setTimestamp(2, timestamp);
                    rs = prep.executeQuery();
                    while (rs.next()) {
                        String message = rs.getString(1);
                        String user = rs.getString(2);
                        int room = rs.getInt(3);
                        messages.add(message + "," + user + "," + room + "," + "TextMessage");
                    }

                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String[] batch = messages.toArray(new String[0]);
                return batch;
            }

        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return new String[0];

    }

    @Override
    public boolean storeMessage(String username, String msg, int room) {
        String sql = "SELECT username " + "FROM chat_room_warehouse " + "WHERE chat_room_id=? AND username=?";
        String inputUser = "";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setInt(1, room);
            prep.setString(2, username);
            ResultSet rs = prep.executeQuery();

            while (rs.next()) {
                inputUser = rs.getString(1);

            }

            if (inputUser.toLowerCase().equals(username)) {

                String sql2 = "INSERT INTO Message_data " + "(chat_room_id,username,message_text) " + "VALUES(?,?,?)";
                try {
                    prep.addBatch(sql2);
                    prep.setInt(1, room);
                    prep.setString(2, inputUser);
                    prep.setString(3, msg);
                    prep.executeUpdate();

                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return true;
            }
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return false;
    }

}

