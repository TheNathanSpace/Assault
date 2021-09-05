package com.thekingelessar.assault.commands;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandForfeit implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            if (!((Player) sender).hasPermission("assault.command.forfeit"))
            {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
                return true;
            }
            
            Player player = (Player) sender;
            GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
            
            if (gameInstance != null)
            {
                GameTeam gameTeam = gameInstance.getPlayerTeam(player);
                
                if (gameTeam.canForfeit() || (args != null && args.length != 0 && args[0].equalsIgnoreCase("force") && player.hasPermission("assault.command.forceforfeit")))
                {
                    gameTeam.toggleForfeit(player);
                }
                else
                {
                    player.sendRawMessage(ChatColor.RED + "The defending team cannot forfeit!");
                    return true;
                }
            }
        }
        
        return false;
    }
}