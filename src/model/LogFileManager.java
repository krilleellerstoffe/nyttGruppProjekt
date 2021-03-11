package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class LogFileManager {

    private String fileName;

    public LogFileManager(String fileName) {
        this.fileName = fileName;
    }

    public synchronized void addLog (String logText) {

        Log log = new Log(logText);
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName, true)))) {
            oos.writeObject(log);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized ArrayList<Log> readLogFile () {
        ArrayList<Log> logs = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(fileName)) {
            Log log;
            while (true) {
                ObjectInputStream ois = new ObjectInputStream(fis);
                log = (Log) ois.readObject();
                logs.add(log);
            }
        } catch (EOFException e) {}
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public ArrayList<Date> getLogDates() {
        ArrayList<Date> dates = new ArrayList<>();
        for (Log log : readLogFile()) {
            Date date = new Date(log.getTimeCreated());
            dates.add(date);
        }
        return dates;
    }

    /**
     * returns all the logs recorded between two given points in time
     * @param timeFrom
     * @param timeTill
     * @return
     */
    public synchronized ArrayList<Log> readLogFile(long timeFrom, long timeTill) {

        ArrayList<Log> logs = new ArrayList<>();
        for (Log log : readLogFile()) {
            int timeFromComparison = Long.compare(log.getTimeCreated(), timeFrom);
            int timeTillComparison = Long.compare(log.getTimeCreated(), timeTill);
            if (timeFromComparison >= 0 && timeTillComparison <= 0) {
                logs.add(log);
            }
        }
        return logs;
    }

    public ArrayList<String> getStringFormatList() {
        ArrayList<String> slog = new ArrayList<>();
        for (Log log : readLogFile())
        slog.add(log.toString());
        return slog;
    }
}
