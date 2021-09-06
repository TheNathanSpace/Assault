package com.thekingelessar.assault;

import com.thekingelessar.assault.commands.*;
import com.thekingelessar.assault.config.MapConfig;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.eventhandlers.RegisterHandlers;
import com.thekingelessar.assault.game.map.BuffShopTrait;
import com.thekingelessar.assault.game.map.ItemShopTrait;
import com.thekingelessar.assault.game.map.Map;
import com.thekingelessar.assault.game.world.WorldManager;
import com.thekingelessar.assault.util.Coordinate;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class Assault extends JavaPlugin
{
    static public Assault INSTANCE;
    
    static public boolean useHolographicDisplays;
    
    static public FileConfiguration mainConfig = null;
    static public World lobbyWorld;
    static public Location lobbySpawn;
    
    static public GameMode lobbyGamemode = GameMode.ADVENTURE;
    static public boolean forceLobbyInventory = true;
    
    static public final String ASSAULT_PREFIX = "§5§l[§d§lAssault§5§l] " + ChatColor.RESET;
    
    static public HashMap<String, Map> maps = new HashMap<>();
    
    static public List<GameInstance> gameInstances = new ArrayList<>();
    static public List<NPC> gameNPCs = new ArrayList<>();
    
    static public List<Player> waitingPlayers = new ArrayList<>();
    
    @Override
    public void onEnable()
    {
        INSTANCE = this;
        
        useHolographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        
        this.saveDefaultConfig();
        mainConfig = this.getConfig();
        try
        {
            lobbyWorld = Bukkit.getWorld(mainConfig.getString("lobby_world"));
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "lobby_world invalid");
            throw exception;
        }
        
        try
        {
            lobbySpawn = new Coordinate(mainConfig.getString("lobby_spawn")).toLocation(lobbyWorld);
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "lobby_spawn invalid");
            throw exception;
        }
        
        try
        {
            lobbyGamemode = GameMode.valueOf(mainConfig.getString("lobby_gamemode"));
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "lobby_gamemode invalid");
            throw exception;
        }
        
        try
        {
            forceLobbyInventory = mainConfig.getBoolean("force_lobby_inventory");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "force_lobby_inventory invalid");
            throw exception;
        }
        
        try
        {
            
            List<String> mapList = (List<String>) mainConfig.getList("map_list");
            for (String mapName : mapList)
            {
                Map mapObject = MapConfig.loadWorldFromConfig(mapName);
                maps.put(mapName, mapObject);
            }
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "map_list invalid");
            throw exception;
        }
        
        this.getCommand("assaulthelp").setExecutor(new CommandAssaultHelp());
        this.getCommand("all").setExecutor(new CommandAll());
        this.getCommand("respawn").setExecutor(new CommandRespawn());
        this.getCommand("forfeit").setExecutor(new CommandForfeit());
        this.getCommand("assaultadmin").setExecutor(new CommandAssaultAdmin());
        
        RegisterHandlers.registerHandlers();
        
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ItemShopTrait.class).withName("itemshoptrait"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(BuffShopTrait.class).withName("buffshoptrait"));
        
        super.onEnable();
    }
    
    @Override
    public void onDisable()
    {
        super.onDisable();
        for (NPC npc : gameNPCs)
        {
            try
            {
                npc.despawn();
            }
            catch (Exception ignored)
            {
            }
            
            try
            {
                npc.destroy();
            }
            catch (Exception ignored)
            {
            }
        }
        
        for (String worldString : WorldManager.gameWorlds)
        {
            World world = Bukkit.getWorld(worldString);
            WorldManager.deleteWorld(world);
        }
    }
    
}