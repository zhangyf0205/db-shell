package com.dbus.db.exec;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by zhangyf on 2018/5/23.
 */
public class ResultSetFormatter {
    private OutputStream os;
    private List<Map<String, Object>> cache;
    private Map<String, Integer> widthMap;
    private List<Header> headers;
    private ResultSetFilter filter;

    public ResultSetFormatter(OutputStream os) {
        this.os = os;
        cache = new LinkedList<>();
        headers = new ArrayList<>();
        widthMap = new HashMap<>();
        filter = (x) -> true;
    }

    public void format(String sql, ResultSet rs) throws Exception {
        buildHeaders(rs.getMetaData());
        buildCacheData(rs);
        if (cache.isEmpty()) {
            os.write(("total rows: " + cache.size() + "\n").getBytes("utf-8"));
            os.flush();
            return;
        }
        calculateRect();
        printHeader();
        printData();
        printEnd();
        os.write(("total rows: " + cache.size() + "\n").getBytes("utf-8"));
        os.flush();
    }

    public void format(String sql, int result) throws Exception {
        os.write(("(" + result + ")execute successful.\n").getBytes("utf-8"));
        os.flush();
    }

    public void setFilter(ResultSetFilter filter) {
        this.filter = filter;
    }

    private void buildHeaders(ResultSetMetaData meta) throws Exception {
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            if (filter.filter(meta.getColumnName(i).toLowerCase()))
                headers.add(new Header(meta.getColumnName(i), meta.getColumnDisplaySize(i), meta.getColumnTypeName(i)));
        }
    }

    private void buildCacheData(ResultSet rs) throws Exception {
        while (rs.next()) {
            Map<String, Object> map = new HashMap<>();
            for (Header header : headers) {
                if (filter.filter(header.name.toLowerCase()))
                    map.put(header.name, rs.getObject(header.name));
            }
            cache.add(map);
        }
    }

    private void printHeader() throws Exception {
        StringBuilder b = new StringBuilder();
        for (Header h : headers) {
            b.append("+");
            for (int i = 0; i < widthMap.get(h.name); i++) {
                b.append("-");
            }
        }
        b.append("+\n");
        for (Header h : headers) {
            b.append("|").append(h.name);
            for (int i = 0; i < widthMap.get(h.name) - h.name.length(); i++) {
                b.append(" ");
            }
        }
        b.append("|\n");
        for (Header h : headers) {
            b.append("+");
            for (int i = 0; i < widthMap.get(h.name); i++) {
                b.append("-");
            }
        }
        b.append("+\n");
        os.write(b.toString().getBytes("utf-8"));
    }

    private void printData() throws Exception {
        for (Map<String, Object> row : cache) {
            printRow(row);
        }
    }

    private void printRow(Map<String, Object> row) throws Exception {
        StringBuilder b = new StringBuilder();
        for (Header h : headers) {
            String data = row.get(h.name) == null ? "NULL" : row.get(h.name).toString();
            b.append("|").append(data);
            for (int i = 0; i < widthMap.get(h.name) - getLength(data); i++) {
                b.append(" ");
            }
        }
        b.append("|\n");
        os.write(b.toString().getBytes("utf-8"));
    }

    private void printEnd() throws Exception {
        StringBuilder b = new StringBuilder();
        for (Header h : headers) {
            b.append("+");
            for (int i = 0; i < widthMap.get(h.name); i++) {
                b.append("-");
            }
        }
        b.append("+\n");
        os.write(b.toString().getBytes("utf-8"));
    }

    private void calculateRect() throws Exception {
        Map<String, Integer> maxWMap = new HashMap<>();
        for (Map<String, Object> row : cache) {
            for (Map.Entry<String, Object> data : row.entrySet()) {
                if (maxWMap.containsKey(data.getKey())) {
                    maxWMap.put(data.getKey(), Math.max(maxWMap.get(data.getKey()), getLength(data)));
                } else {
                    maxWMap.put(data.getKey(), getLength(data.getValue()));
                }
            }
        }

        for (Header header : headers) {
            widthMap.put(header.name, Math.max(maxWMap.get(header.name), getLength(header.name)));
        }
    }

    private int getLength(Object data) throws Exception {
        if (data == null) return "NULL".length();
        String temp = data.toString().replaceAll("[\\u4e00-\\u9fa5]", "00");
        return temp.length();
    }

    private class Header {
        String name;
        int size;
        String type;

        public Header(String name, int size, String type) {
            this.name = name;
            this.size = size;
            this.type = type;
        }
    }
}
