package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerVer2 implements Runnable {
	
	public static final int PORT = 1337;
	public static ServerSocket serverSocket;
	public static ConcurrentHashMap<String, Socket> onlineUsers;
	public static ServerGUI serverGUI;
	
	private Socket link;
	private PrintWriter output;
	private BufferedReader input;
	private String username;
	private String password;

	ServerVer2() {
		Initialize();
	}

	public static void serverDefaultSettings(ServerGUI gui) {

		try {
			serverGUI = gui;
			serverSocket = new ServerSocket(PORT);
			onlineUsers = new ConcurrentHashMap<String, Socket>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void Initialize() {

		try {
			link = serverSocket.accept();
			output = new PrintWriter(link.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(link.getInputStream()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	


	private void connectClient() {
		System.out.println("Waiting for new client");
		Socket link = null;
		try {

			link = serverSocket.accept();
			System.out.println("ClientConnected");
			;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		authentication();
		//syncClientWithServerDB();
		handleClient(link, username);

	}

	@Override
	public void run() {

		connectClient();

	}

	private void authentication() {
		while (true) {
			ServerSideDB db = new ServerSideDB();
			String msg = "";
			try {
				msg = input.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			String[] commandUserPass = msg.split(",");
			try {
				username = commandUserPass[1];
			} catch (ArrayIndexOutOfBoundsException e) {
				continue;
			}

			System.out.println("Client returned username : " + username);

			if (!username.equals("create")) {

				try {
					password = commandUserPass[2];
				} catch (ArrayIndexOutOfBoundsException e) {
					continue;
				}

				System.out.println("Client returned password : " + password);
				if (loginUserExists(db)) {
					if (correctLoginInfo(db)) {
						login(db);
						db.closeConnection();

					}
				}

			} else {
				 try {
					createAccount(db);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	private boolean loginUserExists(ServerSideDB db) {

		boolean condition = db.isRegisteredUser(username);
		if (condition == false) {
			String msg = "UsernameError" + "," + "There is no user: " + username + " in our databases!";
			sendMessage(msg);
			return false;

		}

		return true;
	}

	private boolean correctLoginInfo(ServerSideDB db) {
		if (db.passwordIsCorrect(username, password) == false) {
			String msg = "PasswordError" + "," + "Password doesn't match for username " + username;
			sendMessage(msg);
			return false;
		}
		return true;
	}

	private void login(ServerSideDB db) {
		// TODO Auto-generated method stub
		String msg = "LoginSuccess" + "," + "Succesfully logged in!";
		System.out.println("vliza li v login?");
		sendMessage(msg);
		onlineUsers.put(username, link);
		db.loginTime(username);
	}

	private void createAccount(ServerSideDB db) throws IOException {
		// TODO Auto-generated method stub
		do {
			System.out.println("Write username for new account: ");
			String username = input.readLine();
			if (userDataIsValid(username, 20)) {
				if (!db.isRegisteredUser(username)) {
					System.out.println("Write password for new account");
					String password = input.readLine();
					if (userDataIsValid(password, 32)) {
						if (db.createUser(username, password)) {
							String msg = "AccountCreated" + "," + "Account Succesfully created!";
							sendMessage(msg);
							break;
						} else {
							String msg = "CreateAccountError" + "," + "Database error, try again!";
							sendMessage(msg);
						}
					}

				}
			}

		} while (true);

	}

	private boolean userDataIsValid(String userData, int i) {
		String dataType = null;
		if (i == 20) {
			dataType = "username";
		} else if (i == 32) {
			dataType = "password";
		}
		if (userData.length() > i) {
			String msg = "LenghtError" + "," + dataType + "," + "Is too long!";
			// sendMessage(msg, link);
			return false;
		}
		String[] forbbidenSymbols = { "#", "$", ",", "%", "!", "@", "^", "*", "(", ")", "+", "{", "}", "[", "]", "'",
				"\"", " Insert ", " Update ", " Delete " };

		for (String string : forbbidenSymbols) {
			if (userData.contains(string)) {
				String msg = "ForbidenSymbolRegister" + "," + dataType + "," + "Contrains forbidden symbol!" + ","
						+ string;
				sendMessage("ForbidenSymbolRegister",msg);
				return false;
			}
		}

		return true;
	}

	private void handleClient(Socket link, String username) {
		BufferedReader input = null;
		PrintWriter output = null;
		try {
			input = new BufferedReader(new InputStreamReader(link.getInputStream()));
			output = new PrintWriter(link.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		do {
			try {
				String entry = input.readLine();
				messageType(entry, username, link, output);

				output.println("off be");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (true);

	}

	
	private void sendMessage(String MessageType, String msg) {

		try {
			output = new PrintWriter(link.getOutputStream(), true);
			output.println(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static synchronized void printActiveUsers() {
		ServerGUI.printArea();
	}

}
