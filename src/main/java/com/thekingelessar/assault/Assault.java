package com.thekingelessar.assault;

import com.thekingelessar.assault.config.MapMaker;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Assault extends JavaPlugin
{
    
    static public FileConfiguration mainConfig = null;
    static public HashMap<String, MapMaker> maps = new HashMap<>();
    
    static public Assault INSTANCE;
    
    @Override
    public void onEnable()
    {
        INSTANCE = this;
        
        this.saveDefaultConfig();
        mainConfig = this.getConfig();
        
        MapMaker mapExample = new MapMaker("map_example.yml", INSTANCE.getDataFolder() + "/maps/");
        maps.put("example", mapExample);
        
        super.onEnable();
    }
    
    @Override
    public void onDisable()
    {
        super.onDisable();
    }
    
}