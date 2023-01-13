package Main;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class ClientGUI {

	private JFrame frame;

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
		JTextField text = new JTextField();
		text.setPreferredSize(new Dimension(200,50));
		JButton sendText = new JButton("SEND TEXT");
		frame.add(text);
		frame.add(sendText);
		
	}
	
	

}
