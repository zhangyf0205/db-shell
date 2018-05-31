package com.dbus.db.exec;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangyf on 2018/5/23.
 */
public class OracleShell extends SqlShell {
    public OracleShell(String host, int port, String user, String password, String db) {
        super(host, port, user, password, db);
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
            System.out.print("schema: ");
            db = scanner.nextLine();
        }
    }

    @Override
    protected void loadDriver() throws Exception {
        Class.forName("oracle.jdbc.OracleDriver");
    }

    @Override
    protected String buildUrl() {
        return String.format("jdbc:oracle:thin:@//%s:%d/%s", host, port, db);
    }

    @Override
    protected void execCmd(SQLExecutor exec, String cmd) throws Exception {
        Pattern p = Pattern.compile("^\\s*show\\s+tables", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(cmd);
        if (m.matches()) {
            history.add(cmd);
            exec.getFormatter().setFilter((x) -> SQLExecutors.TABLE_NAMES.contains(x));
            exec.getTables(user.toUpperCase());
            return;
        }

        p = Pattern.compile("^\\s*desc\\s+(?<table>\\S+)", Pattern.CASE_INSENSITIVE);
        m = p.matcher(cmd);
        if (m.matches()) {
            history.add(cmd);
            exec.getFormatter().setFilter((x) -> SQLExecutors.COLUMN_NAMES.contains(x));
            exec.getColumns(user.toUpperCase(), m.group("table").toUpperCase());
            return;
        }
        super.execCmd(exec, cmd);
        super.execCmd(exec, cmd);
    }
}
