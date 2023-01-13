package Main;

public class EntryPoint {

	public static void main(String[] args) {

		Server server = new Server();
		server.start();
		new testClient("account1", "password").start();
	
		

	}

}
