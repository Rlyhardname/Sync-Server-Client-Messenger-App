package Main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class testClient extends Thread{

	private static InetAddress host;
	private static int PORT = 1337;
	private String username;
	private String password;

	testClient(String user, String pass) {
		{
			username = user;
			password = pass;
			ClientGUI.startClientGUI();
			try {
				host = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				System.out.println("Host ID not found");
				System.exit(1);
			}
			
		}
	}

	@Override
	public void run(){
		accessServer(username, password);
	}
	
	private static void accessServer(String username2, String password2) {
		Socket link = null;
		Scanner input = null;
		Scanner userEntry = null;
		try {
			link = new Socket(host, PORT);
			input = new Scanner(link.getInputStream());
			PrintWriter output = new PrintWriter(link.getOutputStream(), true);
			userEntry = new Scanner(System.in);
			String message = "", serverMsg;
			

			do {

				serverMsg = input.nextLine();

				if (serverMsg.equals("Username")) {
					System.out.println("Entering username: Account1");
					System.out.println(serverMsg);
					output.println(username2);
				} else if (serverMsg.equals("Password")) {
					System.out.println("Entering password: password");
					System.out.println(serverMsg);
					output.println(password2);
				} else if(serverMsg.equals("LoginSuccess\" + \",\" + \"Succesfully logged in!")){
					System.out.println(serverMsg);
					message = "*CLOSE*";
				}else {
				
					System.out.println(serverMsg);
				}
//				message = userEntry.nextLine();
//				
//				message = "go";
//				
//				System.out.println("SERVER: " + serverMsg);

			} while (!message.equals("*CLOSE*"));
		} catch (IOException e) {
			e.printStackTrace();
		}
//		} finally {
//			System.out.println("Closing connection...");
//			input.close();
//			userEntry.close();
//			try {
//				link.close();
//			} catch (IOException e) {
//				System.out.println("Unable to dissconect...");
//				System.exit(1);
//			}
//		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
