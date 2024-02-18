package client;

import client.interfaces.FriendListSelectionListener;
import common.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class AppGUI {
    private MessageLogic messageLogic;
    private JFrame frame;
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
    private JButton btnSendFile;
    private int room;
    // pICK FILE BUTTON

    /**
     * Launch the application.
     */
    public static void startClientGUI(MessageLogic messageLogicArg) {
        EventQueue.invokeLater(() -> {
            try {
                System.out.println("username in appGUI constructor" + messageLogicArg.getUser().getUsername());
                AppGUI window = new AppGUI(messageLogicArg);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public AppGUI(MessageLogic messageLogicArg) {
        System.out.println("username in appGUI" + messageLogicArg);
        this.messageLogic = messageLogicArg;
        initialize();
        new Thread(() -> {
            messageLogic.handleServer(this);
        }).start();


    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        // Init frame
        frame = new JFrame();
        frame.setBounds(1200, 100, 600, 300);
        frame.setTitle("application interface");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setTitle(messageLogic.getUser().getUsername());
        room = 0;

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

        btnSendFile = new JButton("Send File");
        panel.add(btnSendFile);

        FriendListSelectionListener listen = new FriendListSelectionListener(this);
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

        btnSendFile.addActionListener(e -> {
            String path = FileTransfer.pickDirectory();
            FileTransfer.sendFile(path, messageLogic.getConnection());
            sendFile(path);
        });

        new Thread(() -> send.addActionListener(e -> sendMessage())).start();
        new Thread(() -> newClient.addActionListener(e -> newClientTest())).start();
        new Thread(() -> frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                try {
                    messageLogic.sendMessage(Command.CLOSING_CONNECTION.name() + "," + messageLogic.getUser().getUsername());
                } finally {
                    try {
                        messageLogic.getConnection().getInput().close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

//                if (!ServerSettings.onlineUsers.isEmpty()) {
//                    ServerSettings.onlineUsers.remove(messageLogic.getUser().getUsername());
//                }

            }
        })).start();

        frame.pack();
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    private Object newClientTest() {
        LoginGUI.startGUI();
        return null;
    }

    private Object sendMessage() {
        System.err.println(room);
        String msg = Command.TEXT_MESSAGE.name() + "," + messageLogic.getUser().getUsername() + "," + getRoom() + "," + textField.getText().toString();
        messageLogic.sendMessage(msg);
        textField.setText("");

        return null;
    }

    public void changeLabelColor(JLabel label, Color color) {
        label.setForeground(color);
    }

    public void concatArea(String msg) {
        textArea.append((msg + "\n"));
    }

    public void sendFile(String path) {
        String msg = "Hello" + "," + messageLogic.getUser().getUsername() + "," + getRoom() + "," + "sendFile";
        messageLogic.getConnection().getOutput().println(msg);
        FileTransfer.sendFile(path, messageLogic.getConnection());
    }

    public JLabel getFriendOne() {
        return friendOne;
    }

    public JLabel getFriendTwo() {
        return friendTwo;
    }

    public JLabel getFriendThree() {
        return friendThree;
    }

    public JLabel getFriendFour() {
        return friendFour;
    }

    public JLabel getFriendFive() {
        return friendFive;
    }

    public JLabel getFileDemonstrationRoom() {
        return fileDemonstrationRoom;
    }
}
