package server.dao;

import client.model.User;
import common.Command;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setString(1, username);
            prep.setString(2, password);
            prep.executeUpdate();
            return Optional.of(new User(username, password));

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Optional.ofNullable(null);
    }

    @Override
    public void logUserLogout(String username) {
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
    public void logUserLogin(String username) {
        String sql = "INSERT INTO user_log " + "(username,login_time) " + "VALUES(?,NOW())";
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
    public void updateUserLogMessageSent(String username,int state) {
        Timestamp dateTimeStamp = fetchLastLogoutTimestamp(username);
        String sql = "UPDATE User_Log " + "SET allMessagesSent = ? " + "WHERE Logout_time = ? and username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setInt(1, state);
            prep.setTimestamp(2, dateTimeStamp);
            prep.setString(3, username);
            prep.executeLargeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Timestamp fetchLastLogoutTimestamp(String username) {
        String sql = "SELECT Logout_time " + "FROM User_Log " + "WHERE username = ? " + "ORDER BY Logout_time DESC LIMIT 1";
        Timestamp dateTimeStamp = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setString(1, username);
            ResultSet rs = prep.executeQuery();

            while (rs.next()) {
                dateTimeStamp = rs.getTimestamp(1);
                if (Objects.nonNull(dateTimeStamp)) {
                    System.err.println("Current Timestamp: " + dateTimeStamp.toString());
                    //  return dateTimeStamp;
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
        ResultSet rs = null;
        Timestamp timestamp = fetchLastLogoutTimestamp(username);
        String sql = "SELECT allMessagesSent FROM User_log " + "WHERE logout_time = ? and allMessagesSent = ? ";
        Timestamp ts;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setTimestamp(1, timestamp);
            prep.setInt(2, 0);
            rs = prep.executeQuery();
            while (rs.next()) {
                int val = rs.getInt(1);
                System.out.println("val : " + val);
                if (val == 0) {
                    return getBatch(username, timestamp);
                }

            }

        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return new String[0];
    }

    private String[] getBatch(String username, Timestamp timestamp) {
        String sql = "SELECT message_text,username,chat_room_id " + "FROM message_data "
                + "WHERE username != ? and timeLOG > ?";
        ArrayList<String> messages = new ArrayList<String>();
        ResultSet rs = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.addBatch(sql);
            prep.setString(1, username);
            prep.setTimestamp(2, timestamp);
            rs = prep.executeQuery();
            while (rs.next()) {
                String message = rs.getString(1);
                String user = rs.getString(2);
                int room = rs.getInt(3);
                messages.add(Command.TEXT_MESSAGE.name() + "," + user + "," + room + "," + message);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        String[] batch = messages.toArray(String[]::new);
        return batch;
    }

    @Override
    public boolean isUserAuthorizedInRoom(String username, String msg, int room) {
        String sql = "SELECT username " + "FROM chat_room_warehouse " + "WHERE chat_room_id=? AND username=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setInt(1, room);
            prep.setString(2, username);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                if (Objects.nonNull(rs.getString(1))) {
                    return true;
                }

            }

        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return false;
    }

    @Override
    public void saveMessage(String username, String msg, int room) {
        String sql2 = "INSERT INTO Message_data " + "(chat_room_id,username,message_text) " + "VALUES(?,?,?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql2)) {
            try {
                prep.setInt(1, room);
                prep.setString(2, username);
                prep.setString(3, msg);
                prep.executeUpdate();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (
                SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public String[] getRoomUsers(int i) {
        String sql = "SELECT Username " + "FROM chat_room_warehouse " + "WHERE chat_room_id=?";
        ResultSet rs = null;
        ArrayList<String> list = new ArrayList<String>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setInt(1, i);
            rs = prep.executeQuery();
            while (rs.next()) {
                String user = rs.getString(1).toLowerCase();
                list.add(user);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        String[] usersInRoom = list.toArray(new String[0]);
        return usersInRoom;
    }

}

