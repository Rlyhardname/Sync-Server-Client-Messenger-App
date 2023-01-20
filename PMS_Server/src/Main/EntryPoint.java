package Main;

public class EntryPoint {

	EntryPoint() {

	}

	public static void main(String[] args) {

		final String[] accounts = { "account1", "account2", "account3", "account4","account5", "account6", "account7", "account8",
				"account9", "account10", "account11", "account12","account13", "account14", "account15", "account16","account17", "account18", "account19", "account20"};
		ServerGUI.startGUI();
		int xStart = 450;
		

			for (int j = 0; j < 20; j++) {

				ClientLoginGUI.startManyGUI(xStart, accounts[j]);
				xStart += 450;
			}
		

	}

}
