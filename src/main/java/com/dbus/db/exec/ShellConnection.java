package com.dbus.db.exec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by zhangyf on 2018/5/23.
 */
public abstract class ShellConnection {
    protected String host;
    protected int port;
    protected String user;
    protected String password;
    protected String db;
    protected Connection conn;

    public ShellConnection(String host, int port, String user, String password, String db) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.db = db;
    }

    public void connect() throws Exception {
        loadDriver();
        conn = DriverManager.getConnection(buildUrl(), user, password);
        System.out.println("database connected.");
    }

    protected void loadDriver() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
    }

    protected String buildUrl() {
        return String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8", host, port, db);
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

