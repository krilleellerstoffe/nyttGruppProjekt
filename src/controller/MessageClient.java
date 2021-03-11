package controller;

import controller.ClientController;
import model.Message;
import model.User;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MessageClient implements Runnable {

    Thread thread = new Thread(this);
    private Socket socket;
    private ClientController controller;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private PropertyChangeSupport changes = new PropertyChangeSupport(this);
    private User user;
    private String ipAddress;
    private int port;
    private boolean connected = false;

    public MessageClient(String ipAddress, int port) {

        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void connect(User user) throws IOException {

        socket = new Socket(ipAddress, port);
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        this.user = user;
        connected = true;
        thread.start();
    }

    @Override
    public void run() {
        try {
            oos.writeObject(user);
            oos.flush();
            String response = (String) ois.readObject();
            JOptionPane.showMessageDialog(null, response + " connected");
            listen();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public void listen() {

        try {
            while (true) {
                Message message;
                message = (Message) ois.readObject();
                if (message.getSender().getUserName().equals("Server")) {
                    User[] connectedUsers = message.getRecipients();

                    changes.firePropertyChange("connectedUsers", null, connectedUsers);
                } else {

                    changes.firePropertyChange("message", null, message);
                    JOptionPane.showMessageDialog(null, message.getText() + " from " + message.getSender()); //simple message controller.client only
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void send(Message message) {
        try {
            oos.writeObject(message);
            JOptionPane.showMessageDialog(null, "message with " + message.getRecipients().length +" recipients sent");
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void disconnect() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        System.exit(0);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void setClientController(ClientController controller) {
        this.controller = controller;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isAvailable() {
        return true; //create variable if needed
    }
}
