package com.example.connectionpool;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnection<T> {

    void setup(String driver, String url, String username, String password, int maxConnections)
            throws ClassNotFoundException, SQLException;

    boolean releaseConnection(Connection con);

    Connection getConnection() throws SQLException;

    int getFreeConnectionCount();

    boolean validate(Connection c);
}
