package com.thekingelessar.assault.config;

import com.thekingelessar.assault.util.Coordinate;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class Map
{
    
    public String name;
    
    public Coordinate waitingPlatform;
    public HashMap<String, Coordinate> spawns = new HashMap<>();
    
    
    public Map(YamlConfiguration config)
    {
        super();
        name = config.getString("world_name");
        waitingPlatform = new Coordinate(config.getString("waiting_platform"));
        
        String redSpawnString = config.getString("spawns.RED");
        String blueSpawnString = config.getString("spawns.BLUE");
        
        spawns.put("RED", new Coordinate(redSpawnString));
        spawns.put("BLUE", new Coordinate(blueSpawnString));
        
    }
    
}
