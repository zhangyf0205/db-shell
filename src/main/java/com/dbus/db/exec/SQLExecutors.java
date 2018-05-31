package com.dbus.db.exec;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhangyf on 2018/5/31.
 */
public class SQLExecutors {
    public static final Set<String> TABLE_NAMES = createSet("table_name");
    public static final Set<String> COLUMN_NAMES = createSet("column_name", "type_name", "column_size");

    public static Set<String> createSet(String... args) {
        Set<String> set = new HashSet<>(args.length);
        for (String s : args) {
            set.add(s);
        }
        return set;
    }
}
