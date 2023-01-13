package Main;

import java.util.Iterator;

public class StartNewClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[][] clients = {{"account2","pass2"},{"fuck","hello"},{"hello","kitty"},{"lalala","lala"},{"soggy","doggy"},{"zzzzz","kkkk"}};
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 1; j++) {
				new testClient(clients[i][0],clients[i][1]).start();
			}
		}
		
	}

}
