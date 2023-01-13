package Main;

import java.awt.EventQueue;

public class EntryPoint {

	public static void main(String[] args) {

		ServerGUI.startGUI();
		Server server = new Server();
		server.start();
		LoginClientGUI.startGUI();
		
		
		// FOR TESTING MULTIPLE CLIENTS
		//new StartClients(); 
		
	
		

	}

}
