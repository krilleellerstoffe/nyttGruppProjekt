package view;

import controller.ClientController;
import model.Message;
import model.User;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class ClientConsole extends JPanel implements PropertyChangeListener {

    private ClientController controller;
    private User[] connectedUsers;
    private ArrayList<User> contacts = new ArrayList<User>();
    private JFileChooser fileChooser = new JFileChooser();
    private JTextArea inputWindow = new JTextArea("Write a message...");
    private JButton sendButton = new JButton("send");
    private JButton logoutButton = new JButton("log out");
    private JButton addFileButton = new JButton("Add a photo");
    private JButton addReceiverButton = new JButton("Add a receiver");
    private JList messageWindow = new JList();
    private JList contactWindow = new JList();
    private MRecipientsFrame mRecipientsFrame;
    private JPanel eastPanel;
    private JScrollPane scrollPane;
    private JFrame frame;

    public ClientConsole(ClientController client) {
        this.controller = client;
        Border border = this.getBorder();
        Border margin = BorderFactory.createLineBorder(Color.BLACK, 1);
        setBorder(new CompoundBorder(border, margin));
        setSize(800, 1000);
        setLayout(new BorderLayout());
        setVisible(true);
        setupComponents();
        setupListeners();
        setupFileChooser();
        controller.messageClient.addPropertyChangeListener(this);
    }

    private void setupComponents() {
        frame = new JFrame();
        frame.setTitle("Chat console");
        frame.setBounds(100, 100, 820, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(this);

        eastPanel = new JPanel(new BorderLayout());
        add(eastPanel, BorderLayout.EAST);
        eastPanel.setPreferredSize(new Dimension(800, 1000));
        eastPanel.setLayout(new BorderLayout());
        Border border = eastPanel.getBorder();
        Border margin = BorderFactory.createLineBorder(Color.BLACK, 1);
        setBorder(new CompoundBorder(border, margin));
        setVisible(true);

        messageWindow.setVisible(true);
        messageWindow.setBackground(new Color(0xECECF3));
        messageWindow.setPreferredSize(new Dimension(100, 100));

        scrollPane = new JScrollPane();
        scrollPane.setVisible(true);
        scrollPane.setAutoscrolls(true);
       // scrollPane.add(messageWindow);
        scrollPane.setViewportView(messageWindow);
        eastPanel.add(scrollPane);
        //scrollPane.setViewportView(messageWindow);
        eastPanel.add(inputWindow, BorderLayout.SOUTH);
        inputWindow.setVisible(true);
        inputWindow.setRows(3);
        inputWindow.setForeground(Color.DARK_GRAY);

        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setPreferredSize(new Dimension(150, 150));
        westPanel.setLayout(new BorderLayout());
        westPanel.setVisible(true);
        westPanel.add(contactWindow, BorderLayout.CENTER);

        eastPanel.add(addFileButton, BorderLayout.EAST);
        eastPanel.add(addReceiverButton, BorderLayout.NORTH);

        contactWindow.setVisible(true);

        eastPanel.add(westPanel, BorderLayout.WEST);
        westPanel.add(sendButton, BorderLayout.SOUTH);
        westPanel.add(logoutButton, BorderLayout.PAGE_START);
    }

    private void setupListeners() {

        ButtonListener listener = new ButtonListener();
        sendButton.addActionListener(listener);
        logoutButton.addActionListener(listener);
        addFileButton.addActionListener(listener);
        addReceiverButton.addActionListener(listener);

        inputWindow.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                inputWindow.setText("");
            }

            public void focusLost(FocusEvent e) {
                if (inputWindow.getText() == "") {
                    inputWindow.setText("Write your message here...");
                }
            }
        });
    }

    private void setupFileChooser() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JPG, PNG, JPEG & GIF Images", "jpg", "gif", "jpeg", "png");
        fileChooser.setFileFilter(filter);
    }

    private class ButtonListener implements ActionListener {

        String uploadedFile;
        User[] recipients;
        String text;

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == sendButton) {
                text = inputWindow.getText();
                recipients = mRecipientsFrame.getRecipients();
                if (uploadedFile != null) {
                    controller.sendMessage(text, uploadedFile, recipients);
                } else {
                    controller.sendMessage(text, recipients);
                }
            } else if (e.getSource() == logoutButton) {
                JOptionPane.showMessageDialog(null, "You are now logged out");
                controller.disconnectClient();
                frame.dispose();
            } else if (e.getSource() == addFileButton) {
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    uploadedFile = fileChooser.getSelectedFile().getPath();
                    JOptionPane.showMessageDialog(null, "Image " + fileChooser.getSelectedFile().getPath() + "successfully uploaded");
                }
            } else if (e.getSource() == addReceiverButton) {
                mRecipientsFrame = new MRecipientsFrame(controller);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("message")) {
            Message message = (Message) evt.getNewValue();
            updateMessageWindow(message);
            if (message.getIcon() != null) {
               // JOptionPane.showMessageDialog(null, message.getSender().getUserName() + " sent you a message", "A new Message", message.getSender().getImage());
                JOptionPane.showMessageDialog(null, message.getText(), "A New Message", JOptionPane.INFORMATION_MESSAGE, message.getIcon());
            }
        }
        if (evt.getPropertyName().equals("connectedUsers")) {
            connectedUsers = (User[]) evt.getNewValue();
            controller.setConnectedUsers(connectedUsers);
        }
    }

    ArrayList<Message> msg = new ArrayList<>();

    private void updateMessageWindow(Message message) {
        msg.add(message);
        messageWindow.setListData(msg.toArray());

        eastPanel.add(scrollPane);
        scrollPane.setViewportView(messageWindow);
    }

    public void updateContacts() {
        //contacts = controller.getContacts();
    }
}