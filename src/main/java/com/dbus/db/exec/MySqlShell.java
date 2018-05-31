package com.dbus.db.exec;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangyf on 2018/5/23.
 */
public class MySqlShell extends SqlShell {
    public MySqlShell(String host, int port, String user, String password, String db) {
        super(host, port, user, password, db);
    }

    @Override
    protected void execCmd(SQLExecutor exec, String cmd) throws Exception {
        Pattern p = Pattern.compile("^\\s*show\\s+tables", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(cmd);
        if (m.matches()) {
            history.add(cmd);
            exec.getFormatter().setFilter((x) -> SQLExecutors.TABLE_NAMES.contains(x));
            exec.getTables(user.toLowerCase());
            return;
        }

        p = Pattern.compile("^\\s*desc\\s+(?<table>\\S+)", Pattern.CASE_INSENSITIVE);
        m = p.matcher(cmd);
        if (m.matches()) {
            history.add(cmd);
            exec.getFormatter().setFilter((x) -> SQLExecutors.COLUMN_NAMES.contains(x));
            exec.getColumns(user.toLowerCase(), m.group("table").toLowerCase());
            return;
        }
        super.execCmd(exec, cmd);
    }
}
