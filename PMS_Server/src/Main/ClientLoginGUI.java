package Main;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class ClientLoginGUI {

	private JFrame frame;
	private ClientLogic client;
	private JTextField username;
	private JTextField password;
	private JButton signUp;
	private JButton login;

	/**
	 * Launch the application.
	 */
	public static void startGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientLoginGUI window = new ClientLoginGUI();
					ServerGUI.createNewConnection();
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
	public ClientLoginGUI() {
		initialize();
		client = new ClientLogic();
		client.start();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(1200, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		username = new JTextField();
		username.setPreferredSize(new Dimension(200, 50));
		password = new JTextField();
		password.setPreferredSize(new Dimension(200, 50));
		login = new JButton("LOGIN");
		signUp = new JButton("SIGN UP");
		username.setText("account1");
		password.setText("a");
		frame.getContentPane().add(username);
		frame.getContentPane().add(password);
		frame.getContentPane().add(login);
		frame.getContentPane().add(signUp);

		Thread tr = new Thread(new Runnable() {

			@Override
			public void run() {
				frame.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(WindowEvent winEvt) {

					}
				});

			}

		});
		tr.start();

		Thread tr1 = new Thread(new Runnable() {

			@Override
			public void run() {
				login.addActionListener(e -> selectionButtonPressed());
			}

		});
		tr1.start();

		Thread tr2 = new Thread(new Runnable() {

			@Override
			public void run() {
				signUp.addActionListener(e -> selectionButtonPressed1());
			}

		});
		tr2.start();

	}

	private Object selectionButtonPressed() {

		String user = username.getText();
		String pass = password.getText().toString();
		client.setUsername(user);
		client.setPassword(pass);
		signUp.setText("LOGIN FAILED");
		try {
			if (client.accessServer("login")) {
				if (!client.isStarted()) {
					client.runHandleServer();
				}
				System.out.println("Client: " + client.getUsername() + " has logged in!");
				ClientOperationGUI.startClientGUI(client);
				frame.dispose();
			}
		} catch (RuntimeException e) {
			System.out.println("NIDEI BEEEE");
			frame.dispose();
		}
		return null;
	}

	private Object selectionButtonPressed1() {

		String user = username.getText().toString();
		String pass = password.getText().toString();
		client.setUsername(user);
		client.setPassword(pass);

		try {
			if (client.accessServer("signup")) {
				System.out.println("Succesful registration of:" + client.getUsername() +"," + frame.getTitle());
				if (!client.isStarted()) {
					client.runHandleServer();
				}
				frame.dispose();
			}
		} catch (RuntimeException e) {
			System.out.println("NIDEI BEEEE");
			frame.dispose();
		}

		return null;
	}

}
