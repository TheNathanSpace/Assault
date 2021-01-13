package com.thekingelessar.assault;

import com.thekingelessar.assault.game.GameInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

import static com.thekingelessar.assault.Assault.lobbyWorld;

public class WorldManager
{
    
    public static HashMap<String, GameInstance> worldGames;
    
    public static void rollback(String worldName)
    {
        if (Bukkit.getWorld(lobbyWorld) == null)
        {
            loadMap(lobbyWorld, true);
        }
        
        for (Player player : Bukkit.getServer().getWorld(worldName).getPlayers())
        {
            player.teleport(new Location(Bukkit.getWorld(lobbyWorld), 0, 70, 0));
        }
        
        unloadMap(worldName);
        loadMap(worldName, false);
    }
    
    public static void unloadMap(String worldName)
    {
        if (Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(worldName), false))
        {
            Assault.INSTANCE.getLogger().info("Successfully unloaded " + worldName);
        }
        else
        {
            Assault.INSTANCE.getLogger().severe("COULD NOT UNLOAD " + worldName);
        }
    }
    
    public static void loadMap(String worldName, boolean autosave)
    {
        World world = Bukkit.getServer().createWorld(new WorldCreator(worldName));
        world.setAutoSave(autosave);
    }
}
