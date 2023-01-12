package Main;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ServerSideDB {

	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/girrafe";
	static final String USER = "root";
	static final String PASS = "dCBZXTf49PcL3L97lWXP";
	Connection conn;
	Statement stmt;
	Enum task;
	String[] args;

	ServerSideDB() {
		conn = null;
		stmt = null;
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
		System.out.println("Query has been executed and connection has been closed");
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
		String sql = "Select User_ID from char_room_wharehouse where char_room_ID = args[1]";
		stmt.executeQuery(sql);
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

	public boolean isRegisteredUser(String username) {
		String sql = "Select username from char_room_wharehouse where username = @username";
		try {
			stmt.executeQuery(sql);
			ResultSet rs = stmt.getResultSet();
			while(rs.next()) {
				if(rs.getString(1) != null) {
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
		String sql = "Select password from char_room_wharehouse where username = @username";
		try {
			stmt.executeQuery(sql);
			ResultSet rs = stmt.getResultSet();
			while(rs.next()) {
				if(rs.getString(1).equals(password)) {
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
		String sql = "INSERT INTO User"
				+ "Values(@username, @password)";
		try {
			stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sql = "SELECT username FROM User"
				+ "WHERE username=@username";
		ResultSet rs = null;
		try {
			stmt.executeQuery(sql);
			rs = stmt.getResultSet();
			if(!(rs.isBeforeFirst())){
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	public void createTables() {
		// TODO Auto-generated method stub
		
	}
	
	boolean createTableUser() {
		String sql = "CREATE TABLE User"
				+ "(Username VARCHAR(25)"
				+ "Password VARCHAR(32)"
				+ "PRIMARY KEY (Username))";
		try {
			if(stmt.execute(sql)) {
				return true;
			};
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
	private void createTableChatRoom() {
		
	}
	private void createTableChatRoomWarehouse() {
		
	}
	private void createTableMessageData() {
		
	}

	public void loginTime(String username) {
		// TODO Auto-generated method stub
		// LocalDateTime.now() insert into user_log where username = username;
	}
}
