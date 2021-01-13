package com.thekingelessar.assault;

import com.thekingelessar.assault.config.LoadMap;
import com.thekingelessar.assault.config.Map;
import com.thekingelessar.assault.game.eventhandlers.RegisterHandlers;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Assault extends JavaPlugin
{
    
    static public FileConfiguration mainConfig = null;
    static public String lobbyWorld;
    static public String gameWorld;
    
    static public HashMap<String, Map> maps = new HashMap<>();
    
    static public Assault INSTANCE;
    
    @Override
    public void onEnable()
    {
        INSTANCE = this;
        
        this.saveDefaultConfig();
        mainConfig = this.getConfig();
        lobbyWorld = mainConfig.getString("lobby_world");
        gameWorld = mainConfig.getString("game_world");
        
        Map mapObject = LoadMap.loadMap("example");
        maps.put("example", mapObject);
        
        this.getCommand("assault").setExecutor(new com.thekingelessar.assault.commands.Assault());
        
        RegisterHandlers.registerHandlers();
        
        super.onEnable();
    }
    
    @Override
    public void onDisable()
    {
        super.onDisable();
    }
    
}