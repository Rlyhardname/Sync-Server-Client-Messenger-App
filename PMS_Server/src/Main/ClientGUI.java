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
	private TestClient client;
	private JTextField text;

	/**
	 * Launch the application.
	 */
	public static void startClientGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI window = new ClientGUI();
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
	public ClientGUI() {
		initialize();
		
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		text = new JTextField();
		text.setPreferredSize(new Dimension(200,50));
		JButton sendText = new JButton("SEND TEXT");
		frame.add(text);
		frame.add(sendText);
		sendText.addActionListener(e -> selectionButtonPressed(client));
	}

	
	private Object selectionButtonPressed(TestClient client) {
		String msg = text.getText().toString();
		client.sendMessage(msg);
		text.setText("");
		return null;
	}

}
