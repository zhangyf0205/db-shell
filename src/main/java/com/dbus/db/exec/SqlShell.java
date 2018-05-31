package com.dbus.db.exec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Scanner;

/**
 * Created by zhangyf on 2018/5/23.
 */
public abstract class SqlShell extends ShellConnection implements Runnable {
    protected static final String OUTPUT_TAG = "=>";
    protected volatile boolean running = false;
    protected Thread thread;
    protected Scanner scanner = new Scanner(System.in);
    protected HistoryController history;

    public SqlShell(String host, int port, String user, String password, String db) {
        super(host, port, user, password, db);
        history = new HistoryController(Math.abs((host + port + user + db).hashCode()) + "");
    }

    public void start() throws Exception {
        prepare();
        connect();
        running = true;
        thread = new Thread(this);
        thread.setName("MysqlShell");
        thread.start();
    }

    public void stop() {
        running = false;
        if (!thread.isInterrupted())
            thread.interrupt();
        close();
        scanner.close();
        history.close();
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                System.out.print("sql> ");
                String cmd = scanner.nextLine();
                process(cmd);
            } catch (InterruptedException ex) {
                stop();
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void prepare() {
        while (host == null || host.trim().length() == 0) {
            System.out.print("host: ");
            host = scanner.nextLine();
        }

        while (port == 0) {
            System.out.print("port: ");
            port = Integer.parseInt(scanner.nextLine());
        }
        while (user == null || user.trim().length() == 0) {
            System.out.print("user: ");
            user = scanner.nextLine();
        }
        while (password == null || password.trim().length() == 0) {
            System.out.print("password: ");
            password = scanner.nextLine();
        }

        while (db == null || db.trim().length() == 0) {
            System.out.print("database: ");
            db = scanner.nextLine();
        }
    }

    protected void process(String cmd) throws Exception {
        OutputStream os = System.out;
        int idx = cmd.indexOf(OUTPUT_TAG);
        if (idx > 0) {
            File file = new File(cmd.substring(idx + OUTPUT_TAG.length()).trim());
            os = new FileOutputStream(file);
            cmd = cmd.substring(0, idx).trim();
        }
        try {
            ResultSetFormatter formatter = new ResultSetFormatter(os);
            SQLExecutor exec = new SQLExecutor(conn, formatter);
            if (cmd == null) throw new IllegalArgumentException("sql 不能为空");
            if (cmd.endsWith(";")) cmd = cmd.substring(0, cmd.length() - 1);
            execCmd(exec, cmd);
        } finally {
            if (idx > 0 && os != null) {
                os.flush();
                os.close();
            }
        }
    }

    protected void execCmd(SQLExecutor exec, String cmd) throws Exception {
        String ptn = cmd.trim().toLowerCase();
        if (ptn.startsWith("select")) {
            history.add(cmd);
            exec.query(cmd);
            return;
        }
        if (ptn.startsWith("insert")) {
            exec.insert(cmd);
            return;
        }
        if (ptn.startsWith("update")) {
            exec.update(cmd);
            return;
        }

        if (ptn.startsWith("delete")) {
            exec.delete(cmd);
            return;
        }

        if (ptn.startsWith("create")) {
            exec.create(cmd);
            return;
        }

        if (ptn.startsWith("alter")) {
            exec.alter(cmd);
            return;
        }

        if (ptn.startsWith("exit")) {
            stop();
        }

        if (ptn.startsWith("history")) {
            String[] cmdArray = cmd.split("\\s");
            int showCount = 10;
            if (cmdArray.length > 1) {
                showCount = Integer.parseInt(cmdArray[1]);
            }
            List<String> his = history.list(showCount);
            for (int i = 0; i < his.size(); i++) {
                System.out.println(his.get(i));
            }
        }

        if (ptn.matches("\\d")) {
            cmd = history.get(Integer.parseInt(ptn));
            if (cmd != null && cmd.length() > 0) {
                System.out.println(cmd);
                process(cmd);
            }
        }

        if (ptn.equals("!")) {
            System.out.println(history.last());
            process(history.last());
        }
    }
}

