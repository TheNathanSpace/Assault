package com.thekingelessar.assault.game.map;

import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.util.Coordinate;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class Map
{
    
    public String name;
    
    public Coordinate waitingPlatform;
    public HashMap<TeamColor, Coordinate> spawns = new HashMap<>();
    
    
    public Map(YamlConfiguration config)
    {
        super();
        name = config.getString("world_name");
        waitingPlatform = new Coordinate(config.getString("waiting_platform"));
        
        String redSpawnString = config.getString("spawns.RED");
        String blueSpawnString = config.getString("spawns.BLUE");
        
        spawns.put(TeamColor.RED, new Coordinate(redSpawnString));
        spawns.put(TeamColor.GREEN, new Coordinate(blueSpawnString));
        
    }
    
}
