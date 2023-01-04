package Main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server implements Runnable {

	public static int port = 1337;
	public static ServerSocket serverSocket;

	Server() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		do {
			this.run();
		} while (true);

	}

	private void connectClient() {
		// TODO Auto-generated method stub
		Socket link = null;
		try {
			link = serverSocket.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		synchronizeDB();
		handleClient(link);
		
	}

	private void synchronizeDB() {
		// TODO Auto-generated method stub
		
	}

	private void handleClient(Socket link) {
		
		Scanner input = null;
		try {
			
			input = new Scanner(link.getInputStream());
			PrintWriter output = new PrintWriter(link.getOutputStream(), true);
			int numMessages = 0;
			String message = input.nextLine();
			handleMessage(message);
			while (!message.equals("*CLOSE*")) {
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

	private void handleMessage(String message) {
		// TODO Auto-generated method stub
		// Index: 0 - Chat_Room_ID; 1 - MessageType(text,image,text+image,voice); 2: Message
		String[] msg = message.split(",");
		// - Update DB char_ROOM
		// ----->>>> Insert Code here
		// Select members in room that changed state
		String sql = "Select User_ID from char_room_wharehouse where char_room_ID = msg[0]";
		new ConnectToDB(msg,TasksDB.notifyUsers);
		
		// notify room members if online to request update.
		notifyOnline();
		
	}
	
	private void connectToDB() {
		
	}

	private void notifyOnline() {
		
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Server();
	}

	@Override
	public void run() {

		connectClient();

	}

}
