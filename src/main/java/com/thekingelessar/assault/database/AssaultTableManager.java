package com.thekingelessar.assault.database;

import com.thekingelessar.assault.Assault;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public class AssaultTableManager
{
    
    private static volatile AssaultTableManager instance;
    public static final String TABLE_NAME = "statistics";
    
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
        
            Statement statement = DatabaseClient.getInstance().getConnection().createStatement();
            String createTableCommand = new StringBuilder()
                    .append(String.format("CREATE TABLE IF NOT EXISTS %s (", TABLE_NAME))
                    .append("PLAYER_UUID TEXT PRIMARY KEY NOT NULL,")
                    .append("GAMES_FINISHED INTEGER DEFAULT 0,")
                    .append("KILLS INTEGER DEFAULT 0,")
                    .append("DEATHS INTEGER DEFAULT 0,")
                    .append("STARS INTEGER DEFAULT 0,")
                    .append("FASTEST_TIME REAL DEFAULT -1.0,")
                    .append("MOST_KILLS_IN_SINGLE_GAME INTEGER DEFAULT 0,")
                    .append("MOST_DEATHS_IN_SINGLE_GAME INTEGER DEFAULT 0,")
                    .append("MOST_STARS_IN_SINGLE_GAME INTEGER DEFAULT 0")
                    .append("LAST_PLAYED INTEGER DEFAULT 0")
                    .append(");")
                    .toString();
            statement.executeUpdate(createTableCommand);
            
            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM %s;", TABLE_NAME));
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            
            List<Statistic> missingList = Arrays.asList(Statistic.values());
            for (int i = 0; i <= resultSetMetaData.getColumnCount(); i++)
            {
                String columnName = resultSetMetaData.getColumnName(i);
                missingList.remove(Statistic.valueOf(columnName));
            }
            
            for (Statistic missingStatistic : missingList)
            {
                String createColumnStatement = String.format("ALTER TABLE %s ADD COLUMN %s %s;", TABLE_NAME, missingStatistic, missingStatistic.getType());
                statement.executeUpdate(createColumnStatement);
                Assault.INSTANCE.getLogger().log(Level.INFO, "Added column " + missingStatistic + " to statistics database");
            }
            
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
                    .append(String.format("INSERT INTO %s (PLAYER_UUID) ", TABLE_NAME))
                    .append(String.format("VALUES ('%s');", uniqueId))
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
            if (this.getValue(uniqueId, Statistic.PLAYER_UUID) == null)
            {
                return;
            }
            
            Statement statement = DatabaseClient.getInstance().getConnection().createStatement();
            statement.executeUpdate(new StringBuilder()
                    .append(String.format("INSERT INTO %s (PLAYER_UUID, %s) ", TABLE_NAME, statistic))
                    .append(String.format("VALUES ('%s', %s);", uniqueId, value))
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
                    .append(String.format("SELECT PLAYER_UUID, %s FROM %s", statistic, TABLE_NAME))
                    .append(String.format("WHERE PLAYER_UUID='%s'", uuid))
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
    
    public List<Map<Statistic, Object>> getPlayerData(UUID playerUUID)
    {
        List<Map<Statistic, Object>> playerStats = new ArrayList<>();
        for (Statistic statistic : Statistic.values())
        {
            if (statistic.equals(Statistic.PLAYER_UUID)) continue;
            
            Object value = getValue(playerUUID, statistic);
            HashMap<Statistic, Object> valueMap = new HashMap<>();
            valueMap.put(statistic, value);
            playerStats.add(valueMap);
        }
        return playerStats;
    }
    
    public Map<UUID, List<Map<Statistic, Object>>> getAllPlayerData()
    {
        this.createTable();
        try
        {
            Statement statement = DatabaseClient.getInstance().getConnection().createStatement();
            ResultSet playerUUIDs = statement.executeQuery(new StringBuilder()
                    .append("SELECT PLAYER_UUID ")
                    .append(String.format("FROM %s;", TABLE_NAME))
                    .toString());
            
            Map<UUID, List<Map<Statistic, Object>>> playerMap = new HashMap<>();
            while (playerUUIDs.next())
            {
                UUID playerUUID = UUID.fromString(playerUUIDs.getString(Statistic.PLAYER_UUID.toString()));
                playerMap.put(playerUUID, this.getPlayerData(playerUUID));
            }
            return playerMap;
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        
        return null;
    }
    
    public File exportStatistics(File exportFile)
    {
        Map<UUID, List<Map<Statistic, Object>>> allPlayerData = this.getAllPlayerData();
        JSONObject jsonObject = new JSONObject(allPlayerData);
        
        if (exportFile == null)
        {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS");
            exportFile = new File(String.format("plugins/Assault/exported/statistics_%s.json", dateFormat.format(date)));
        }
        
        try
        {
            exportFile.createNewFile();
        }
        catch (IOException e)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "Couldn't export statistics—can't create file!");
            e.printStackTrace();
            return null;
        }
        
        try
        {
            FileWriter fileWriter = new FileWriter(exportFile);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();
        }
        catch (IOException e)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "Couldn't export statistics—IOException while writing!");
            e.printStackTrace();
            return null;
        }
        
        return exportFile;
    }
}
