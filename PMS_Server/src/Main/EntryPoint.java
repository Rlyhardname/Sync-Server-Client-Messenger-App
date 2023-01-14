package Main;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.ServerSocket;

public class EntryPoint {

	public static void main(String[] args) {

		
		ServerGUI.startGUI();
		LoginClientGUI.startGUI();
		// FOR TESTING MULTIPLE CLIENTS
		//new StartClients(); 
		
	
		

	}


}
