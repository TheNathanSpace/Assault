package com.thekingelessar.assault.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseClient
{
    private static final String DB_NAME = "assault_statistics.db";
    private static volatile DatabaseClient instance;
    private Connection connection;
    
    private DatabaseClient()
    {
    }
    
    public String getDBName()
    {
        return DB_NAME;
    }
    
    public static synchronized DatabaseClient getInstance()
    {
        if (instance == null)
        {
            instance = new DatabaseClient();
        }
        return instance;
    }
    
    public Connection getConnection()
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                connection = DriverManager.getConnection("jdbc:sqlite:" + DatabaseClient.DB_NAME);
            }
            return connection;
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return null;
    }
}