package controller;


import model.Message;
import model.User;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class ClientController {

    //private static final String SERVERADDRESS = "localhost";
    private static final int PORT = 2555;

    private static final String FILEPATH_CONTACTS = "files/contacts.dat";
    private static final String FILEPATH_CONTACTS_FOLDER = "files";

    private ArrayList<User> contacts;
    private User[] connectedUsers;
    public MessageClient messageClient;
    public User user;



    public ClientController(String ipAddress) {

        messageClient = new MessageClient(ipAddress, PORT);
        contacts = new ArrayList<User>();
        readContactsFromFile();
        messageClient.setClientController(this);
    }

    //Read contacts from file, run on startup.
    private void readContactsFromFile() {
        File folders = new File(FILEPATH_CONTACTS_FOLDER);
        if (!folders.exists()) {
            folders.mkdirs();
        }

        File contact = new File(FILEPATH_CONTACTS);
        if (contact.isFile()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(contact));
                while (true) {
                    User u = (User) ois.readObject();
                    if(!contacts.contains(u)) {
                        contacts.add(u);
                    }

                }
            } catch (EOFException EOFE) {
                // Exception is thrown and caught here when all user objects are read.
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    /**
     * Method that writes the contacts to the filepath.
     * @param users
     */

    public void writeContactsToFile(ArrayList<User> users) {

        for (User c : users) {
            contacts.add(c);
        }

        File oldContacts = new File(FILEPATH_CONTACTS);
        oldContacts.delete();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(FILEPATH_CONTACTS)));
            for (User u : users) {
                oos.writeObject(u);
            }
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User[] getConnectedUsers() {
        return connectedUsers;
    }

    public void setConnectedUsers (User[] connectedUsers) {
        this.connectedUsers = connectedUsers;
    }

    public ArrayList<User> getContacts() {
        return contacts;
    }

    public void disconnectClient() {
        messageClient.disconnect();
    }

    public void login(String username, ImageIcon img) {
        user = new User(username, img);
        try {
            messageClient.connect(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String text, String fileName, User[] recipients) {
        Message message = new Message(text, new ImageIcon(fileName), user, recipients);
        messageClient.send(message);
    }

    public void sendMessage(String text, User[] recipients) {
        Message message = new Message(text, user, recipients);
        messageClient.send(message);
    }
}
