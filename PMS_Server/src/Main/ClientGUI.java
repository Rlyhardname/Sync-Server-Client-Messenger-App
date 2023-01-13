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
		client = new TestClient("account1","password");
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		JTextField text = new JTextField();
		text.setPreferredSize(new Dimension(200,50));
		JButton sendText = new JButton("SEND TEXT");
		frame.add(text);
		frame.add(sendText);
		sendText.addActionListener(e -> selectionButtonPressed(client));
	}
	
//	private static void sendMessage(InetAddress localIP, int PORT) {
//		PrintWriter output;
//		try {
//		
//			output = new PrintWriter(link.getOutputStream(), true);
//			output.println(msg);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	private Object selectionButtonPressed(TestClient client) {
		client.sendMessage();
		return null;
	}

}
