package Main;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class LoginClientGUI {

	private JFrame frame;
	private JTextField username;
	private JTextField password;
	private ClientLogic client;
	private JButton signUp;
	private LoginClientGUI window;
	private JButton login;
	private Thread tr;

	/**
	 * Launch the application.
	 */
	public static void startGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginClientGUI window = new LoginClientGUI();
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
	public LoginClientGUI() {
		initialize();
		
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		username = new JTextField();
		username.setPreferredSize(new Dimension(200, 50));
		password = new JTextField();
		password.setPreferredSize(new Dimension(200, 50));
		login = new JButton("LOGIN");
		signUp = new JButton("SIGN UP");
		username.setText("account1");
		password.setText("password");
		frame.getContentPane().add(username);
		frame.getContentPane().add(password);
		frame.getContentPane().add(login);
		frame.getContentPane().add(signUp);

		login.addActionListener(e -> selectionButtonPressed());

	}

	private Object selectionButtonPressed() {

		client = new ClientLogic();
		client.start();
		String user = username.getText();
		String pass = password.getText().toString();
		client.setUsername(user);
		client.setPassword(pass);
		signUp.setText("LOGIN FAILED");
		// client.setOperation(1);
		try {
			if(client.accessServer()) {
				ClientGUI.startClientGUI(client);
				System.out.println("vliza li 1??");
				frame.dispose();
			}
		}catch(RuntimeException e) {
			frame.dispose();
		}
		
		System.out.println("Izliza li ot access server?");
//			do {
//
//			} while (client.getOperation() == -2 || client.getOperation() == -3);
//
//			if (client.getOperation() == -2) {
//				
//			}

		return null;
	}

}
