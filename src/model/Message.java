package model;

import javax.swing.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {

    private String text;
    private ImageIcon icon;
    private User sender;
    private User[] recipients;
    private long timeReceivedByServer;
    private long timeMessageDelivered;

    public Message(User[] connectedUsers) {
        this.recipients = connectedUsers;
        this.text = "ConnectedUsers";
        this.sender = new User("Server"); //if the message has no sender defined, it has to be from the server itself

    }

    public Message(String text, User sender, User[] recipients) {
        this.text = text;
        this.sender = sender;
        this.recipients = recipients;
    }
    public Message(String text, String icon, User sender, User[] recipients) {
        this(text, new ImageIcon(icon), sender, recipients);
    }

    public Message(String text, ImageIcon icon, User sender, User[] recipients) {
        this.text = text;
        this.icon = icon;
        this.sender = sender;
        this.recipients = recipients;
    }

    public User getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public long getTimeReceivedByServer() {
        return timeReceivedByServer;
    }

    public void setTimeReceivedByServer(long timeReceivedByServer) {
        this.timeReceivedByServer = timeReceivedByServer;
    }

    public long getTimeMessageDelivered() {
        return timeMessageDelivered;
    }

    public void setTimeMessageDelivered(long timeMessageDelivered) {
        this.timeMessageDelivered = timeMessageDelivered;
    }

    public User[] getRecipients() {
        return recipients;
    }

    public String toString () {

        Date dateCreated = new Date(getTimeReceivedByServer());
        Date dateReceived = new Date(getTimeMessageDelivered());
        DateFormat dateFormat = new SimpleDateFormat();

        String messageContents = dateFormat.format(dateReceived) + ": \"" + getText() + "\" " + "sent by " + getSender().getUserName() +" at: " + dateFormat.format(dateCreated);
        return messageContents;
    }
}
