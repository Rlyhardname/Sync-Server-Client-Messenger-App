package Main;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.util.ConcurrentModificationException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ServerGUI {

	JFrame frame;
	JButton newClientLogin;
	static JTextArea textArea;
	ServerSettings settings;

	/**
	 * Launch the application.
	 */
	public static void startGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI window = new ServerGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerGUI() {
		initialize();
		settings = new ServerSettings();
		ServerVer2 trash = new ServerVer2(this);
		Thread trash1 = new Thread(trash);
		trash1.start();
		createNewConnection();
//		server2 = new ServerVer2(this);
//		Thread serverTwoThread = new Thread(server2);
//		serverTwoThread.start();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(200, 100, 1000, 650);
		frame.setLayout(new FlowLayout());
		JPanel buttons = new JPanel();
		JPanel outputArea = new JPanel();
		JButton print = new JButton("PRINT USERS");
		newClientLogin = new JButton("NEW CLIENT LOGIN");
		textArea = new JTextArea();
		outputArea.setPreferredSize(new Dimension(750, 500));
		textArea.setPreferredSize(new Dimension(750, 500));
		outputArea.add(textArea);
		buttons.add(print);
		buttons.add(newClientLogin);
		frame.add(outputArea);
		frame.add(buttons);

		Thread printThread = new Thread(new Runnable() {

			@Override
			public void run() {
				print.addActionListener(e -> selectionButtonPressed());
				
			}
			
		});
		
		Thread openNewClientWindow = new Thread(new Runnable() {

			@Override
			public void run() {
				newClientLogin.addActionListener(e -> selectionButtonPressed1());
				
			}
			
		});
		
		printThread.start();
		openNewClientWindow.start();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void createNewConnection() {
		Thread newClient = new Thread(new Runnable() {
			@Override
			public void run() {
				ServerVer2 newServerClient = new ServerVer2();
				Thread serverTwoThread = new Thread(newServerClient);
				serverTwoThread.start();
				newServerClient.connectClient();
			}

		});
		newClient.start();

	}

	private Object selectionButtonPressed() {
		printArea();
		return null;
	}

	private Object selectionButtonPressed1() {
		ClientLoginGUI.startGUI();
		return null;
	}

	static void printArea() {
		System.out.println("printAreaMethod");
		StringBuffer concat = new StringBuffer();
		try {
			if (!ServerSettings.onlineUsers.isEmpty()) {
				System.out.println("Printing Online Users");
				ServerSettings.onlineUsers.forEach((key, value) -> concat
						.append("Active UserName: :" + key + "Active user password: " + value + "\n"));

			}

		} catch (ConcurrentModificationException | NullPointerException e) {
			System.err.println("Всички спят...");
		}
		if (!concat.equals("")) {
			textArea.setText(concat.toString());
		}

	}

}
