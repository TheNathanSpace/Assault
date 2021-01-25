package com.thekingelessar.assault.commands;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAssault implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args == null || args.length == 0) return false;
        switch (args[0])
        {
            case "start":
                if (sender instanceof Player)
                {
                    GameInstance testInstance = new GameInstance(args[1]);
                    testInstance.startWorld();
                }
                break;
    
            case "splitteams":
                if (sender instanceof Player)
                {
                    GameInstance testInstance = new GameInstance(args[1]);
                    testInstance.createTeams();
                }
                break;
            
            case "close":
                WorldManager.closeWorld(Bukkit.getWorld(args[1]));
                break;
            
            case "create":
                WorldManager.createWorldFromMap(args[1], Boolean.parseBoolean(args[2]), args[3]);
                break;
            
            case "join":
                if (sender instanceof Player)
                {
                    ((Player) sender).teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
                }
                break;
        }
        return true;
    }
}