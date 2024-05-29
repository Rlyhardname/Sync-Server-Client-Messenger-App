package server.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.util.ConcurrentModificationException;

import server.configurations.ServerSettings;
import server.services.ServerVer2;
import server.dao.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import server.dao.DataBaseConfigurations;
import server.utils.Util;

public class ServerGUI {
    private ServerSettings serverSettings;
    private DataBaseConfigurations dataBaseConfigurations;
    private JFrame frame;
    public static JTextArea textArea;

    /**
     * Launch the application.
     */
    public static void startGUI() {
        EventQueue.invokeLater(() -> {
            try {
                new ServerGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

    /**
     * Create the application.
     */
    public ServerGUI() {
        serverSettings = new ServerSettings();
        dataBaseConfigurations = new DataBaseConfigurations("ServerIO", "jdbc:mysql://localhost/ServerIO", "root", "dCBZXTf49PcL3L97lWXP");
        initialize();
        DataSourcePool.instanceOf(dataBaseConfigurations.newMysqlDataSource());
        try {
            new SeedDB(dataBaseConfigurations);
        } catch (RuntimeException e) {
            ServerGUI.textArea.setText("Error occurred, check database schema");
        }

        new Thread(() -> {
            startServer();
        }).start();
        new Thread(() -> Util.pushChatRooms()).start();
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
        frame.setVisible(true);
    }

    public void startServer() {
        new ServerVer2();
    }

    private Object printUsersAndAdditionalInfo() {
        printArea();
        return null;
    }

    public static void printArea() {
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
