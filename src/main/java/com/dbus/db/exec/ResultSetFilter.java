package com.dbus.db.exec;

/**
 * Created by zhangyf on 2018/5/31.
 */
public interface ResultSetFilter {
    boolean filter(String columnName);
}
