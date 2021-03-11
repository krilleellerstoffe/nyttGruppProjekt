package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controller.ClientController;
import model.User;

public class MRecipientsFrame extends JFrame {

    private JPanel contentPane;

    private JList<User> listConnected;
    private JList<User> listContacts;
    private JList<User> listRecipients;

    private ClientController controller;

    private JFrame thisWindow = this;

    private JButton btnDone;
    private JButton btnAdd;
    private JButton btnRemove;

    private ArrayList<User> recipients;

    public MRecipientsFrame(ClientController controller) {
        this.controller = controller;
        listRecipients = new JList<>();
        listContacts = new JList<>();
        listConnected = new JList<>();
        recipients = new ArrayList<User>();
        init();
        initLists();
    }

    public User[] getRecipients() {

        User[] recipientsArray = new User[recipients.size()];
        for (int i = 0; i < recipients.size(); i++) {
            recipientsArray[i] = recipients.get(i);
        }
        return recipientsArray;
    }

    private void init() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1000, 700);
        setResizable(false);
        setVisible(true);
        setBounds(100, 100, 1000, 700);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        ListListener listListener = new ListListener();

        JLabel lblNewLabel = new JLabel("Set which users are to receive the message in the list to the right");
        contentPane.add(lblNewLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        contentPane.add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 11, 237, 615);
        panel.add(scrollPane);

        listConnected.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listConnected.addListSelectionListener(listListener);
        scrollPane.setViewportView(listConnected);

        JLabel lblConnected = new JLabel("Connected Users");
        lblConnected.setHorizontalAlignment(SwingConstants.CENTER);
        scrollPane.setColumnHeaderView(lblConnected);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(270, 11, 237, 615);
        panel.add(scrollPane_1);

        listContacts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listContacts.addListSelectionListener(listListener);
        scrollPane_1.setViewportView(listContacts);

        JLabel lblContacts = new JLabel("Contacts");
        lblContacts.setHorizontalAlignment(SwingConstants.CENTER);
        scrollPane_1.setColumnHeaderView(lblContacts);

        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setBounds(600, 11, 237, 615);
        panel.add(scrollPane_2);

        listRecipients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listRecipients.addListSelectionListener(listListener);
        scrollPane_2.setViewportView(listRecipients);

        JLabel lblRecipients = new JLabel("Recipients");
        lblRecipients.setHorizontalAlignment(SwingConstants.CENTER);
        scrollPane_2.setColumnHeaderView(lblRecipients);

        btnDone = new JButton("Done");
        btnDone.setBounds(845, 587, 119, 39);
        btnDone.addActionListener(new ButtonListener());
        panel.add(btnDone);

        btnAdd = new JButton("Add");
        btnAdd.setBounds(507, 300, 93, 50);
        btnAdd.addActionListener(new ButtonListener());
        panel.add(btnAdd);

        btnRemove = new JButton("Remove");
        btnRemove.setBounds(507, 350, 93, 50);
        btnRemove.addActionListener(new ButtonListener());
        panel.add(btnRemove);
    }

    public void initLists() {
        User[] connected = controller.getConnectedUsers();
        ArrayList<User> contacts = controller.getContacts();

        ArrayList<User> connectedString = new ArrayList<User>();
        ArrayList<User> contactsString = new ArrayList<User>();



        for (User u : connected) {
            connectedString.add(u);
        }

        for (User u : contacts) {
            contactsString.add(u);
        }

        for (User u : contacts) {
            if (connectedString.contains(u)) {
                connectedString.remove(u);
            }
        }

        DefaultListModel<User> modelConnected = new DefaultListModel<>();
        DefaultListModel<User> modelContacts = new DefaultListModel<>();

        for (User s : connectedString) {
            modelConnected.addElement(s);
        }
        for (User s : contactsString) {
            modelContacts.addElement(s);
        }

        listConnected.setModel(modelConnected);
        listContacts.setModel(modelContacts);
    }

    private class ButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnDone) {
                recipients.clear();

                ListModel<User> model = listRecipients.getModel();

                for (int i = 0; i < model.getSize(); i++) {
                    User user = model.getElementAt(i);
                    if (user != null) {
                        recipients.add(user);
                    }

                }
                controller.writeContactsToFile(recipients);
                //controller.setRecipients(recipients.toArray());

                //            Ska Fixas

                thisWindow.dispose();
            }

            if (e.getSource() == btnAdd) {

                if (listRecipients.getSelectedIndex() != -1) {
                    JOptionPane.showMessageDialog(null, "Must select in the list of connected contacts, not recipients.", "ERROR", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (listConnected.getSelectedIndex() != -1) {
                        int index = listConnected.getSelectedIndex();
                        User username = listConnected.getModel().getElementAt(index);

                        ListModel<User> recipientsModel = listRecipients.getModel();
                        ArrayList<User> recipientsStrings = new ArrayList<>();

                        for (int i = 0; i < recipientsModel.getSize(); i++) {
                            recipientsStrings.add(recipientsModel.getElementAt(i));
                        }

                        if (recipientsStrings.contains(username)) {
                            JOptionPane.showMessageDialog(null, "User is already in the list of recipients", "ERROR", JOptionPane.ERROR_MESSAGE);
                        } else {
                            recipientsStrings.add(username);
                            DefaultListModel<User> newModel = new DefaultListModel<>();

                            for (User s : recipientsStrings) {
                                newModel.addElement(s);
                            }
                            listRecipients.setModel(newModel);
                        }
                    }
                    if (listContacts.getSelectedIndex() != -1) {
                        int index = listContacts.getSelectedIndex();
                        User username = listContacts.getModel().getElementAt(index);

                        ListModel<User> recipientsModel = listRecipients.getModel();
                        ArrayList<User> recipientsStrings = new ArrayList<>();

                        for (int i = 0; i < recipientsModel.getSize(); i++) {
                            recipientsStrings.add(recipientsModel.getElementAt(i));
                        }

                        if (recipientsStrings.contains(username)) {
                            JOptionPane.showMessageDialog(null, "User is already in the list of recipients", "ERROR", JOptionPane.ERROR_MESSAGE);
                        } else {
                            recipientsStrings.add(username);
                            DefaultListModel<User> newModel = new DefaultListModel<>();

                            for (User s : recipientsStrings) {
                                newModel.addElement(s);
                            }
                            listRecipients.setModel(newModel);
                        }
                    }
                }
            }

            if (e.getSource() == btnRemove) {
                if (listRecipients.getSelectedIndex() != -1) {
                    int index = listRecipients.getSelectedIndex();

                    ListModel<User> recipientsModel = listRecipients.getModel();
                    DefaultListModel<User> newModel = new DefaultListModel<>();

                    for (int i = 0; i < recipientsModel.getSize(); i++) {
                        if (i != index) {
                            newModel.addElement(recipientsModel.getElementAt(i));
                        }
                    }
                    listRecipients.setModel(newModel);
                } else {
                    JOptionPane.showMessageDialog(null, "Selection must be in the list of recipients", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class ListListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() == listConnected) {
                listContacts.clearSelection();
                listRecipients.clearSelection();
            }

            if (e.getSource() == listContacts) {
                listConnected.clearSelection();
                listRecipients.clearSelection();
            }

            if (e.getSource() == listRecipients) {
                listConnected.clearSelection();
                listContacts.clearSelection();
            }
        }
    }

}
