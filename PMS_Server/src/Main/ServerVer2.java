package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ConcurrentModificationException;

public class ServerVer2 implements Runnable {

	private Socket link;
	private PrintWriter output;
	private BufferedReader input;
	private String username;
	private String password;

	ServerVer2() {
		Initialize();
	}

	ServerVer2(ServerGUI GUI) {
		ServerSettings.serverDefaultSettings(GUI);

	}

	private void Initialize() {
		try {
			link = ServerSettings.serverSocket.accept();
			output = new PrintWriter(link.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(link.getInputStream()));
		} catch (IOException e1) {
			try {
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			e1.printStackTrace();
		}

	}

	void connectClient() {
		try {

			System.out.println("ClientConnected");
			authentication();
			// syncClientWithServerDB();
			handleClient();

		} catch (NullPointerException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {

	}

	private void authentication() {
		while (true) {
			DbServer db = new DbServer();
			String[] commandUserPass;
			String msg = "";
			try {
				msg = input.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e1) {
				break;
			}
			commandUserPass = msg.split(",");

			try {
				username = commandUserPass[1];
			} catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
				continue;
			}

			System.out.println("Client returned username : " + username);

			if (!commandUserPass[0].equals("SIGN UP")) {

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

			} else if (commandUserPass[0].equals("SIGN UP")) {
				try {
					createAccount(db);
				} catch (IOException | NullPointerException e) {
					e.printStackTrace();
				}

			}
		}

	}

	private boolean loginUserExists(DbServer db) {

		boolean condition = db.isRegisteredUser(username);
		if (condition == false) {
			String msg = "UsernameError" + "," + "There is no user: " + username + " in our databases!";
			sendMessage(msg);
			return false;

		}

		return true;
	}

	private boolean correctLoginInfo(DbServer db) {
		if (db.passwordIsCorrect(username, password) == false) {
			String msg = "PasswordError" + "," + "Password doesn't match for username " + username;
			sendMessage(msg);
			return false;
		}
		return true;
	}

	private void login(DbServer db) {
		String msg = "LoginSuccess" + "," + "Succesfully logged in!";
		System.out.println("vliza li v login?");
		sendMessage(msg);
		ServerSettings.onlineUsers.put(username, link);
		db.loginTime(username);
	}

	private void createAccount(DbServer db) throws IOException {
		do {
			if (userDataIsValid(20)) {
				if (!db.isRegisteredUser(this.username)) {
					System.out.println("Write password for new account");
					if (userDataIsValid(32)) {
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

	private boolean userDataIsValid(int i) {
		String dataType = "";
		if (i == 20) {
			dataType = "username";
		} else if (i == 32) {
			dataType = "password";
		}
		if (dataType.length() > i) {
			String msg = "LenghtError" + "," + dataType + "," + "Is too long!";
			sendMessage(msg);
			return false;
		}
		String[] forbbidenSymbols = { "#", "$", ",", "%", "!", "@", "^", "*", "(", ")", "+", "{", "}", "[", "]", "'",
				"\"", " Insert ", " Update ", " Delete " };

		for (String string : forbbidenSymbols) {
			if (dataType.contains(string)) {
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
		//PrintWriter onlineUserOutput = null;
		do {

			try {

				msg = input.readLine();
				if (msg != null) {
					String[] userMsg = msg.split(",");
					if(!msg.startsWith(",")) {
						sendMessage(userMsg[0]);
					}
					
					if (userMsg[0].equals("ClosingClient"))
						break;
				}

//				Socket friend = ServerSettings.onlineUsers.get(userMsg[1]);
//				onlineUserOutput = new PrintWriter(friend.getOutputStream(), true);
//				onlineUserOutput.println(userMsg[0]);
			} catch (IOException e) {
				System.out.println("sopa");
				e.printStackTrace();
			} catch (NullPointerException e1) {
				e1.printStackTrace();
				// msg = "ExitClient";
			}
		} while (true);

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
