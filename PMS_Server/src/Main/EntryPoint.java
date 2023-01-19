package Main;

import java.awt.desktop.ScreenSleepEvent;

public class EntryPoint {

	EntryPoint() {

	}

	public static void main(String[] args) {

//		DbServer db = new DbServer();
//	
//;
//		String user = "Account";
//		int counter = 0;
//		int increment = 2;
//		for (int i = 0; i < 310; i++) {
//
//			if(counter==10) {
//				counter = 0;
//				increment++;
//			}
//			counter++;
//			String buffer1 = user+i;
//			
//			db.fillRoom(increment,buffer1);
//			
//			
//			
//		}
//		db.addChatRoom(buffer1);
//		db.createUser(buffer, "a");
		
//		db.alterTable();
//		db.fillRoom();
//		DbServer.createTableUserLog();
//		DbServer.createTableChatRoomWarehouse();
//		db.createTableMessageData();
// 		db.createTableMessageData();
//		if(DbServer.createTableChatRoomWarehouse()) {
//			System.out.println("yes");
//		};

//		for (int i = 0; i < 5; i++) {
		// ClientLoginGUI.startGUI();
//	
//		}
		ServerGUI.startGUI();
		int xStart = 450;
		

			for (int j = 0; j < 20; j++) {

				ClientLoginGUI.startGUImany(xStart, j);
				xStart += 450;
			}
		

//

//		
// Test Eror;

// FOR TESTING MULTIPLE CLIENTS
// new StartClients();

	}

}
