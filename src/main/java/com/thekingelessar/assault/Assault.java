package com.thekingelessar.assault;

import com.thekingelessar.assault.commands.*;
import com.thekingelessar.assault.config.MapConfig;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.eventhandlers.RegisterHandlers;
import com.thekingelessar.assault.game.map.BuffShopTrait;
import com.thekingelessar.assault.game.map.ItemShopTrait;
import com.thekingelessar.assault.game.map.Map;
import com.thekingelessar.assault.game.world.WorldManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
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
    
    static public boolean useHolographicDisplays;
    
    static public FileConfiguration mainConfig = null;
    static public World lobbyWorld;
    static public Location lobbySpawn;
    
    static public String assaultPrefix = "§5§l[§d§lAssault§5§l] " + ChatColor.RESET;
    
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
        lobbyWorld = Bukkit.getWorld(mainConfig.getString("lobby_world"));
        lobbySpawn = new Location(lobbyWorld, 0.5, 101.5, 0.5, 90, 0);
        
        List<String> mapList = (List<String>) mainConfig.getList("map_list");
        for (String mapName : mapList)
        {
            Map mapObject = MapConfig.loadWorldFromConfig(mapName);
            maps.put(mapName, mapObject);
        }
        
        this.getCommand("help").setExecutor(new CommandHelp());
        this.getCommand("all").setExecutor(new CommandAll());
        this.getCommand("respawn").setExecutor(new CommandRespawn());
        this.getCommand("forfeit").setExecutor(new CommandForfeit());
        this.getCommand("assaultadmin").setExecutor(new CommandAssault());
        
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