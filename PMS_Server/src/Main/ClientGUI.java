package Main;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;


//import com.mysql.cj.xdevapi.Client;

public class ClientGUI {

	private static InetAddress host;
	private static int PORT = 1337;
	private JFrame frame;
	private ClientLogic client;
	private JTextArea tArea;
	private JButton newClient;
	private JScrollPane scrollPane;
	String chatBox;

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
		//frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().setLayout(new GridLayout());
		
		JPanel jp = new JPanel();
		jp.setPreferredSize(new Dimension(500,500));
		JButton sendText = new JButton("SEND TEXT");
		tArea = new JTextArea(10,20);
		tArea.setForeground(new Color(135, 206, 250));
		tArea.setBackground(new Color(0, 0, 0));
		tArea.setLineWrap(true);
		//tArea.setPreferredSize(new Dimension(200,50));
		scrollPane = new JScrollPane(tArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jp.add(scrollPane);
		frame.getContentPane().add(jp);
		frame.getContentPane().add(sendText);
		chatBox = "";
		
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
		String msg = tArea.getText().toString();
		client.sendMessage(msg);
		
		String text1 = "";
		try {
			text1 +=  client.getInput().readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		text1 = "THIS IS GOING TO BE A VERY LONG TEXT!";
		tArea.append((text1+"\n"));

		return null;
	}

}
