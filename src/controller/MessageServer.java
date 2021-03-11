package controller;

import model.Message;
import model.MessageManager;
import model.User;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class MessageServer implements Runnable{


    private MessageManager messageManager;
    private ConnectedClients connectedClients;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private ServerSocket serverSocket;
    public Thread server = new Thread(this);
    private boolean running;


     public MessageServer (MessageManager messageManager, int port) {
         this.messageManager = messageManager;
         this.connectedClients = new ConnectedClients();
         try {
             serverSocket = new ServerSocket(port);
             propertyChangeSupport.firePropertyChange("value", null, " Server running");
             running = true;
         } catch (IOException e) {
             running = false;
             return;
         }

         server.start();
     }

    /**
     * Waits for a new connection from a User. Gets the user's details, then starts a thread to listen for messages
     */
    @Override
    public void run() {
         while (true) {
             try {
                 Socket socket = serverSocket.accept();
                 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                 User user = (User) ois.readObject();
                 String response = user.getUserName();
                 oos.writeObject(response);
                 oos.flush();
                 ClientHandler clientHandler = new ClientHandler(socket, ois, oos, user);//make sure same input/output streams are used
                 connectedClients.put(user, clientHandler);
                 propertyChangeSupport.firePropertyChange("value", null, user.getUserName() + " connected");
                 clientHandler.start();
             } catch (IOException | ClassNotFoundException e) {
                 e.printStackTrace();
             }
         }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private User user;

        public ClientHandler(Socket socket, ObjectInputStream ois, ObjectOutputStream oos, User user) {
            this.socket = socket;
            this.ois = ois;
            this.oos = oos;
            this.user = user;
        }

        public User getUser() {
            return user;
        }
        @Override
        public void run() {

            try {
                if(messageManager.userHasMessages(user)) {
                    for (Message savedMessage : messageManager.get(user)
                    ) {
                        send(savedMessage);
                        System.out.println("stored message sent to " + user.getUserName());
                    }
                }
            while (true) {
                    Message message = (Message) ois.readObject();
                    message.setTimeReceivedByServer(System.currentTimeMillis());
                    sendToConnectedUsers(message);
                    System.out.println("message received from " + message.getSender() + " with " + message.getRecipients().length + " recipients");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if(socket!=null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                connectedClients.remove(user);
                propertyChangeSupport.firePropertyChange("value", null, user.getUserName() + " disconnected");
            }
        }

        public void send(Message message) {
            try {
                message.setTimeMessageDelivered(System.currentTimeMillis());
                oos.writeObject(message);
                System.out.println("message sent to " + user);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void disconnect() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendToConnectedUsers(Message message) {

        User[] recipients = message.getRecipients();
        System.out.println("MessageServer" + recipients);
        for (User user: recipients) {
                ClientHandler clientHandler = connectedClients.get(user);
                if (clientHandler!=null) {
                    clientHandler.send(message);
                    propertyChangeSupport.firePropertyChange("value", null, message.getSender().getUserName() + " to " + message.getRecipients()[0].getUserName() + ": " + message.getText());
                }
                else {
                    messageManager.storeMessage(user, message);
                    propertyChangeSupport.firePropertyChange("value", null, message.getSender().getUserName() + " to " + message.getRecipients()[0].getUserName() + " message stored");
                }
            }

        }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private class ConnectedClients {

         private HashMap<User, ClientHandler> clients = new HashMap<User, ClientHandler>();

        public synchronized void put(User user, ClientHandler clientHandler) {
            clients.put(user, clientHandler);
            sendUserList();
        }

        public synchronized ClientHandler get(User user) {
            return clients.get(user);
        }

        public synchronized void remove (User user) {

            clients.remove(user);
            sendUserList();
        }

        private void sendUserList() {

            User[] connectedUsers = new User[clients.size()];
            int i = 0;
            for (ClientHandler cl: clients.values()) {
                connectedUsers[i] = cl.getUser();
                i++;
            }
            Message message = new Message(connectedUsers);
            sendToConnectedUsers(message);
        }
    }


}
