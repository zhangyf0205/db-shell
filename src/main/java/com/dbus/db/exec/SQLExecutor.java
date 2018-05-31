package com.dbus.db.exec;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by zhangyf on 2018/5/9.
 */
public class SQLExecutor {
    private Connection conn;
    private ResultSetFormatter formatter;

    public SQLExecutor(Connection conn, ResultSetFormatter formatter) {
        this.conn = conn;
        this.formatter = formatter;
    }

    public ResultSet query(String sql) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            formatter.format(sql, rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (statement != null) statement.close();
            } catch (Exception ex) {
            }
        }
        return rs;
    }

    public int insert(String sql) {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            int result = statement.executeUpdate(sql);
            formatter.format(sql, result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (Exception ex) {
            }
        }
    }

    public int update(String sql) {
        return insert(sql);
    }

    public void delete(String sql) {
        update(sql);
    }

    public void create(String sql) {
        update(sql);
    }

    public void alter(String sql) {
        update(sql);
    }

    public ResultSet getTables(String user) throws Exception {
        ResultSet rs = null;
        try {
            DatabaseMetaData data = conn.getMetaData();
            String[] types = {"TABLE"};
            rs = data.getTables(null, user, null, types);
            formatter.format("", rs);
        } finally {
            if (rs != null) rs.close();
        }
        return rs;
    }

    public ResultSet getColumns(String user, String table) throws Exception {
        ResultSet rs = null;
        try {
            DatabaseMetaData data = conn.getMetaData();
            rs = data.getColumns(null, user, table, null);
            formatter.format("", rs);
        } finally {
            if (rs != null) rs.close();
        }
        return rs;
    }

    public ResultSetFormatter getFormatter() {
        return formatter;
    }
}
