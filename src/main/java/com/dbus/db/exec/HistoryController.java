package com.dbus.db.exec;

import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyf on 2018/5/30.
 */
public class HistoryController {
    private List<String> history;
    private File file;
    private BufferedWriter writer;
    private PrintWriter pw;

    public HistoryController(String historyId) {
        history = new ArrayList<>();
        String path = System.getProperty("user.dir");
        String fileName = "." + historyId + ".his";
        try {
            file = new File(path, fileName);
            if (!file.exists()) file.createNewFile();
            writer = Files.newBufferedWriter(Paths.get(fileName), StandardOpenOption.APPEND);
            pw = new PrintWriter(writer);

            List<String> list = FileUtils.readLines(file, "utf-8");
            history.addAll(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(String cmd) {
        if (cmd != null && cmd.trim().length() > 0 && !history.contains(cmd)) {
            history.add(cmd);
            pw.println(cmd);
            pw.flush();
        }
    }

    public String get(int i) {
        if (i < history.size())
            return history.get(i);
        return "";
    }

    public List<String> list(int count) {
        if (history.size() < count) count = history.size();
        List<String> subList = new ArrayList<>(history.size() - count);
        for (int i = history.size() - count; i < history.size(); i++) {
            subList.add(i + ": " + history.get(i));
        }
        return subList;
    }

    public void close() {
        pw.close();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String last() {
        return history.get(history.size() - 1);
    }
}
