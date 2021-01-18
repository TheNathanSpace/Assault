package com.thekingelessar.assault;

import com.thekingelessar.assault.commands.CommandAssault;
import com.thekingelessar.assault.config.WorldConfig;
import com.thekingelessar.assault.game.map.Map;
import com.thekingelessar.assault.game.RegisterHandlers;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Assault extends JavaPlugin
{
    
    static public FileConfiguration mainConfig = null;
    static public String lobbyWorld;
    
    static public HashMap<String, Map> maps = new HashMap<>();
    
    static public Assault INSTANCE;
    
    @Override
    public void onEnable()
    {
        INSTANCE = this;
        
        this.saveDefaultConfig();
        mainConfig = this.getConfig();
        lobbyWorld = mainConfig.getString("lobby_world");
        
        Map mapObject = WorldConfig.loadWorldFromConfig("map_example.yml");
        maps.put("map_example", mapObject);
        
        this.getCommand("assault").setExecutor(new CommandAssault());
        
        RegisterHandlers.registerHandlers();
        
        super.onEnable();
    }
    
    @Override
    public void onDisable()
    {
        super.onDisable();
    }
    
}