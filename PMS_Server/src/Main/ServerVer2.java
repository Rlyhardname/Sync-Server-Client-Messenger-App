package Main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerVer2 extends Thread {

	private Socket link;
	private PrintWriter output;
	private BufferedReader input;
	private String username;
	private String password;
	private DataOutputStream outputFile;
	private DataInputStream inputFile;

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
			inputFile = new DataInputStream(link.getInputStream());
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
			syncClientWithServerDB();
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
			ServerDB db = new ServerDB();
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
						db.insertUserLogLogin(username);
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

	private void syncClientWithServerDB() {
		ServerDB db = new ServerDB();
		String[] batch = db.getUnsendMessages(username);
		if (ServerSettings.onlineUsers.get(username) != null) {
			for (String string : batch) {

				output.println(string);
			}

		}
		db.closeConnection();
	}

	private boolean loginUserExists(ServerDB db) {

		boolean condition = db.isRegisteredUser(username);
		if (condition == false) {
			String msg = "UsernameError" + "," + "There is no user: " + username + " in our databases!";
			sendMessage(msg);
			return false;

		}

		return true;
	}

	private boolean correctLoginInfo(ServerDB db) {
		if (db.passwordIsCorrect(username, password) == false) {
			String msg = "PasswordError" + "," + "Password doesn't match for username " + username;
			sendMessage(msg);
			return false;
		}
		return true;
	}

	private void login(ServerDB db) {
		String msg = "LoginSuccess" + "," + "Succesfully logged in!";
		sendMessage(msg);
		ServerSettings.onlineUsers.put(username, link);
	}

	private void createAccount(ServerDB db) throws IOException {
		do {
			if (userDataIsValid(20)) {
				if (!db.isRegisteredUser(this.username)) {
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
			String msg = "TooManyCharacters" + "," + dataType + "," + "Is too long!";
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
		do {

			try {

				msg = input.readLine();

				if ((msg == null) || (msg.startsWith(","))) {
					continue;
				}
				String[] userMsg = msg.split(",");
				if (userMsg[0].equals("ClosingClient"))
					break;

				ServerDB db = new ServerDB();
				if (userMsg[3].equals("sendFile")) { // SEND FILE logic
					reSendFile(userMsg[2], userMsg[1], db);
					db.closeConnection();
					// userMsg[0] - message // userMsg[1] - username // userMsg[2] - Chat_room_ID //
					// // userMsg[3] = "msgType"
				}

				new Thread(new Runnable() {
					@Override
					public void run() {
						if (isTextMessage(userMsg[3])) { // SEND MSG LOGIC
							boolean isMessageStored = db.storeMessage(userMsg[1], userMsg[0],
									Integer.parseInt(userMsg[2]));
							if (isMessageStored) {
								sendMsgOnlineRoomUsers(db, userMsg[2], userMsg[1], userMsg[0]);
								db.closeConnection();
							}
						}
					}

				}).start();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e1) {
				e1.printStackTrace();
			}
		} while (true);
		ServerDB db = new ServerDB();
		db.insertUserLogout(username);
		db.closeConnection();
		output.close();
		input.close();
		link.close();

	}

	private boolean isTextMessage(String msg) {
		if (msg.equals("TextMessage")) {
			return true;
		}
		return false;
	}

	private void sendMsgOnlineRoomUsers(ServerDB db, String room, String user, String message) {
		String[] users = db.getRoomUsers(Integer.parseInt(room));
		for (String roomUser : users) {
			if (ServerSettings.onlineUsers.get(roomUser) != null) {

				Socket userSocket = ServerSettings.onlineUsers.get(roomUser);
				PrintWriter distribute = null;
				try {
					distribute = new PrintWriter(userSocket.getOutputStream(), true);
					distribute.println(message + "," + room + "," + roomUser + "," + "TextMessage");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				db.alterUserLogoutState(roomUser);
			}
			;
		}

	}

	private void sendMessage(String msg) {

		output.println(msg);

	}

	public static synchronized void printActiveUsers() {
		ServerGUI.printArea();
	}

	private void reSendFile(String room, String usernameSendingFile, ServerDB db) throws IOException

	{

		String[] usersInRoom = db.getRoomUsers(Integer.parseInt(room));
		for (String user : usersInRoom) {

			Socket onlineUser = ServerSettings.onlineUsers.get(user);
			if (onlineUser != null && !usernameSendingFile.toLowerCase().equals(user)) {
				int bytes = 0;
				PrintWriter resend = null;
				try {
					resend = new PrintWriter(onlineUser.getOutputStream(), true);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				resend.println(
						usernameSendingFile + "," + " is sendingFile in room " + "," + "room" + "," + "ReceiveFile");
				DataOutputStream outputFile1 = null;
				try {
					outputFile1 = new DataOutputStream(onlineUser.getOutputStream());
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				try {

					byte[] buffer = new byte[4 * 1024];
					long size = inputFile.readLong();

					while ((size > 0 && (bytes = inputFile.read(buffer)) != -1)) {
						outputFile1.write(buffer, 0, bytes);

						size -= bytes;
						outputFile1.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				resend.flush();
				// resend.close();
			}

		}

	}

}
