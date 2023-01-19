package Main;

public class EntryPoint {

	EntryPoint() {

	}

	public static void main(String[] args) {

//		DbServer db = new DbServer();
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
		//	ClientLoginGUI.startGUI();
//	
//		}
		ServerGUI.startGUI();
		int xStart = 450;
		for (int j = 0; j < 4; j++) {
		
		ClientLoginGUI.startGUImany(xStart,j);
			xStart+=450;
		}
		//
		
//		
		//Test Eror;
		

		// FOR TESTING MULTIPLE CLIENTS
		// new StartClients();

	}

}
