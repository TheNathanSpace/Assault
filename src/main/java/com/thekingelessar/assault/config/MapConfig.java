package com.thekingelessar.assault.config;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.map.Map;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MapConfig
{
    public static Map loadWorldFromConfig(String map)
    {
        if (!map.startsWith("map_"))
        {
            map = "map_" + map;
        }
        
        if (!map.endsWith(".yml"))
        {
            map = map + ".yml";
        }
        
        map = "maps/" + map;
        
        File playerDataFileObject = new File(Assault.INSTANCE.getDataFolder(), map);
        
        if (!playerDataFileObject.exists())
        {
            playerDataFileObject.getParentFile().mkdirs();
            Assault.INSTANCE.saveResource(map, false);
        }
        
        YamlConfiguration configuration = new YamlConfiguration();
        try
        {
            configuration.load(playerDataFileObject);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
        
        return new Map(configuration);
    }
    
}
