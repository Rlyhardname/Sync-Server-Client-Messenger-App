package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TestClient extends Thread {

	private InetAddress host;
	private final int PORT = 1337;
	private String username;
	private String password;
	private BufferedReader input;
	private PrintWriter output;
	private Socket link;
	private LoginClientGUI login;



	TestClient() {
		{
			try {
				host = InetAddress.getLocalHost();
				link = new Socket(host, PORT);
				input = new BufferedReader(new InputStreamReader(link.getInputStream()));
				output = new PrintWriter(link.getOutputStream(), true);
			} catch (IOException e) {
				System.out.println("Host ID not found");
				System.exit(1);
			}

		}
	}

	@Override
	public void run() {

	
	}

	boolean accessServer() {
		
		loginMessage(username,password);
		if(isLoginSuccess()) {
			ClientGUI.startClientGUI();
			return true;
		}
		return false;
	}

	public boolean isLoginSuccess() {
		String serverMsg = receiveMessage();
		if (serverMsg.equals("LoginSuccess,Succesfully logged in!")) {
			return true;
		}
		return false;
	
	}
	
	public String receiveMessage() {
		String msg = "";
		try {
			msg = input.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}
	public boolean login() {
		String message = "", serverMsg = "";
		do {

			try {
				serverMsg = input.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (serverMsg.equals("Username")) {
				System.out.println("Entering username: Account1");
				System.out.println(serverMsg);
				output.println(username);
			} else if (serverMsg.equals("Password")) {
				System.out.println("Entering password: password");
				System.out.println(serverMsg);
				output.println(password);
			} else if (serverMsg.equals("LoginSuccess\" + \",\" + \"Succesfully logged in!")) {
				System.out.println(serverMsg);
				message = "*CLOSE*";
			} else {

				System.out.println(serverMsg);
			}

		} while (!message.equals("*CLOSE*"));
		return true;
	}
	public StringBuffer concatStrings(String... data) {
		StringBuffer concat = new StringBuffer();
		
		for (String string : data) {
			concat.append(string);
			concat.append(",");
		}

		concat.deleteCharAt(concat.length()-1);
		return concat;
	}
	public void loginMessage(String user, String pass) {
		String msg = concatStrings(user,pass).toString();
		output.println("LOGIN"+","+msg);

	}
	public void sendMessage(String msg) {

		output.println(msg);
		
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

	public InetAddress getHost() {
		return host;
	}

	public void setHost(InetAddress host) {
		this.host = host;
	}

	public int getPORT() {
		return PORT;
	}
	public LoginClientGUI getLogin() {
		return login;
	}

	public void setLogin(LoginClientGUI login) {
		this.login = login;
	}

}
