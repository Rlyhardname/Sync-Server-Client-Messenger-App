package server.gui;

import server.configurations.ServerSettings;
import server.dao.DataBaseConfigurations;
import server.dao.DataSourcePool;
import server.dao.SeedDB;
import server.services.ServerVer2;
import server.utils.Util;

import javax.swing.*;
import java.awt.*;
import java.util.ConcurrentModificationException;

import static server.configurations.ApplicationContext.APPLICATION_CONTEXT;

public class ServerGUI {
    public static JTextArea textArea;
    private DataBaseConfigurations dataBaseConfigurations;
    private JFrame frame;

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
        initView();

        initDB();

        APPLICATION_CONTEXT.initContext();

        initServerSettings();

        initSettingsAndLaunchServer();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initView() {
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
        JButton start = new JButton("START SERVER");
        buttons.add(start);
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

    private void initDB() {
        dataBaseConfigurations = new DataBaseConfigurations("ServerIO", "jdbc:mysql://localhost/ServerIO", "root", "dCBZXTf49PcL3L97lWXP");
        DataSourcePool.instanceOf(dataBaseConfigurations.newMysqlDataSource());
        try {
            new SeedDB(dataBaseConfigurations);
        } catch (RuntimeException e) {
            ServerGUI.textArea.append("Error occurred, check database schema " + System.lineSeparator());
        }
    }

    private void initSettingsAndLaunchServer() {
        if (initServerSettings()) {
            new Thread(this::newServerInstance).start();
            new Thread(Util::recurringPushToChatRooms).start();
        }

    }

    private void newServerInstance() {
        new ServerVer2();
    }

    private boolean initServerSettings() {
        try {
            ServerSettings.startServer();
            return true;
        } catch (RuntimeException e) {
            ServerGUI.textArea.append("server settings didn't load correctly... try relaunch if failed again look at configurations" + System.lineSeparator());
            // log(e) failed start
            // popout message that server is down
        }

        return false;
    }

    private void printUsersAndAdditionalInfo() {
        printArea();
    }

    public static void printArea() {
        String usersConcatWithPasswords = "";
        try {
            if (APPLICATION_CONTEXT.isUserCountPositive()) {
                usersConcatWithPasswords = APPLICATION_CONTEXT.concatUsersWithPasswordReturnString();
            }

        } catch (ConcurrentModificationException e) {
            //log(e);
            textArea.setText("Should not throw this, online users should be thread safe");

        } catch (NullPointerException e1) {
            textArea.setText("No active users online");

        }

        if (!usersConcatWithPasswords.isEmpty()) {
            textArea.setText(usersConcatWithPasswords);
        }

    }
}
