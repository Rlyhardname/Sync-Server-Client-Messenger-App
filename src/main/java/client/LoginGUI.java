package client;

import server.ServerGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class LoginGUI {
    private JFrame frame;
    private MessageLogic messageLogic;
    private JTextField username;
    private JTextField password;
    private JButton signUp;
    private JButton login;

    /**
     * Launch the application.
     */
    public static void startGUI() {
        EventQueue.invokeLater(() -> {
            try {
                LoginGUI window = new LoginGUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Create the application.
     */
    public LoginGUI() {
        initialize(600);
        new Thread(() -> messageLogic = new MessageLogic());
    }

    private void initialize(int xAxis) {
        // Init GUI
        frame = new JFrame();
        frame.setBounds((1800 - xAxis), 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout());
        username = new JTextField();
        username.setPreferredSize(new Dimension(200, 50));
        password = new JTextField();
        password.setPreferredSize(new Dimension(200, 50));
        login = new JButton("LOGIN");
        signUp = new JButton("SIGN UP");
        username.setText("account1");
        password.setText("a");
        frame.getContentPane().add(username);
        frame.getContentPane().add(password);
        frame.getContentPane().add(login);
        frame.getContentPane().add(signUp);
        //Window close/X button
        new Thread(() -> frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {

            }
        })).start();
        // Login
        new Thread(() -> login.addActionListener(e -> login())).start();
        // Register
        new Thread(() -> signUp.addActionListener(e -> register())).start();
    }

    private Object login() {
        try {
            if (messageLogic.accessServer("login", username.getText(), password.getText())) {
                messageLogic.runHandleServer();
                System.out.println("Client: " + username.getText() + " has logged in!");
                AppGUI.startClientGUI(messageLogic);
                frame.dispose();
            }
        } catch (RuntimeException e) {
            signUp.setText("LOGIN FAILED");
            e.printStackTrace();
            frame.dispose();
        }

        return null;
    }

    private Object register() {
        try {
            if (messageLogic.accessServer("signup", username.getText(), password.getText())) {
                System.out.println("Successful registration of:" + username.getText() + "," + frame.getTitle());
                messageLogic.runHandleServer();
                frame.dispose();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            frame.dispose();
        }

        return null;
    }

}
