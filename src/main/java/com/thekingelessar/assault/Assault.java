package com.thekingelessar.assault;

import com.thekingelessar.assault.commands.CommandAssault;
import com.thekingelessar.assault.config.MapConfig;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.eventhandlers.RegisterHandlers;
import com.thekingelessar.assault.game.map.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Assault extends JavaPlugin
{
    static public Assault INSTANCE;
    
    static public FileConfiguration mainConfig = null;
    static public World lobbyWorld;
    static public Location lobbySpawn;
    
    static public String assaultPrefix = "§5§l[§dAssault§5] " + ChatColor.RESET;
    
    static public HashMap<String, Map> maps = new HashMap<>();
    
    static public List<GameInstance> gameInstances = new ArrayList<>();
    
    static public List<Player> waitingPlayers = new ArrayList<>();
    
    @Override
    public void onEnable()
    {
        INSTANCE = this;
        
        this.saveDefaultConfig();
        mainConfig = this.getConfig();
        lobbyWorld = Bukkit.getWorld(mainConfig.getString("lobby_world"));
        lobbySpawn = new Location(lobbyWorld, 0.5, 101.5, 0.5, 90, 0);
        
        Map mapObject = MapConfig.loadWorldFromConfig("map_saloon.yml");
        maps.put("map_saloon", mapObject);
        
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