package Main;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;


//import com.mysql.cj.xdevapi.Client;

public class ClientGUI {

	private static InetAddress host;
	private static int PORT = 1337;
	private JFrame frame;
	private ClientLogic client;
	private JTextField text;
	private JButton newClient;

	/**
	 * Launch the application.
	 * @param tr 
	 */
	public static void startClientGUI(ClientLogic client) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI window = new ClientGUI(client);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @param tr 
	 */
	public ClientGUI(ClientLogic client) {
		initialize();
		this.client = client;
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		text = new JTextField();
		text.setPreferredSize(new Dimension(200,50));
		JButton sendText = new JButton("SEND TEXT");
		frame.getContentPane().add(text);
		frame.getContentPane().add(sendText);
		
		newClient = new JButton("newClient");
		frame.getContentPane().add(newClient);
		sendText.addActionListener(e -> selectionButtonPressed(client));
		newClient.addActionListener(e -> selectionButtonPressed1(client));
	}

	
	private Object selectionButtonPressed1(ClientLogic client2) {
		LoginClientGUI.startGUI();
		return null;
	}

	private Object selectionButtonPressed(ClientLogic client) {
		String msg = text.getText().toString();
		client.sendMessage(msg);
		
		String text1 = "";
		try {
			text1 = client.getInput().readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		text.setText(text1);
		return null;
	}

}
