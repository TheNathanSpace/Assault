package com.thekingelessar.assault.commands;

import com.thekingelessar.assault.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Assault implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args == null || args.length == 0) return false;
        switch (args[0])
        {
            case "rollback":
                WorldManager.rollback(args[1]);
                break;
            
            case "load":
                WorldManager.loadMap(args[1], Boolean.parseBoolean(args[2]));
                break;
            
            case "join":
                if (sender instanceof Player)
                {
                    if (Bukkit.getWorld(args[1]) == null)
                    {
                        WorldManager.loadMap(args[1], Boolean.parseBoolean(args[2]));
                    }
                    ((Player) sender).teleport(new Location(Bukkit.getWorld(args[1]), 0, 70, 0));
                }
                break;
        }
        return true;
    }
}