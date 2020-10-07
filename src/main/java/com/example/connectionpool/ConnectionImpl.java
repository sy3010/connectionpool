package com.example.connectionpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;


public class ConnectionImpl implements IConnection<Connection> {

    Hashtable<Connection, Long> availableConnections = new Hashtable<>();
    Hashtable<Connection, Long> busyConnections = new Hashtable<>();

    private String url;
    private String username;
    private String password;

    private int maxConnections = 4;
    private int connectionIncrement = 1;
    private int connectionTimeout = 5000;
    private int expirationTime = 30000;

    private ConnectionImpl(){}

    @Override
    public void setup(String driver, String url, String username, String password, int initialConnections)
            throws ClassNotFoundException, SQLException {

        Class.forName(driver);
        this.url=url;
        this.username=username;
        this.password=password;
        for(int i=0; i<initialConnections; i++){
            availableConnections.put(this.createConnection(), System.currentTimeMillis());
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public synchronized boolean releaseConnection(Connection con) {

        if (null != con) {
            busyConnections.remove(con);
            availableConnections.put(con, System.currentTimeMillis());
            return true;
        }
        return false;
    }

    private void checkExpiry(){

        long now = System.currentTimeMillis();
        Enumeration<Connection> e = availableConnections.keys();
        while (e.hasMoreElements()) {
            Connection con = e.nextElement();
            if ((now - availableConnections.get(con)) > expirationTime) {
                // object has expired
                availableConnections.remove(con);
                close(con);
            }
        }

    }

    @Override
    public synchronized Connection getConnection() throws SQLException {

        checkExpiry();
        if (availableConnections.size() != 0) {
            Enumeration<Connection> e = availableConnections.keys();
            while (e.hasMoreElements()) {
                Connection con = e.nextElement();
                busyConnections.put(con, System.currentTimeMillis());
                availableConnections.remove(con);
                return con;
            }
        }
        else if(checkIncrement()){
            Connection con = createConnection();
            busyConnections.put(con, System.currentTimeMillis());
            return con;
        }
        else {
            try {
                Thread.sleep(connectionTimeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (availableConnections.size() != 0) {
                Enumeration<Connection> e = availableConnections.keys();
                while (e.hasMoreElements()) {
                    Connection con = e.nextElement();
                    busyConnections.put(con, System.currentTimeMillis());
                    availableConnections.remove(con);
                    return con;
                }
            }
        }
        return null; // throw timeOut Exception
    }

    private boolean checkIncrement() {

        if(availableConnections.size() + busyConnections.size() + connectionIncrement > maxConnections){
            System.out.println("Max connections created!!");
            return false;
        }
        return true;
    }


    public static IConnection getInstance(){
        return ConnectionImplHelper.INSTANCE;
    }

    private static class ConnectionImplHelper{
        private static final ConnectionImpl INSTANCE = new ConnectionImpl();
    }


    public int getFreeConnectionCount() {
        return availableConnections.size();
    }

    public boolean validate(Connection o) {
        if(o == null){
            return false;
        }
        try {
            return (!o.isClosed());
        }
        catch (SQLException e) {
            e.printStackTrace();
            return (false);
        }
    }

    private void close(Connection c){
        if(c!=null) {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
