package com.thekingelessar.assault.commands;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAll implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args == null || args.length == 0) return false;
        
        if (sender instanceof Player)
        {
            if (!((Player) sender).hasPermission("assault.command.help"))
            {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
                return true;
            }
    
            Player player = (Player) sender;
            String message = String.join(" ", args);
            
            GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
            if (gameInstance != null)
            {
                GameTeam gameTeam = gameInstance.getPlayerTeam(player);
                if (gameTeam != null)
                {
                    String format = ChatColor.GRAY + "[ALL] " + gameTeam.color.chatColor + "[" + gameTeam.color.getFormattedName(false, false, null) + gameTeam.color.chatColor + "]§r %1$s§r: %2$s";
                    message = String.format(format, player.getName(), message);
                    
                    for (Player gamePlayer : gameInstance.getPlayers())
                    {
                        gamePlayer.sendMessage(message);
                    }
                    
                    return true;
                }
            }
        }
        
        return false;
    }
}