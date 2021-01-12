package com.thekingelessar.assault.config;

import com.thekingelessar.assault.util.Coordinate;

import java.util.HashMap;

public class MapMaker extends ConfigMaker
{
    
    public String name;
    public HashMap<String, Coordinate> spawns = new HashMap<>();
    
    public MapMaker(String fileName, String dir)
    {
        super(fileName, dir);
        name = this.getString("name");
        
        String redSpawnString = this.getString("spawns.red");
        String blueSpawnString = this.getString("spawns.red");
        
        spawns.put("red", new Coordinate(redSpawnString));
        spawns.put("blue", new Coordinate(blueSpawnString));
        
    }
    
}
