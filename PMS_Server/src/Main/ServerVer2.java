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
	private Socket link;
	private PrintWriter output;
	private BufferedReader input;
	
	

	public static int operation;
	public Thread operationThread;
	public static String order;

	ServerVer2() {
		Initialize();
// operationThread = new Thread(this);
//		operationThread.start();

	}

	private static void serverDefaultSettings() {

		try {
			serverSocket = new ServerSocket(PORT);
			onlineUsers = new ConcurrentHashMap<String, Socket>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void Initialize() {

		operation = 0;
		link = null;
		Socket link;
		try {
			link = serverSocket.accept();
			output = new PrintWriter(link.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(link.getInputStream()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
