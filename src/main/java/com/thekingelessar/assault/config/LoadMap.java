package com.thekingelessar.assault.config;

import com.thekingelessar.assault.Assault;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LoadMap
{
    public static Map loadMap(String map)
    {
        map = "map_" + map + ".yml";
        File playerDataFileObject = new File(Assault.INSTANCE.getDataFolder(), map);
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(playerDataFileObject);
        
        if (!playerDataFileObject.exists())
        {
            playerDataFileObject.getParentFile().mkdirs();
            Assault.INSTANCE.saveResource(map, false);
        }
        
        configuration = new YamlConfiguration();
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
