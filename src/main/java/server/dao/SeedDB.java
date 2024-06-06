package server.dao;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SeedDB {
    public SeedDB(DataBaseConfigurations dataBaseConfiguration) {
        String createDB = "CREATE DATABASE IF NOT EXISTS " + dataBaseConfiguration.getName();
        DataSource dataSource = DataSourcePool.getDataSource();
        boolean exists = true;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createDB);
        } catch (SQLException e) {
            System.err.println("DB url not existing yet...");
            exists = false;
        }

        if (!exists) {
            ((MysqlDataSource) dataSource).setUrl("jdbc:mysql://localhost/");
            try (Connection connection = DataSourcePool.getDataSource().getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute(createDB);
                exists = true;
                ((MysqlDataSource) dataSource).setUrl("jdbc:mysql://localhost/" + dataBaseConfiguration.getName());
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        String chat_room = "CREATE TABLE IF NOT EXISTS " + dataBaseConfiguration.getName() + ".chat_room (" +
                "id INT NOT NULL AUTO_INCREMENT, " +
                "room_name VARCHAR(64) NOT NULL, " +
                "users_count INT DEFAULT 2 NOT NULL, " +
                "PRIMARY KEY (id))";
        String user = "CREATE TABLE IF NOT EXISTS " + dataBaseConfiguration.getName() + ".user (" +
                "username VARCHAR(25) NOT NULL, " +
                "password VARCHAR(32) NOT NULL, " +
                "PRIMARY KEY (username))";
        String user_log = "CREATE TABLE IF NOT EXISTS " + dataBaseConfiguration.getName() + ".user_log (" +
                "username VARCHAR(25) NOT NULL, " +
                "login_time DATETIME, " +
                "logout_time DATETIME, " +
                "all_Messages_Sent TINYINT DEFAULT 0 NOT NULL, " +
                "CONSTRAINT constraint_username FOREIGN KEY (username) REFERENCES user (username) ON UPDATE CASCADE)";
        String chat_room_warehouse = "CREATE TABLE IF NOT EXISTS " + dataBaseConfiguration.getName() + ".chat_room_warehouse (" +
                "chat_room_id INT NOT NULL, " +
                "username VARCHAR(25) , " +
                "PRIMARY KEY(chat_room_id, username), " +
                "CONSTRAINT constraint_room_id_CK FOREIGN KEY (chat_room_id) REFERENCES chat_room (id), " +
                "CONSTRAINT constraint_username_CK FOREIGN KEY (username) REFERENCES user (username) ON UPDATE CASCADE)";

        String friends = "CREATE TABLE IF NOT EXISTS " + dataBaseConfiguration.getName() + ".friends (" +
                "username VARCHAR(25) NOT NULL, " +
                "friend VARCHAR(25) NOT NULL, " +
                "chat_room_id INT NOT NULL, " +
                "chat_room_name VARCHAR(64) NOT NULL, " +
                "PRIMARY KEY(username, friend), " +
                "CONSTRAINT constraint_username_id_CK_F FOREIGN KEY (username) REFERENCES user (username) ON UPDATE CASCADE, " +
                "CONSTRAINT constraint_friend_CK_F  FOREIGN KEY (friend) REFERENCES user (username) ON UPDATE CASCADE, " +
                "CONSTRAINT constraint_room_id_CK_F FOREIGN KEY (chat_room_id) REFERENCES chat_room (id))";

        String message_data = "CREATE TABLE IF NOT EXISTS " + dataBaseConfiguration.getName() + ".message_data (" +
                "chat_room_id INT NOT NULL, " +
                "username VARCHAR(25) NOT NULL, " +
                "message_text VARCHAR(128) NOT NULL, " +
                "time_log DATETIME DEFAULT NOW() NOT NULL, " +
                "CONSTRAINT constraint_room_id_CK_MD FOREIGN KEY (chat_room_id) REFERENCES chat_room (id), " +
                "CONSTRAINT constraint_username_CK_MD  FOREIGN KEY (username) REFERENCES user (username) ON UPDATE CASCADE)";

        String setDefaultSchema = "USE " + dataBaseConfiguration.getName();

        if (exists) {
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute(chat_room);
                statement.execute(user);
                statement.execute(user_log);
                statement.execute(chat_room_warehouse);
                statement.execute(friends);
                statement.execute(message_data);
                statement.execute(setDefaultSchema);



            } catch (SQLException e) {
                System.err.println("Look at SeedConfiguration");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }
}
