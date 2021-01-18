package com.thekingelessar.assault.game.world;

import com.thekingelessar.assault.Assault;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.thekingelessar.assault.Assault.lobbyWorld;

public class WorldManager
{
    
    public static List<String> gameWorlds = new ArrayList<>();
    
    public static void closeWorld(World world)
    {
        // Teleport players out
        if (Bukkit.getWorld(lobbyWorld) == null)
        {
            createWorldFromMap(lobbyWorld, true, "lobby");
        }
        
        for (Player player : world.getPlayers())
        {
            player.teleport(new Location(Bukkit.getWorld(lobbyWorld), 0, 70, 0));
        }
        
        // Close server world
        if (Bukkit.getServer().unloadWorld(world, false))
        {
            Assault.INSTANCE.getLogger().info("Closed world " + world.getName());
        }
        else
        {
            Assault.INSTANCE.getLogger().severe("Could not close world " + world.getName());
        }
        
        // Delete files
        deleteWorld(world);
    }
    
    public static World createWorldFromMap(String mapName, boolean autosave, String worldID)
    {
        if (worldID == null)
        {
            worldID = mapName + "_" + UUID.randomUUID();
        }
        
        copyWorld(mapName, worldID);
        
        World newWorld = loadWorld(worldID);
        newWorld.setAutoSave(autosave);
        
        gameWorlds.add(worldID);
        
        return newWorld;
    }
    
    public static World loadWorld(String worldName)
    {
        return WorldCreator.name(worldName).createWorld();
    }
    
    public static boolean deleteWorld(World world)
    {
        File worldFolder = world.getWorldFolder();
        
        if (worldFolder.exists())
        {
            File[] files = worldFolder.listFiles();
            for (File file : files)
            {
                if (file.isDirectory())
                {
                    deleteDirectory(file);
                }
                else
                {
                    file.delete();
                }
            }
        }
        
        gameWorlds.remove(world.getName());
        
        return (worldFolder.delete());
    }
    
    static void deleteDirectory(File file)
    {
        for (File subFile : file.listFiles())
        {
            if (subFile.isDirectory())
            {
                deleteDirectory(subFile);
            }
            else
            {
                subFile.delete();
            }
        }
        file.delete();
    }
    
    public static void copyWorld(String sourceName, String targetName)
    {
        File sourceFolder = new File(Bukkit.getWorldContainer(), sourceName);
        File targetFolder = new File(Bukkit.getWorldContainer(), targetName);
        
        copyWorld(sourceFolder, targetFolder);
    }
    
    public static void copyWorld(File source, File target)
    {
        try
        {
            ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
            if (!ignore.contains(source.getName()))
            {
                if (source.isDirectory())
                {
                    if (!target.exists())
                    {
                        target.mkdirs();
                    }
                    String files[] = source.list();
                    for (String file : files)
                    {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyWorld(srcFile, destFile);
                    }
                }
                else
                {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                    {
                        out.write(buffer, 0, length);
                    }
                    in.close();
                    out.close();
                }
            }
        }
        catch (IOException e)
        {
            Assault.INSTANCE.getLogger().severe("Error copying map.");
        }
    }
}
