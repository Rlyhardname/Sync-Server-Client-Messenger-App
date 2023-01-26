package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSettings {

	public static final int PORT = 1337;
	public static ServerSocket serverSocket;
	public static ConcurrentHashMap<String, Socket> onlineUsers;
	public static ServerGUI serverGUI;

	ServerSettings(){
		
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
}
