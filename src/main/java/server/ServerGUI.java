package server;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.util.ConcurrentModificationException;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import client.LoginGUI;

public class ServerGUI {
    private ServerSettings settings;
    private static ServerGUI serverGUI;
    private JFrame frame;
    static JTextArea textArea;

    /**
     * Launch the application.
     */
    public static void startGUI() {
        EventQueue.invokeLater(() -> {
            try {
                if (Objects.isNull(serverGUI)) {
                    serverGUI = new ServerGUI();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

    /**
     * Create the application.
     */
    public ServerGUI() {
        initialize();
        settings = new ServerSettings();
        ServerSettings.serverDefaultSettings(this);
        while (true) {
            ServerVer2 serverV2 = new ServerVer2();
            if (Objects.nonNull(serverV2)) {
                createNewConnection(serverV2);
            } else {
                // TODO log
            }
        }

    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        // Init frame
        frame = new JFrame();
        frame.setBounds(200, 100, 1000, 650);
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Init client button
        JButton newClientLogin = new JButton("NEW CLIENT LOGIN");
        // Init textArea for users information
        textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(750, 500));
        // Init button pane and add buttons
        JPanel buttons = new JPanel();
        JButton print = new JButton("PRINT USERS");
        buttons.add(print);
        buttons.add(newClientLogin);
        // Init printOut area and add widgets
        JPanel outputArea = new JPanel();
        outputArea.setPreferredSize(new Dimension(750, 500));
        outputArea.add(textArea);

        // Add button and printOutArea panes to frame
        frame.add(outputArea);
        frame.add(buttons);

        new Thread(() -> print.addActionListener(e -> printUsersAndAdditionalInfo())).start();
        new Thread(() -> newClientLogin.addActionListener(e -> clientLoginTestButton())).start();
    }

    private void createNewConnection(ServerVer2 serverVer2) {
        new Thread(() -> {
            serverVer2.connectClient();
        }).start();
    }

    private Object printUsersAndAdditionalInfo() {
        printArea();
        return null;
    }

    private Object clientLoginTestButton() {
        LoginGUI.startGUI();
        ServerVer2 serverV2 = new ServerVer2();
        createNewConnection(serverV2);
        return null;
    }

    static void printArea() {
        StringBuffer concat = new StringBuffer();
        try {
            if (!ServerSettings.onlineUsers.isEmpty()) {
                ServerSettings.onlineUsers.forEach((key, value) -> concat
                        .append("Active UserName: :" + key + "Active user password: " + value + "\n"));
            }

        } catch (ConcurrentModificationException e) {
            if (concat.equals("") || concat.length() == 0) {
                textArea.setText("Бурканът преля... Обади се на техника!");
            }

        } catch (NullPointerException e1) {
            if (concat.equals("") || concat.length() == 0) {
                textArea.setText("Всички спят...");
            }

        }

        if (!concat.equals("")) {
            textArea.setText(concat.toString());
        }

    }

}
