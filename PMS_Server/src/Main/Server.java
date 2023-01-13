package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
		printActiveUsers();
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
		BufferedReader input = null;
		while (true) {
			// Въведи user И парола sus system in za test
			// input = new Scanner(link.getInputStream()); 4akame ime i parola

			ServerSideDB db = new ServerSideDB();
			try {
				input = new BufferedReader(new InputStreamReader(link.getInputStream()));
				sendMessage("Username", link);
				// System.out.println("Enter username: ");
				String username = input.readLine();
				System.out.println("Client returned username : " + username);

				if (!username.equals("create")) {

					sendMessage("Password", link);

					String password = input.readLine();
					System.out.println("Client returned password : " + password);
					if (loginUserExists(username, db, link)) {
						if (correctLoginInfo(username, password, db, link)) {
							login(username, db, link);
							db.closeConnection();
							break;
						}
					}

				} else {
					// createAccount(db, input, link);

				}
			} catch (Exception e) {
				System.out.println("Neshto grumna!");
			}
			db.closeConnection();
		}
	}

	private void login(String username, ServerSideDB db, Socket link) {
		// TODO Auto-generated method stub
		String msg = "LoginSuccess" + "," + "Succesfully logged in!";
		sendMessage(msg, link);
		onlineUsers.put(username, link);
		db.loginTime(username);
	}

	public void printActiveUsers() {
		onlineUsers.forEach((key, value) -> System.out.println(key + value));
	}

	private void createAccount(ServerSideDB db, Scanner input, Socket link) {
		// TODO Auto-generated method stub
		do {
			System.out.println("Write username for new account: ");
			String username = input.nextLine();
			if (userDataIsValid(username, 20, link)) {
				if (!db.isRegisteredUser(username)) {
					System.out.println("Write password for new account");
					String password = input.nextLine();
					if (userDataIsValid(password, 32, link)) {
						if (db.createUser(username, password)) {
							String msg = "AccountCreated" + "," + "Account Succesfully created!";
							sendMessage(msg, link);
							break;
						} else {
							String msg = "CreateAccountError" + "," + "Database error, try again!";
							sendMessage(msg, link);
						}
					}

				}
			}

		} while (true);

	}

	private boolean userDataIsValid(String userData, int i, Socket link) {
		String dataType = null;
		if (i == 20) {
			dataType = "username";
		} else if (i == 32) {
			dataType = "password";
		}
		if (userData.length() > i) {
			String msg = "LenghtError" + "," + dataType + "," + "Is too long!";
			sendMessage(msg, link);
			return false;
		}
		String[] forbbidenSymbols = { "#", "$", ",", "%", "!", "@", "^", "*", "(", ")", "+", "{", "}", "[", "]", "'",
				"\"", " Insert ", " Update ", " Delete " };

		for (String string : forbbidenSymbols) {
			if (userData.contains(string)) {
				String msg = "ForbidenSymbolError" + "," + dataType + "," + "Contrains forbidden symbol!" + ","
						+ string;
				sendMessage(msg, link);
				return false;
			}
		}

		return true;
	}

	private boolean loginUserExists(String username, ServerSideDB db, Socket link) {

		boolean condition = db.isRegisteredUser(username);
		if (condition == false) {
			String msg = "UsernameError" + "," + "There is no user: " + username + " in our databases!";
			sendMessage(msg, link);
			return false;

		}

		return true;
	}

	private boolean correctLoginInfo(String username, String password, ServerSideDB db, Socket link) {
		if (db.passwordIsCorrect(username, password) == false) {
			String msg = "PasswordError" + "," + "Password doesn't match for username " + username;
			sendMessage(msg, link);
			return false;
		}
		return true;
	}

	private void sendMessage(String msg, Socket link) {
		PrintWriter output;
		try {
			output = new PrintWriter(link.getOutputStream(), true);
			output.println(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void handleClient(Socket link) {

		System.out.println("sleep for 10 mins");
		try {
			this.sleep(1000 * 1000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		BufferedReader input = null;
		try {

			input = new BufferedReader(new InputStreamReader(link.getInputStream()));
			String update = input.readLine();
			while (!(update = input.readLine()).equals("-1")) {
				String[] data = update.split(",");
				updateServerDB(data[0], data[1]);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {

		}

		try {

			input = new BufferedReader(new InputStreamReader(link.getInputStream()));
			PrintWriter output = new PrintWriter(link.getOutputStream(), true);
			int numMessages = 0;

			String message = input.readLine();
			while (!message.equals("*CLOSE*")) {
				handleMessage(message);

				System.out.println("\nMessage received...");
				numMessages++;
				output.println("Message" + numMessages + ": " + message);
				message = input.readLine();
			}
			output.println("Messages received: " + numMessages);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("\nClose connection...");

			try {
				input.close();
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

	public void createTable() {
		ServerSideDB db = new ServerSideDB();
		if (db.createTableUser()) {
			System.out.println("DB succesfully created!");
		}
		;
		db.closeConnection();

	}

}
