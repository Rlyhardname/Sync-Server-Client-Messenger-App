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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;

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
	private JButton btnSendFile;
	// pICK FILE BUTTON

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

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @param client2
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(1200, 100, 600, 300);
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
		// pravish buttona i go vkarvash v panel
		panel = new JPanel();
		panel.add(textField);
		panel.add(send);
		panel.add(newClient);
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
		btnSendFile = new JButton("Send File");
		btnSendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.getOutput().println("Zdr"+","+client.getUsername()+","+ 1+"," + "sendFile");
				client.sendFile("E:\\Triangle.jpg");
				System.out.print("Sending File...");
				
			}
		});
		panel.add(btnSendFile);

		ComponentListenerCustom listen = new ComponentListenerCustom(this);
		friendList = new JPanel();
		friendList.setLayout(new GridLayout(5, 1, 0, 0));
		friendOne = new JLabel("Friend 1: room1 - Acc 1-2   ");
		friendOne.setHorizontalAlignment(SwingConstants.CENTER);
		friendList.add(friendOne);
		friendOne.addMouseListener(listen);
		friendTwo = new JLabel("Friend 2: room2 - Acc 1-3   ");
		friendTwo.setHorizontalAlignment(SwingConstants.CENTER);
		friendList.add(friendTwo);
		friendTwo.addMouseListener(listen);
		friendThree = new JLabel("Friend 3: room3 - Acc 1-4   ");
		friendThree.setHorizontalAlignment(SwingConstants.CENTER);
		friendList.add(friendThree);
		friendThree.addMouseListener(listen);
		friendFour = new JLabel("Group chat 1: room4 - Acc 1-2-3");
		friendFour.setHorizontalAlignment(SwingConstants.CENTER);
		friendList.add(friendFour);
		friendFour.addMouseListener(listen);
		friendFive = new JLabel("Group chat 2: room5 Acc 1-2-4-5");
		friendFive.setHorizontalAlignment(SwingConstants.CENTER);
		friendList.add(friendFive);
		friendFive.addMouseListener(listen);
		frame.getContentPane().add(friendList, BorderLayout.EAST);

		
		Thread tr1 = new Thread(new Runnable() {
			@Override
			public void run() {
				send.addActionListener(e -> selectionButtonPressed());

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
						client.sendMessage("ClosingClient" +"," + client.getUsername());
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
		
		frame.pack();
	}

	private Object selectionButtonPressed1() {
		ClientLoginGUI.startGUI();
		return null;
	}

	private Object selectionButtonPressed() {
		String title = frame.getTitle();
		int room = Character.getNumericValue(title.charAt(title.length()-2));
		String msg = textField.getText().toString() + "," + client.getUsername() + "," + room + ","+ "TextMessage";
		client.sendMessage(msg);
		textField.setText("");

		return null;
	}

	public void concattArea(String msg) {
		textArea.append((msg + "\n"));
	}
	
	public void sendFile() {
		// logika za prashtane
		// message = "sendFile,разширение,
	}
}
