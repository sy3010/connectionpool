package com.example.connectionpool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;

@SpringBootApplication
public class ConnectionpoolApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ConnectionpoolApplication.class, args);
        ConnectionPool connectionPool = new ConnectionPool();
        connectionPool.setPool();
        Connection c1 = connectionPool.getConnection();
        // manual calls from here

    }

}
