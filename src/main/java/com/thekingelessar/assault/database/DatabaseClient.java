package com.thekingelessar.assault.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NetworkManager
{
    private static final String DB_NAME = "assault_statistics.db";
    private static volatile NetworkManager instance;
    private Connection connection;
    
    private NetworkManager()
    {
    }
    
    public static synchronized NetworkManager getInstance()
    {
        if (instance == null)
        {
            instance = new NetworkManager();
        }
        return instance;
    }
    
    public void getConnection()
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                connection = DriverManager.getConnection("jdbc:sqlite:" + NetworkManager.DB_NAME);
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }
    
    public void createString
}