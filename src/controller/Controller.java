package controller;

import model.MessageManager;
import view.Viewer;
import view.panels.Menu;

import java.util.ArrayList;
import java.util.Date;

public class Controller {

    private model.LogFileManager serverFileManager;
    private MessageClient messageClient;
    public MessageServer messageServer;
    private Viewer menu;

    public Controller() {

        serverFileManager = new model.LogFileManager("files/log.dat");
        serverFileManager.addLog("Server started");

        menu = new Menu("Program Selection Menu", 400, 400, this);
        menu.show();
    }

    public Viewer getMenu() {
        return menu;
    }

    public boolean serverStarts() {
        messageServer = new MessageServer(new MessageManager(), 2555);

        if (messageServer.isRunning()) {
            return true;
        }

        return false;
        /*LogFileManager lfm = new LogFileManager("files/log.dat");
        lfm.addLog("A log");
        System.out.println(System.currentTimeMillis());
        for (Log log: lfm.readLogFile(System.currentTimeMillis()-10000l, System.currentTimeMillis())) { //tests for logs made within last 10 seconds
            System.out.println(log);
        }*/
    }

    public ArrayList<String> getStringFormatList(long time) {
        ArrayList<String> messages = new ArrayList<>();
        for (model.Log log : serverFileManager.readLogFile(System.currentTimeMillis() - time, System.currentTimeMillis())) { //tests for logs made within last 10 seconds
            messages.add(log.toString());
        }
        return messages;
    }

    public ArrayList<Date> getLogDates() {
        return serverFileManager.getLogDates();
    }

    public boolean connect(String ip) {

        messageClient = new MessageClient(ip, 2555);
        if (messageClient.isConnected() || messageClient.isAvailable()) {
            return true;
        }
        return false;
    }

    public model.LogFileManager getServerFileManager() {
        return serverFileManager;
    }

    public MessageServer getMessageServer() {
        return messageServer;
    }

    public static void main(String[] args) {
        new Controller();
    }
}
