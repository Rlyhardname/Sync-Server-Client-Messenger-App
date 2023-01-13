package Main;

import java.awt.EventQueue;

public class EntryPoint {

	public static void main(String[] args) {

		GUI.startGUI();
		Server server = new Server();
		server.start();
		new StartClients();
		
	
		

	}

}
