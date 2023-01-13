package Main;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class LoginClientGUI {

	private JFrame frame;
	private JTextField username;
	private JTextField password;
	private TestClient client;
	private JButton signUp;
	private LoginClientGUI window;

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
		client = new TestClient();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		username = new JTextField();
		username.setPreferredSize(new Dimension(200, 50));
		password = new JTextField();
		password.setPreferredSize(new Dimension(200, 50));
		JButton login = new JButton("LOGIN");
		signUp = new JButton("SIGN UP");
		frame.add(username);
		frame.add(password);
		frame.add(login);
		frame.add(signUp);
		login.addActionListener(e -> selectionButtonPressed());

	}

	private Object selectionButtonPressed() {
		String user = username.getText();
		String pass = password.getText().toString();
		client.setUsername(user);
		client.setPassword(pass);
		signUp.setText("LOGIN FAILED");
		if (client.accessServer()) {
			frame.dispose();

			// ClientGUI.startClientGUI();
		}
		return null;
	}
}
