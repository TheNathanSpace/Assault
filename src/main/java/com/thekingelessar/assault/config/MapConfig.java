package com.thekingelessar.assault.config;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.world.map.Map;
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
        
        File mapFileObject = new File(Assault.INSTANCE.getDataFolder(), map);
        
        if (!mapFileObject.exists())
        {
            mapFileObject.getParentFile().mkdirs();
            Assault.INSTANCE.saveResource(map, false);
        }
        
        YamlConfiguration configuration = new YamlConfiguration();
        try
        {
            configuration.load(mapFileObject);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
        
        return new Map(configuration);
    }
    
}
