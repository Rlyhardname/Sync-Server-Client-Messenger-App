package Main;

import java.util.Iterator;

public class StartClients {

	// Static test for loggin of multiple clients at the same time;
	StartClients() {
		String[][] clients = { {"account1","password"}, { "account2", "pass2" }, { "fuck", "hello" }, { "hello", "kitty" }, { "lalala", "lala" },
				{ "soggy", "doggy" }, { "zzzzz", "kkkk" } };
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 1; j++) {
				new testClient(clients[i][0], clients[i][1]).start();
			}
		}
	}

}
