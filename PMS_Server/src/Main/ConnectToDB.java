package Main;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectToDB {

	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/girrafe";
	static final String USER = "root";
	static final String PASS = "dCBZXTf49PcL3L97lWXP";

	ConnectToDB(String[] args, Enum task) {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
	
			if(task.ordinal() == 1) {
				ResultSet resultSet = selectRoomUsers(stmt);
				while (resultSet.next()) {
					notifyUser(resultSet.getString(1));
				}
			}
			else if(task.ordinal() == 2) {
				// some other code until we do all cases
			}

			

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		System.out.println("Query has been executed and connection has been closed");
	}

	private void notifyUser(String string) {
		// TODO Auto-generated method stub
		// check if user is online and if true = send update 
		// else if offline do nothing, and when user gets online he will ask for updates when connecting(will send his db i guess and we compare to our and send him updated tables info which he can imput in his db)
	}

	public static ResultSet selectRoomUsers(Statement stmt) throws SQLException {
		String sql = "Select User_ID from char_room_wharehouse where char_room_ID = args[0]";
		stmt.executeQuery(sql);
		return stmt.getResultSet();
	}



}

