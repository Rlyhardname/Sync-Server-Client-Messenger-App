package Main;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class DbServer {

	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/girrafe";
	static final String USER = "root";
	static final String PASS = "dCBZXTf49PcL3L97lWXP";
	Connection conn;
	static Statement stmt;
	PreparedStatement prep;
	Enum task;
	String[] args;

	DbServer() {
		conn = null;
		stmt = null;
		prep = null;
		try {
			Class.forName(JDBC_DRIVER);
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

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
		} catch (SQLException se) {
			se.printStackTrace();
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		// System.out.println("Query has been executed and connection has been closed");

	}

	private void doSomething() {

		if (task.ordinal() == 2) {
			// some other code until we do all cases
		}

	}

	// returns resultSet with all user_ID's that equal the args[0]
	public ArrayList<String> selectRoomUsers() throws SQLException {
		// Maybe change to String[] after tests
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

	private void notifyUser(String user) {
		if (isOnline(user)) {

			// napravi neshto
		}
		// TODO Auto-generated method stub
		// check if user is online and if true = send update
		if (isOnline(user)) {

		}
		;
		// else if offline do nothing, and when user gets online he will ask for updates
		// when connecting(will send his db i guess and we compare to our and send him
		// updated tables info which he can imput in his db)
	}

	private boolean isOnline(String user) {
		// proveri dali userera e online
		// TODO Auto-generated method stub
		return false;
	}


	public boolean isRegisteredUser(String username) {
		String sql = "SELECT username FROM User " + " where username = ?";
		try {
			prep = conn.prepareStatement(sql);
			prep.setString(1, username);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				if (rs.getString(1) != null) {
					return true;
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean passwordIsCorrect(String username, String password) {
		// TODO Auto-generated method stub
		String sql = "SELECT password from User where username = ?";
		try {
			prep = conn.prepareStatement(sql);
			prep.setString(1, username);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				if (rs.getString(1).equals(password)) {
					return true;
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean createUser(String username, String password) {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO User " + "VALUES(?,?)";

		try {
			prep = conn.prepareStatement(sql);
			prep.setString(1, username);
			prep.setString(2, password);
			prep.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sql = "SELECT username FROM User " + "WHERE username=?";

		ResultSet rs = null;
		try {
			prep = conn.prepareStatement(sql);
			prep.setString(1, username);
			rs = prep.executeQuery();// prep.getResultSet();
			if (!(rs.isBeforeFirst())) {

				return false;
			}

		} catch (

		SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public void createTables() {
		// TODO Auto-generated method stub

	}

	static boolean createTableUser() {
		String sql = "CREATE TABLE `user` (\n"
				+ "  `username` varchar(25) NOT NULL,\n"
				+ "  `password` varchar(32) NOT NULL,\n"
				+ "  PRIMARY KEY (`username`),\n"
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

	
	static boolean createTableUserAdditionalInfo() {
		String sql = "CREATE TABLE `user_additional_info` (\n"
				+ "  `username` varchar(25) NOT NULL,\n"
				+ "  `avatar` blob,\n"
				+ "  `bio` varchar(200) DEFAULT NULL,\n"
				+ "  KEY `username_idx` (`username`),\n"
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
	
	
	static boolean createTableUserLog() {
		String sql = "CREATE TABLE user_log"
				+ "(username VARCHAR(25) NOT NULL,"
				+ "login_time datetime DEFAULT NULL,"
				+ "logout_time datetime DEFAULT NULL,"
				+ "allMessagesSent TINYINT DEFAULT 1,"
				+ "FOREIGN KEY (username) REFERENCES user(username))";
//				+ "  KEY `user_log_username_fk_idx` (`username`),\n"
//				+ "  CONSTRAINT `user_log_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE\n"
//				+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
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

	
	
	static boolean createTableFriends() {
		String sql = "CREATE TABLE `friends` (\n"
				+ "  `username` varchar(25) NOT NULL,\n"
				+ "  `friend` int NOT NULL,\n"
				+ "  KEY `friends_username_idx` (`username`),\n"
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
	
	
	

	static boolean createTableChatRoom() {
		String sql = "CREATE TABLE `chat_room` (\n"
				+ "  `chat_room_id` int NOT NULL AUTO_INCREMENT,\n"
				+ "  `room_name` varchar(45) DEFAULT 'New_Room',\n"
				+ "  `room_theme` blob,\n"
				+ "  PRIMARY KEY (`chat_room_id`),\n"
				+ "  UNIQUE KEY `chat_room_id_UNIQUE` (`chat_room_id`)\n"
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

	static boolean createTableChatRoomWarehouse() {
		String sql = "CREATE TABLE `chat_room_warehouse` (\n"
				+ "  `chat_room_id` int NOT NULL,\n"
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

	static boolean createTableMessageData() {
		String sql = "CREATE TABLE `message_data` (\n"
				+ "  `message_id` int NOT NULL AUTO_INCREMENT,\n"
				+ "  `username` varchar(25) NOT NULL,\n"
				+ "  `chat_room_id` int NOT NULL,\n"
				+ "  `message_text` varchar(500) DEFAULT NULL,\n"
				+ "  `message_image` blob,\n"
				+ "  `time_log` datetime DEFAULT NULL,\n"
				+ "  `user_state` int DEFAULT NULL,\n"
				+ "  PRIMARY KEY (`message_id`),\n"
				+ "  UNIQUE KEY `message_id_UNIQUE` (`message_id`),\n"
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
	
	
	
	
	
	
	public void addChatRoom() {
		String sql = "INSERT INTO chat_room "
				+ "(room_name) "
				+ "VALUES(?)";

		String room = "Account1AndAccount2Room";
		try {
			prep = conn.prepareStatement(sql);
			prep.setString(1, room);
			prep.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void fillRoom() {
		String sql = "INSERT INTO chat_room_warehouse "
				+ "VALUES(?,?)";

		int room = 1;
		String user = "account4";
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
	
	public void storeMessage(String user,String msg,int room) {
		String sql = "INSERT INTO Message_data "
				+"(chat_room_id,username,message_text)"
				+ "VALUES(?,?,?)";
		try {
			prep = conn.prepareStatement(sql);
			prep.setInt(1, room);
			prep.setString(2, user);
			prep.setString(3, msg);
			prep.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loginTime(String username) {
		// TODO Auto-generated method stub
		// LocalDateTime.now() insert into user_log where username = username;
	}
	
	public void alterTable() {
		String sql = "ALTER TABLE message_data "+
				"ADD file BLOB DEFAULT NULL";
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

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public boolean checkIfRoomUsersOnline(String string) {
		
		return true;
	}

	public String[] getRoomUsers(int i) {
		String sql = "SELECT Username "+
				"FROM chat_room_warehouse " +
				"WHERE chat_room_id=?";
		ResultSet rs = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			prep = conn.prepareStatement(sql);
			prep.setInt(1, 1);
			rs = prep.executeQuery();
			while(rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				rs.close();
				prep.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		String [] yes = (String[]) list.toArray(new String[0]);
		return yes;
	}

	public void insertUserLogLogin(String username) {
		String sql = "INSERT INTO user_log "+
				"(username,login_time) "+
				"VALUES(?,NOW())";
		try {
			prep = conn.prepareStatement(sql);
			prep.setString(1,username);
			prep.executeLargeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void insertUserLogout(String username) {
		String sql = "INSERT INTO user_log "+
				"(username,logout_time) "+
				"VALUES(?,NOW())";
		try {
			prep = conn.prepareStatement(sql);
			prep.setString(1,username);
			prep.executeLargeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void StoreFile(String message,  String username, int roomID, FileInputStream file) {
		// TODO Auto-generated method stub
		
		String sql = "INSERT INTO message_data "+
				"(message,username) "+
				"VALUES(?,NOW())";
		try {
			prep = conn.prepareStatement(sql);
			prep.setString(1,message);
			prep.setString(2, username);
			prep.setInt(3, roomID);
			prep.setBinaryStream(4, file);
			prep.executeLargeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public String[] getUnsendMessages(String username) {
		// TODO Auto-generated method stub
		String sql = "SELECT message,username,room_id "+
		"FROM message_data "+
		"(username) "+
		"VALUES(?) "+
	//	"WHERE time_log " 
		ArrayList<String> messages = new ArrayList<String>();
		try {
			prep = conn.prepareStatement(sql);
			prep.setString(2, username);
			ResultSet rs = prep.executeQuery();
			while(rs.next()) {
			String message = rs.getString(1);
			String user = rs.getString(2);
			int room = rs.getInt(3);
			messages.add(message+","+user+","+room);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String [] batch = messages.toArray(new String[0]);
		return batch;
		
	}
	
	
}
