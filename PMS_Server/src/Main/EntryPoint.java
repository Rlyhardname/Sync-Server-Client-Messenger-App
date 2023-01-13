package Main;

import java.awt.EventQueue;

public class EntryPoint {

	public static void main(String[] args) {

		ServerGUI.startGUI();
		Server server = new Server();
		server.start();
		ClientGUI.startClientGUI();
		
		// FOR TESTING MULTIPLE CLIENTS
		//new StartClients(); 
		
	
		

	}

}
