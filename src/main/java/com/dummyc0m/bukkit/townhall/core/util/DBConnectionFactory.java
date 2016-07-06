package com.dummyc0m.bukkit.townhall.core.util;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Dummyc0m on 3/8/16.
 */
public class DBConnectionFactory {
    private final boolean useDataSource;
    private final String url;
    private final String username;
    private final String password;
    private DataSource dataSource;

    public DBConnectionFactory(String type, String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        if (type.equalsIgnoreCase("mysql")) {
            useDataSource = true;
            MysqlDataSource mysqlDS = new MysqlDataSource();
            mysqlDS.setURL(url);
            mysqlDS.setUser(username);
            mysqlDS.setPassword(password);
            dataSource = mysqlDS;
        } else if (type.equalsIgnoreCase("sqlite")) {
            useDataSource = true;
            SQLiteDataSource sqLiteDS = new SQLiteDataSource();
            sqLiteDS.setUrl(url);
            dataSource = sqLiteDS;
        } else {
            useDataSource = false;
        }
    }

    public Connection create() {
        if (useDataSource) {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (username != null && password != null) {
                    return DriverManager.getConnection(url, username, password);
                } else {
                    return DriverManager.getConnection(url);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
