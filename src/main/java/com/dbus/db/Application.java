package com.dbus.db;


import com.dbus.db.exec.MySqlShell;
import com.dbus.db.exec.OracleShell;
import com.dbus.db.exec.SqlShell;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

/**
 * Created by zhangyf on 2018/5/9.
 */
public class Application {
    private static String host;
    private static int port;
    private static String user;
    private static String password;
    private static String db;
    private static String dbType;
    //private static String conf;

    public static void main(String[] args) throws Exception {
        int res = parseArgs(args);
        if (res != 0) return;
        SqlShell shell;
        switch (dbType) {
            case "mysql":
                shell = new MySqlShell(host, port, user, password, db);
                break;
            case "oracle":
                shell = new OracleShell(host, port, user, password, db);
                break;
            default:
                throw new IllegalArgumentException("Only mysql and oracle are supported.");
        }
        shell.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shell.stop();
                System.out.println("db-shell exited.\n");
            }
        });
    }

    public static int parseArgs(String[] args) {
        Options opt = new Options();
        opt.addOption("h", "host", true, "host");
        opt.addOption("P", "port", true, "port");
        opt.addOption("db", "database", true, "database");
        opt.addOption("u", "user", true, "user");
        opt.addOption("p", "password", true, "password");
        opt.addOption("t", "db_type", true, "database type(mysql: 1 oracle: 2");

        //opt.addOption("-conf", "conf", true, "jdbc.properties file name");

        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(opt, args);
            host = line.getOptionValue("host");
            port = Integer.parseInt(line.getOptionValue("port") == null ? "0" : line.getOptionValue("port"));
            db = line.getOptionValue("database");
            user = line.getOptionValue("user");
            password = line.getOptionValue("password");
            dbType = line.getOptionValue("db_type");
            //conf = line.getOptionValue("conf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}