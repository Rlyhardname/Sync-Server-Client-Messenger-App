package Main;

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

	boolean createTableUser() {
		String sql = "CREATE TABLE User" + "(Username VARCHAR(25)," + "Password VARCHAR(32),"
				+ "PRIMARY KEY (Username))";
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

	private void createTableUserLog() {

	}

	private void createTableUserAdditionalInfo() {

	}

	static boolean createTableChatRoom() {
		String sql = "CREATE TABLE Chat_Room" + 
				"(chat_room_id int NOT NULL AUTO_INCREMENT," + 
				"room_name VARCHAR(25)," +
				"PRIMARY KEY (chat_room_id))";
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
		String sql = "CREATE TABLE Chat_Room_Warehouse" + 
				"(chat_room_id int NOT NULL," + 
				"Username VARCHAR(25) NOT NULL,"+
				"FOREIGN KEY (chat_room_id) REFERENCES Chat_Room(chat_room_id) )";
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
		String sql = "CREATE TABLE Message_Data" + 
				"(chat_room_id int NOT NULL," + 
				"Username VARCHAR(25) NOT NULL,"+
				"message_text VARCHAR(25),"+
				"timeLOG DATETIME DEFAULT CURRENT_TIMESTAMP," +
				"FOREIGN KEY (chat_room_id) REFERENCES Chat_Room(chat_room_id) )";
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
	
	public void createChatRoom() {
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
		String user = "account2";
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
	
	public void storeMessage(String user,String msg) {
		String sql = "INSERT INTO Message_data "
				+"(chat_room_id,username,message_text)"
				+ "VALUES(?,?,?)";

		int room = 1;

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
		String sql = "ALTER TABLE message_data"+
				"ADD timeLOG DATETIME";
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void dropTable() {
		String sql = "DROP TABLE message_data";
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Enum getTask() {
		return task;
	}

	public void setTask(Enum task) {
		this.task = task;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public boolean checkIfRoomUsersOnline(String string) {
		// TODO Auto-generated method stub
		return true;
	}

	public String[] getRoomUsers(int i) {
		String sql = "SELECT Username"+
				"FROM chat_room_warehouse" +
				"WHERE chat_room_id=?";
		ResultSet rs = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			prep = conn.prepareStatement(sql);
			prep.setInt(1, i);
			rs = prep.executeQuery();
			while(rs.next()) {
				list.add(rs.getString(2));
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
		
		return (String[]) list.toArray();
	}
}
