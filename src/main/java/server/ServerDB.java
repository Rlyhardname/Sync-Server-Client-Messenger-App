package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

public class ServerDB {

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/girrafe";
    static final String USER = "root";
    static final String PASS = "dCBZXTf49PcL3L97lWXP";
    private Connection conn;
    private Statement stmt;
    private PreparedStatement prep;
    Enum task;

    ServerDB() {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);


        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void closeConnection() {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (prep != null) {
                prep.close();
            }
            if (conn != null) {
                conn.close();
            }
            if (!conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        System.out.println("Query has been executed and connection has been closed");
    }

    public ArrayList<String> selectRoomUsers() throws SQLException {

        ArrayList<String> users = new ArrayList<String>();
        String sql = "Select username from user where user = ?";
        prep = conn.prepareStatement(sql);
        ResultSet rs = prep.executeQuery();
        if (task.ordinal() == 1) {
            ResultSet resultSet = null;
            try {
                resultSet = stmt.getResultSet();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                while (resultSet.next()) {
                    try {
                        users.add(resultSet.getString(1));
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return users;
    }

    boolean createTableUser() {
        String sql = "CREATE TABLE `user` (\n" + "  `username` varchar(25) NOT NULL,\n"
                + "  `password` varchar(32) NOT NULL,\n" + "  PRIMARY KEY (`username`),\n"
                + "  UNIQUE KEY `username_UNIQUE` (`username`)\n"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
        try {
            if (stmt.execute(sql)) {
                return true;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    boolean createTableUserAdditionalInfo() {
        String sql = "CREATE TABLE `user_additional_info` (\n" + "  `username` varchar(25) NOT NULL,\n"
                + "  `avatar` blob,\n" + "  `bio` varchar(200) DEFAULT NULL,\n" + "  KEY `username_idx` (`username`),\n"
                + "  CONSTRAINT `user_additional_info_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE\n"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
        try {
            if (stmt.execute(sql)) {
                return true;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }

    boolean createTableUserLog() {
        String sql = "CREATE TABLE user_log" + "(username VARCHAR(25) NOT NULL," + "login_time datetime DEFAULT NULL,"
                + "logout_time datetime DEFAULT NULL," + "allMessagesSent TINYINT DEFAULT 1,"
                + "FOREIGN KEY (username) REFERENCES user(username))";
        try {
            if (stmt.execute(sql)) {
                return true;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }

    boolean createTableFriends() {
        String sql = "CREATE TABLE `friends` (\n" + "  `username` varchar(25) NOT NULL,\n"
                + "  `friend` int NOT NULL,\n" + "  KEY `friends_username_idx` (`username`),\n"
                + "  CONSTRAINT `friends_username` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE\n"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
        try {
            if (stmt.execute(sql)) {
                return true;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }

    boolean createTableChatRoom() {
        String sql = "CREATE TABLE `chat_room` (\n" + "  `chat_room_id` int NOT NULL AUTO_INCREMENT,\n"
                + "  `room_name` varchar(45) DEFAULT 'New_Room',\n" + "  `room_theme` blob,\n"
                + "  PRIMARY KEY (`chat_room_id`),\n" + "  UNIQUE KEY `chat_room_id_UNIQUE` (`chat_room_id`)\n"
                + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
        try {
            if (stmt.execute(sql)) {
                return true;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    boolean createTableChatRoomWarehouse() {
        String sql = "CREATE TABLE `chat_room_warehouse` (\n" + "  `chat_room_id` int NOT NULL,\n"
                + "  `username` varchar(25) NOT NULL,\n"
                + "  KEY `chat_room_warehouse_chat_room_id_fk_idx` (`chat_room_id`),\n"
                + "  KEY `chat_room_warehouse_username_fk_idx` (`username`),\n"
                + "  CONSTRAINT `chat_room_warehouse_chat_room_id_fk` FOREIGN KEY (`chat_room_id`) REFERENCES `chat_room` (`chat_room_id`) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                + "  CONSTRAINT `chat_room_warehouse_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE\n"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
        try {
            if (stmt.execute(sql)) {
                return true;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    boolean createTableMessageData() {
        String sql = "CREATE TABLE `message_data` (\n" + "  `message_id` int NOT NULL AUTO_INCREMENT,\n"
                + "  `username` varchar(25) NOT NULL,\n" + "  `chat_room_id` int NOT NULL,\n"
                + "  `message_text` varchar(500) DEFAULT NULL,\n" + "  `message_image` blob,\n"
                + "  `time_log` datetime DEFAULT NULL,\n" + "  `user_state` int DEFAULT NULL,\n"
                + "  PRIMARY KEY (`message_id`),\n" + "  UNIQUE KEY `message_id_UNIQUE` (`message_id`),\n"
                + "  KEY `message_data_username_fk_idx` (`username`),\n"
                + "  KEY `message_data_chat_room_id_fk_idx` (`chat_room_id`),\n"
                + "  CONSTRAINT `message_data_chat_room_id_fk` FOREIGN KEY (`chat_room_id`) REFERENCES `chat_room` (`chat_room_id`) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                + "  CONSTRAINT `message_data_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE\n"
                + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
        try {
            if (stmt.execute(sql)) {
                return true;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void addChatRoom(String room) {
        String sql = "INSERT INTO chat_room " + "(room_name) " + "VALUES(?)";

        try {
            prep = conn.prepareStatement(sql);
            prep.setString(1, room);
            prep.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void fillRoom(int room, String user) {
        String sql = "INSERT INTO chat_room_warehouse " + "VALUES(?,?)";

        try {
            prep = conn.prepareStatement(sql);
            prep.setInt(1, room);
            prep.setString(2, user);
            prep.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void alterTable() {
        String sql = "ALTER TABLE message_data " + "ADD file BLOB DEFAULT NULL";
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void dropTable() {
        String sql = "DROP TABLE User_Log";
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



}

