package client;

import common.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AppGUI {
    private client.MessageLogic messageLogic;
    private JFrame frame;
    private JTextArea textArea;
    private JButton newClient;
    private JButton sendMessageBTN;
    private JScrollPane scrollPane;
    private JPanel panel;
    private JPanel friendsPanel;
    private JPanel header;
    private JTextField textField;
    private JComboBox<String> searchBar;
    private JComboBox<String> friendRequestBar;
    private JButton btnSendFile;
    private JList<String> jList;
    private volatile int selectedRoom;
    private volatile ConcurrentHashMap<String, Integer> rooms = new ConcurrentHashMap<>();
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
            try {
                messageLogic.handleServer(this);
            } finally {
                try {
                    messageLogic.getConnection().getLink().close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }


        }).

                start();


    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        // Init frame
        frame = new JFrame();
        frame.setBounds(800, 100, 665, 300);
        frame.setResizable(false);
        frame.setTitle("application interface");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setTitle(messageLogic.getUser().getUsername());
        selectedRoom = 1;

        // Header
        header = new JPanel(new BorderLayout());
        JLabel friendListLabel = new JLabel("Friend List            ");
        friendListLabel.setHorizontalAlignment(JLabel.RIGHT);
        //String[] testDropdown = new String[]{"one", "two", "1337"};
        searchBar = new JComboBox();
        searchBar.setPreferredSize(new Dimension(150, 20));
        searchBar.setEditable(true);
        friendRequestBar = new JComboBox<>();
        friendRequestBar.setPreferredSize(new Dimension(150, 20));
        JButton searchBTN = new JButton("\uD83D\uDD0E");
        searchBTN.setSize(new Dimension(10, 10));
        JButton friendRequestBTN = new JButton("\uD83D\uDC4B");
        friendRequestBTN.setSize(new Dimension(10, 10));
        JButton acceptFriendBTN = new JButton("\u2795");
        acceptFriendBTN.setSize(new Dimension(10, 10));
        JButton declineFriendBTN = new JButton("\u2716\uFE0F");
        declineFriendBTN.setSize(new Dimension(10, 10));
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchBar);
        searchPanel.add(searchBTN);
        searchPanel.add(friendRequestBTN);
        searchPanel.add(friendRequestBar);
        searchPanel.add(acceptFriendBTN);
        searchPanel.add(declineFriendBTN);
        header.add(friendListLabel, BorderLayout.EAST);
        header.add(searchPanel, BorderLayout.WEST);
        frame.getContentPane().add(header, BorderLayout.NORTH);

        // CENTER
        textArea = new JTextArea(10, 10);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        scrollPane = new JScrollPane(textArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Footer
        // Widgets
        textField = new JTextField(43);
        sendMessageBTN = new JButton("SEND");
        btnSendFile = new JButton("Send File");
        // newClient = new JButton("NEW CLIENT");

        // BTN panel
        panel = new JPanel();
        panel.add(textField);
        panel.add(sendMessageBTN);
        panel.add(btnSendFile);
        // panel.add(newClient);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        //frListListener listen = new frListListener(this);
        friendsPanel = new JPanel();
        friendsPanel.setLayout(new GridLayout(0, 1, 0, 0));
        jList = new JList<>();
        JPanel east = new JPanel(new BorderLayout());
        east.add(friendsPanel, BorderLayout.CENTER);
        friendsPanel.add(jList);
        frame.getContentPane().add(east
                , BorderLayout.EAST);

        new Thread(() -> btnSendFile.addActionListener(e -> {
            String[] block = FileTransfer.pickDirectory().split(",");
            String path = block[0];
            String fileName = block[1];
            sendFile(path, fileName);
        })).start();

        jList.addListSelectionListener((e) -> {
            Object obj = e.getSource();
            String val = ((JList<String>) obj).getSelectedValue();
            if (Objects.nonNull(val)) {
                Integer roomId = getRooms().get(val.trim());
                if (Objects.nonNull(roomId)) {
                    setSelectedRoom(roomId);
                }

            }

        });

        searchBTN.addActionListener((e) -> searchPerson());

        //new Thread(() -> newClient.addActionListener(e -> newClientTest())).start();
        new Thread(() -> sendMessageBTN.addActionListener(e -> sendMessage())).start();
        new Thread(() -> frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                try {
                    messageLogic.sendMessage(Command.CLOSING_CONNECTION.name() + "," + messageLogic.getUser().getUsername() + "," + "-1" + "," + "Pressing X button");
                } finally {
                    try {
                        messageLogic.getConnection().getLink().close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        })).start();

        //  frame.pack();

    }

    public int getSelectedRoom() {
        return selectedRoom;
    }

    public void setSelectedRoom(int selectedRoom) {
        System.out.println("current room changed to: " + selectedRoom);
        this.selectedRoom = selectedRoom;
    }

    private Object newClientTest() {
        LoginGUI.startGUI();
        return null;
    }


    private Object sendMessage() {
        System.err.println(selectedRoom);
        String msg = Command.TEXT_MESSAGE.name() + "," + messageLogic.getUser().getUsername() + "," + getSelectedRoom() + "," + textField.getText().toString();
        messageLogic.sendMessage(msg);
        textField.setText("");

        return null;
    }

    private void searchPerson() {
        String msg = Command.SEARCH_PERSON.name() + "," + getSearchBar().getSelectedItem();
        messageLogic.sendMessage(msg);
    }

    public JList<String> getjList() {
        return jList;
    }

    public void setjList(String[] friends) {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < friends.length; i++) {
            model.add(i, friends[i]);
        }
        jList.setModel(model);
    }

    public void changeLabelColor(JLabel label, Color color) {
        label.setForeground(color);
    }

    public void concatArea(String msg) {
        textArea.append((msg + "\n"));
    }

    public void sendFile(String path, String fileName) {
        String msg = Command.SEND_FILE.name() + "," + messageLogic.getUser().getUsername() + "," + getSelectedRoom() + "," + fileName;
        messageLogic.getConnection().getOutput().println(msg);
        FileTransfer.sendFile(path, messageLogic.getConnection());
    }

    public ConcurrentHashMap<String, Integer> getRooms() {
        System.out.println("returning current room before sending.. " + selectedRoom);
        return rooms;
    }

    public JComboBox<String> getSearchBar() {
        return searchBar;
    }

    public JComboBox<String> getFriendRequestBar() {
        return friendRequestBar;
    }
}
