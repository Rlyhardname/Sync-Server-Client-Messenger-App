package Main;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ConcurrentModificationException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ServerGUI {

	JFrame frame;
	static JTextArea textArea;


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
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(2200, 100, 1000, 650);
		frame.setLayout(new FlowLayout());
		JPanel buttons = new JPanel();
		JPanel outputArea = new JPanel();
		JButton print = new JButton("PRINT USERS");
		textArea = new JTextArea();
		outputArea.setPreferredSize(new Dimension(750,500));
		textArea.setPreferredSize(new Dimension(750,500));
		outputArea.add(textArea);
		buttons.add(print);
		frame.add(outputArea);
		frame.add(buttons);
	
		
		print.addActionListener(e -> selectionButtonPressed());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private Object selectionButtonPressed() {
		printArea();
		return null;
	}
	static void printArea() {
		StringBuffer concat = new StringBuffer();
		Server.order = "Print";
		new StringBuffer();
		try {
			Server.onlineUsers.forEach(
					(key, value) -> concat.append("Active UserName: :" + key + "Active user password: " + value+"\n"));
		} catch (ConcurrentModificationException e) {
			System.err.println("Nishkite neshto ne se razbraha!");
		}
		textArea.setText(concat.toString());
	}

}