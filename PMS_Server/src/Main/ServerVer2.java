package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerVer2 implements Runnable {

//	public static final int PORT = 1337;
//	public static ServerSocket serverSocket;
//	public static ConcurrentHashMap<String, Socket> onlineUsers;
//	public static ServerGUI serverGUI;

	private Socket link;
	private PrintWriter output;
	private BufferedReader input;
	private String username;
	private String password;
	private String trash;

	ServerVer2() {
		Initialize();
	}

	ServerVer2(ServerGUI GUI) {
		ServerSettings.serverDefaultSettings(GUI);

	}

//	public static void serverDefaultSettings(ServerGUI gui) {
//
//		try {
//			serverGUI = gui;
//			serverSocket = new ServerSocket(PORT);
//			onlineUsers = new ConcurrentHashMap<String, Socket>();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	private void Initialize() {
		try {
			link = ServerSettings.serverSocket.accept();
			output = new PrintWriter(link.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(link.getInputStream()));
		} catch (IOException | NullPointerException e1) {
			try {
				input.close();
				output.close();
				link.close();
			} catch (IOException | NullPointerException e) {
				//e.printStackTrace();
			}
			//e1.printStackTrace();
		}

	}

	void connectClient() {
		try {

			System.out.println("ClientConnected");
			authentication();
			// syncClientWithServerDB();
			handleClient();

		} catch(NullPointerException e1) {
			System.err.println("tuka moje bi??ASDSAD");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void run() {


	}

	private void authentication() {
		while (true) {
			System.out.println("samnitelna rabota");
			ServerSideDB db = new ServerSideDB();
			String[] commandUserPass;
			String msg = "";
			try {
				msg = input.readLine();
			} catch (IOException  e) {
				e.printStackTrace();
			}catch(NullPointerException e1) {
				break;
			}
			commandUserPass = msg.split(",");

			try {
				username = commandUserPass[1];
			} catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
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
						break;
					}
				}

			} else {
				try {
					createAccount(db);
				} catch (IOException | NullPointerException e) {
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
		String msg = "LoginSuccess" + "," + "Succesfully logged in!";
		System.out.println("vliza li v login?");
		sendMessage(msg);
		ServerSettings.onlineUsers.put(username, link);
		db.loginTime(username);
	}

	private void createAccount(ServerSideDB db) throws IOException {
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
			sendMessage(msg);
			return false;
		}
		String[] forbbidenSymbols = { "#", "$", ",", "%", "!", "@", "^", "*", "(", ")", "+", "{", "}", "[", "]", "'",
				"\"", " Insert ", " Update ", " Delete " };

		for (String string : forbbidenSymbols) {
			if (userData.contains(string)) {
				String msg = "ForbidenSymbolRegister" + "," + dataType + "," + "Contrains forbidden symbol!" + ","
						+ string;
				sendMessage(msg);
				return false;
			}
		}

		return true;
	}

	private void handleClient() throws IOException {
		String msg = "";
		PrintWriter writeTo = null;
		do {

			try {

				msg = input.readLine();
				String[] userMsg = msg.split(",");
				if (userMsg[0].equals("ClosingClient"))
					break;
				Socket friend = ServerSettings.onlineUsers.get(userMsg[1]);

				writeTo = new PrintWriter(friend.getOutputStream(), true);
				writeTo.println(userMsg[0]);
				// sendMessage(msg);
			} catch (IOException  e) {
				System.out.println("sopa");
				e.printStackTrace();
			} catch(NullPointerException e1) {
				System.out.println("tuka li e be?/?");
				msg = "ExitClient";
			}
		} while (!msg.contains("ExitClient"));

		output.close();
		input.close();
		link.close();

	}

	private void sendMessage(String msg) {

		output.println(msg);

	}

	public static synchronized void printActiveUsers() {
		ServerGUI.printArea();
	}

}
