package client;

import common.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

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
        selectedRoom = 1;

        // Header
        header = new JPanel(new BorderLayout());
        JLabel friendListLabel = new JLabel("Friend List            ");
        friendListLabel.setHorizontalAlignment(JLabel.RIGHT);
        header.add(friendListLabel, BorderLayout.EAST);
        frame.getContentPane().add(header, BorderLayout.NORTH);

        // CENTER
        textArea = new JTextArea(10, 10);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        scrollPane = new JScrollPane(textArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Footer
        // Widgets
        textField = new JTextField(30);
        sendMessageBTN = new JButton("SEND");
        btnSendFile = new JButton("Send File");
        newClient = new JButton("NEW CLIENT");

        // BTN panel
        panel = new JPanel();
        panel.add(textField);
        panel.add(sendMessageBTN);
        panel.add(btnSendFile);
        panel.add(newClient);
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
        new Thread(() -> newClient.addActionListener(e -> newClientTest())).start();
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

        frame.pack();
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
}
