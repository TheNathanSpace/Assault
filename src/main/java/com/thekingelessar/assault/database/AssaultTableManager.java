package com.thekingelessar.assault.database;

import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class AssaultTableManager
{
    
    private static volatile AssaultTableManager instance;
    
    public AssaultTableManager()
    {
    }
    
    public static synchronized AssaultTableManager getInstance()
    {
        if (instance == null)
        {
            instance = new AssaultTableManager();
        }
        return instance;
    }
    
    public void createTable()
    {
        try
        {
            Statement statement = DatabaseClient.getInstance().getConnection().createStatement();
            statement.executeUpdate(new StringBuilder()
                    .append(String.format("CREATE TABLE [IF NOT EXISTS] %s.statistics (", DatabaseClient.getInstance().getDBName()))
                    .append("PLAYER_UUID TEXT PRIMARY KEY NOT NULL,")
                    .append("GAMES_FINISHED INTEGER DEFAULT 0,")
                    .append("KILLS INTEGER DEFAULT 0,")
                    .append("DEATHS INTEGER DEFAULT 0,")
                    .append("STARS INTEGER DEFAULT 0,")
                    .append("FASTEST_TIME REAL DEFAULT -1.0,")
                    .append("MOST_KILLS_IN_SINGLE_GAME INTEGER DEFAULT 0,")
                    .append("MOST_DEATHS_IN_SINGLE_GAME INTEGER DEFAULT 0,")
                    .append("MOST_STARS_IN_SINGLE_GAME INTEGER DEFAULT 0,")
                    .append(") [WITHOUT ROWID];")
                    .toString());
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }
    
    public void insertPlayer(Player player)
    {
        this.createTable();
        UUID uniqueId = player.getUniqueId();
        try
        {
            Statement statement = DatabaseClient.getInstance().getConnection().createStatement();
            statement.executeUpdate(new StringBuilder()
                    .append("INSERT INTO statistics (PLAYER_UUID) ")
                    .append(String.format("VALUES (%s);", uniqueId))
                    .toString());
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }
    
    public void insertValue(Player player, Statistic statistic, Object value)
    {
        this.createTable();
        UUID uniqueId = player.getUniqueId();
        try
        {
            Statement statement = DatabaseClient.getInstance().getConnection().createStatement();
            statement.executeUpdate(new StringBuilder()
                    .append(String.format("INSERT INTO statistics (PLAYER_UUID, %s) ", statistic))
                    .append(String.format("VALUES (%s, %s);", uniqueId, value))
                    .toString());
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }
    
    public void incrementValue(Player player, Statistic statistic)
    {
        this.createTable();
        UUID uniqueId = player.getUniqueId();
        Object value = this.getValue(uniqueId, statistic);
        this.insertValue(player, statistic, ((int) value) + 1);
    }
    
    public Object getValue(UUID uuid, Statistic statistic)
    {
        this.createTable();
        try
        {
            Statement statement = DatabaseClient.getInstance().getConnection().createStatement();
            ResultSet rs = statement.executeQuery(new StringBuilder()
                    .append(String.format("SELECT PLAYER_UUID, %s FROM statistics", statistic))
                    .append(String.format("WHERE PLAYER_UUID=%s", uuid))
                    .toString());
            
            if (rs.next())
            {
                return rs.getObject(statistic.toString());
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        
        return null;
    }
}
