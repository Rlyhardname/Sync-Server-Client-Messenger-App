package Main;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import javax.swing.JLabel;

public class ClientOperationGUI {
	private ClientLogic client;
	JFrame frame;
	private JTextArea textArea;
	private JButton newClient;
	private JButton send;
	private JScrollPane scrollPane;
	private JPanel panel;
	private JPanel friendList;
	private JPanel header;
	private JTextField textField;
	JLabel friendOne;
	JLabel friendTwo;
	JLabel friendThree;
	JLabel friendFour;
	JLabel friendFive;

	/**
	 * Launch the application.
	 * 
	 * @param tr
	 */
	public static void startClientGUI(ClientLogic client) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientOperationGUI window = new ClientOperationGUI(client);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @param tr
	 */
	public ClientOperationGUI(ClientLogic client1) {
		initialize();
		this.client = client1;
		client.setGui(this);

	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @param client2
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(3200, 100, 600, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setTitle("frame2");

		header = new JPanel();
		frame.getContentPane().add(header, BorderLayout.NORTH);

		textArea = new JTextArea(10, 10);
		scrollPane = new JScrollPane(textArea);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		textField = new JTextField(30);
		send = new JButton("SEND");
		newClient = new JButton("NEW CLIENT");
		panel = new JPanel();
		panel.add(textField);
		panel.add(send);
		panel.add(newClient);
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		friendList = new JPanel();
		friendOne = new JLabel("account1");
		friendTwo = new JLabel("account2");
		friendThree = new JLabel("account3");
		friendFour = new JLabel("account4");
		friendFive = new JLabel("account5");
		friendList.add(friendOne);
		friendList.add(friendTwo);
		friendList.add(friendThree);
		friendList.add(friendFour);
		friendList.add(friendFive);
		frame.getContentPane().add(friendList, BorderLayout.EAST);

		ComponentListenerCustom listen = new ComponentListenerCustom(this);
		friendOne.addMouseListener(listen);
		friendTwo.addMouseListener(listen);
		friendThree.addMouseListener(listen);
		friendFour.addMouseListener(listen);
		friendFive.addMouseListener(listen);
//		friendOne.addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e) {
//				JLabel obj = (JLabel) e.getComponent();
//
//				if (obj.equals(friendOne)) {
//					frame.setTitle("account1");
//				} else if (obj == friendTwo) {
//					frame.setTitle("account2");
//				} else if (obj == friendThree) {
//					frame.setTitle("account3");
//				} else if (obj == friendFour) {
//					frame.setTitle("account4");
//				} else if (obj == friendFive) {
//					frame.setTitle("account5");
//				}
//			}
//		});

		Thread tr1 = new Thread(new Runnable() {
			@Override
			public void run() {
				send.addActionListener(e -> selectionButtonPressed());

			}

			private Object selectionLabelPressed() {
				// TODO Auto-generated method stub
				return null;
			}

		});
		tr1.start();

		Thread tr2 = new Thread(new Runnable() {

			@Override
			public void run() {
				newClient.addActionListener(e -> selectionButtonPressed1());
			}

		});
		tr2.start();

		Thread tr = new Thread(new Runnable() {

			@Override
			public void run() {
				frame.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(WindowEvent winEvt) {
						client.sendMessage("ClosingClient");
						client.getOutput().close();
						try {
							client.getInput().close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (!ServerSettings.onlineUsers.isEmpty()) {
							ServerSettings.onlineUsers.remove(client.getUsername());
						}

					}
				});

			}

		});
		tr.start();

	}

	private Object selectionButtonPressed1() {
		ClientLoginGUI.startGUI();
		return null;
	}

	private Object selectionButtonPressed() {
		String msg = textField.getText().toString() + "," + client.getUsername();
		client.sendMessage(msg);
		textField.setText("");

		return null;
	}

	public void concattArea(String msg) {
		textArea.append((msg + "\n"));
	}
}