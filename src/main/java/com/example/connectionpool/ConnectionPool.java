package com.example.connectionpool;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    IConnection connectionImpl;

    public IConnection setPool(){
        try {
            connectionImpl = ConnectionImpl.getInstance();
            connectionImpl.setup("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/parking","root",
                    "admin", 3);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return connectionImpl;
    }

    public Connection getConnection(){
        Connection con = null;
        try {
            con = connectionImpl.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public void releaseConnection(Connection con){
        connectionImpl.releaseConnection(con);
    }

    public boolean validate(Connection c){
        return connectionImpl.validate(c);
    }
    public int getFreeConnectionCount() {
        return connectionImpl.getFreeConnectionCount();
    }


}

