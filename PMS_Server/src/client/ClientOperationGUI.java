package client;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;

import server.ServerSettings;

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
	private JLabel friendOne;
	private JLabel friendTwo;
	private JLabel friendThree;
	private JLabel friendFour;
	private JLabel friendFive;
	private JLabel fileDemonstrationRoom;
	public JPanel getFriendList() {
		return friendList;
	}

	private JButton btnSendFile;
	private int room;
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
		frame.setTitle(client.getUsername());

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
		room = 0;

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
		panel.add(btnSendFile);

		ComponentListenerCustom listen = new ComponentListenerCustom(this);
		friendList = new JPanel();
		friendList.setLayout(new GridLayout(5, 1, 0, 0));
		friendOne = new JLabel("1 ROOM         ");
		friendOne.setHorizontalAlignment(SwingConstants.CENTER);
		friendList.add(friendOne);
		friendOne.addMouseListener(listen);
		friendTwo = new JLabel("2 ROOM         ");
		friendTwo.setHorizontalAlignment(SwingConstants.CENTER);
		friendList.add(friendTwo);
		friendTwo.addMouseListener(listen);
		friendThree = new JLabel("3 ROOM         ");
		friendThree.setHorizontalAlignment(SwingConstants.CENTER);
		friendList.add(friendThree);
		friendThree.addMouseListener(listen);
		friendFour = new JLabel("4 ROOM         ");
		friendFour.setHorizontalAlignment(SwingConstants.CENTER);
		friendList.add(friendFour);
		friendFour.addMouseListener(listen);
		friendFive = new JLabel("5 ROOM         ");
		friendFive.setHorizontalAlignment(SwingConstants.CENTER);
		friendList.add(friendFive);
		friendFive.addMouseListener(listen);		
		fileDemonstrationRoom = new JLabel("987 fileDemonstrationRoom");
		fileDemonstrationRoom.setHorizontalAlignment(SwingConstants.CENTER);
		friendList.add(fileDemonstrationRoom);
		fileDemonstrationRoom.addMouseListener(listen);
		frame.getContentPane().add(friendList, BorderLayout.EAST);

		
		btnSendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String path = client.pickFile();
				client.setFilePath(path);
				sendFile(path);
			}
		});
		
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
						client.sendMessage("ClosingClient" + "," + client.getUsername());
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

	public int getRoom() {
		return room;
	}

	public void setRoom(int room) {
		this.room = room;
	}

	private Object selectionButtonPressed1() {
		ClientLoginGUI.startGUI();
		return null;
	}

	private Object selectionButtonPressed() {
		System.err.println(room);
		String msg = textField.getText().toString() + "," + client.getUsername() + "," + getRoom() + "," + "TextMessage";
		client.sendMessage(msg);
		textField.setText("");

		return null;
	}
	
	public void changeLabelColor(JLabel label, Color color) {
		label.setForeground(color);
	}

	public void concattArea(String msg) {
		textArea.append((msg + "\n"));
	}

	public void sendFile(String path) {
		String msg = "Hello" + "," + client.getUsername() + "," + getRoom() + "," + "sendFile";
		client.getOutput().println(msg);
		client.sendFile(path);
	
	}

	public void setFriendList(JPanel friendList) {
		this.friendList = friendList;
	}

	public JLabel getFriendOne() {
		return friendOne;
	}

	public void setFriendOne(JLabel friendOne) {
		this.friendOne = friendOne;
	}

	public JLabel getFriendTwo() {
		return friendTwo;
	}

	public void setFriendTwo(JLabel friendTwo) {
		this.friendTwo = friendTwo;
	}

	public JLabel getFriendThree() {
		return friendThree;
	}

	public void setFriendThree(JLabel friendThree) {
		this.friendThree = friendThree;
	}

	public JLabel getFriendFour() {
		return friendFour;
	}

	public void setFriendFour(JLabel friendFour) {
		this.friendFour = friendFour;
	}

	public JLabel getFriendFive() {
		return friendFive;
	}

	public void setFriendFive(JLabel friendFive) {
		this.friendFive = friendFive;
	}

	public JLabel getFileDemonstrationRoom() {
		return fileDemonstrationRoom;
	}

	public void setFileDemonstrationRoom(JLabel fileDemonstrationRoom) {
		this.fileDemonstrationRoom = fileDemonstrationRoom;
	}

}
