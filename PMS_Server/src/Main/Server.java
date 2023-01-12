package Main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Server extends Thread {

	public static int port = 1337;
	public static ServerSocket serverSocket;
	public static HashMap<String, Socket> onlineUsers;

	Server() {
		onlineUsers = new HashMap<String, Socket>();
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void connectClient() {
		// TODO Auto-generated method stub
		Socket link = null;
		try {

			link = serverSocket.accept();
			Thread newClient = new Thread(this);
			newClient.start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		authentication(link); // seems done
		syncClientWithServerDB();
		handleClient(link);

	}

	@Override
	public void run() {

		System.out.println("Waiting for new client");
		connectClient();

	}

	private void authentication(Socket link) {
		// TODO Auto-generated method stub
		while (true) {
			// Въведи user И парола sus system in za test
			// input = new Scanner(link.getInputStream()); 4akame ime i parola

			Scanner input = new Scanner(System.in);
			String username = input.nextLine();
			ServerSideDB db = new ServerSideDB();
			if (!username.equals("create")) {
				String password = input.nextLine();
				if (userExists(username, db)) {
					if (correctLoginInfo(username, password, db)) {
						login(username, link);
						input.close();
						db.closeConnection();
						break;
					}
				}

			} else {
				createAccount(db, input);
				input.close();
				db.closeConnection();
			}
		}
	}

	private void login(String username, Socket link) {
		// TODO Auto-generated method stub
		onlineUsers.put(username, link);
	}

	private void createAccount(ServerSideDB db, Scanner input) {
		// TODO Auto-generated method stub
		do {
			String username = input.nextLine();
			if (userDataIsValid(username, 20)) {
				if (!db.isUser(username)) {
					String password = input.nextLine();
					if (userDataIsValid(password, 32)) {
						db.createUser(username, password);
					}

				}
			}

		} while (true);

	}

	private boolean userDataIsValid(String username, int i) {
		// TODO Auto-generated method stub
		// foreach String[] = {"#","$",","," Insert ", " Update ", " Delete "} ect...
		// username.compare() to see if some symbol is in the username return false and
		// send client error message + return false
		// then check if username is less than i symbols and return true if both
		// conditions are met
		return false;
	}

	private boolean userExists(String username, ServerSideDB db) {
		return db.isUser(username);
	}

	private boolean correctLoginInfo(String username, String password, ServerSideDB db) {
		if (db.passwordIsCorrect(username, password)) {
			// send client GUI notification to change view
		}
		return false;
	}

	private void handleClient(Socket link) {

		Scanner input = null;
		try {
			input = new Scanner(link.getInputStream());
			String update = input.nextLine();
			while (!(update = input.nextLine()).equals("-1")) {
				String[] data = update.split(",");
				updateServerDB(data[0], data[1]);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			input.close();
		}

		try {

			input = new Scanner(link.getInputStream());
			PrintWriter output = new PrintWriter(link.getOutputStream(), true);
			int numMessages = 0;

			String message = input.nextLine();
			while (!message.equals("*CLOSE*")) {
				handleMessage(message);

				System.out.println("\nMessage received...");
				numMessages++;
				output.println("Message" + numMessages + ": " + message);
				message = input.nextLine();
			}
			output.println("Messages received: " + numMessages);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("\nClose connection...");
			input.close();
			try {
				link.close();
			} catch (IOException e) {
				System.out.println("\nUnable to close...");
				System.exit(1);
			}
		}

	}

	private void updateServerDB(String updateType, String string2) {
		// TODO Auto-generated method stub
		if (updateType.equals("1")) {
			// update client DATA in server db
		} else {
			// update add client messages in chatroom with name X
		}
	}

	private void syncClientWithServerDB() {
		// Select every entry in DB between client's last logout and current login time
		// now() and send them to client vs array of messages or new DB with the entries
		// between those 2 dates
	}

	private void handleMessage(String message) {
		// TODO Auto-generated method stub
		// Index: 0 userSending 1 - Chat_Room_ID; 2 -
		// MessageType(text,image,text+image,voice); 3:
		// Message
		String[] msg = message.split(",");
		// - Update DB char_ROOM
		// ----->>>> Insert Code here
		// Select members in room that changed state
		String sql = "Select User_ID from char_room_wharehouse where char_room_ID = msg[0]";
		ServerSideDB db = new ServerSideDB();
		db.setArgs(msg);
		db.setTask(TasksDB.notifyUsers);
		ArrayList<String> users = null;
		try {
			users = db.selectRoomUsers();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String user : users) {
			if (isOnline(user) && !user.equals(msg[0])) {
				updateUser(db, user);
			}
		}
		// notify room members if online to request update.
		notifyOnline();

	}

	private void updateUser(ServerSideDB db, String user) {
		// TODO Auto-generated method stub
		// fetch user socket from map and send message to " USER "

	}

	private boolean isOnline(String user) {
		if (onlineUsers.get(user) != null) {
			return true;
		}
		return false;
	}

	private void notifyOnline() {
		// prepovtarq se sus ServerSideDB notify

	}
	
	private void createTable() {
		ServerSideDB db = new ServerSideDB();
		db.createTables();{
			
		}
	}

}
