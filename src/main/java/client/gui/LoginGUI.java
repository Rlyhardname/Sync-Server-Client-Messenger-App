package client.gui;

import client.services.MessageLogic;
import common.Command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

import static javax.swing.JOptionPane.showMessageDialog;

public class LoginGUI {
    private JFrame frame;
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
        MessageLogic messageLogic = new MessageLogic();
        try {
            if (messageLogic.accessServer(Command.LOGIN.name(), username.getText(), password.getText())) {
                frame.dispose();
                System.out.println("username in loginGUI login button logic" + messageLogic.getUser().username());
                AppGUI.startClientGUI(messageLogic);
            } else {
                showMessageDialog(null, "Wrong credentials!");
            }

        } catch (RuntimeException e) {
            // TODO log failed access, have popup to client
            showMessageDialog(null, "Login failed... If this persists, restart the application");
            e.printStackTrace();

        }

        return null;
    }

    private Object register() {
        MessageLogic messageLogic = new MessageLogic();
        try {
            if (messageLogic.accessServer(Command.SIGN_UP.name(), username.getText(), password.getText())) {
                showMessageDialog(null, "Successfully registered user with username: " + messageLogic.getUser().username());
            } else {
                showMessageDialog(null, "Username already exists!");
            }

        } catch (RuntimeException e) {
            // TODO log failed access, have popup to client
            showMessageDialog(null, "Something went wrong...  try again....");
            e.printStackTrace();
        }

        return null;
    }

}
