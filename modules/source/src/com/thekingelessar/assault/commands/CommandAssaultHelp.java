package com.thekingelessar.assault.commands;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.GRAY;

public class CommandAssaultHelp implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            if (!((Player) sender).hasPermission("assault.command.help"))
            {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
                return true;
            }
            
            Player player = (Player) sender;
            
            List<String> helpResponse = new ArrayList<>();
            helpResponse.add("-------------");
            helpResponse.add(Assault.ASSAULT_PREFIX + "Help");
            helpResponse.add(GRAY + "/assaulthelp" + Util.RESET_CHAT + ": Explanation of commands.");
            helpResponse.add(GRAY + "/all <message>" + Util.RESET_CHAT + ": Shows a chat message to all players in the game.");
            helpResponse.add(GRAY + "/respawn" + Util.RESET_CHAT + ": Kills you (in case you get stuck somewhere).");
            helpResponse.add(GRAY + "/forfeit" + Util.RESET_CHAT + ": Toggles your vote to forfeit.");
            helpResponse.add("-------------");
            
            player.sendMessage(String.join("\n", helpResponse));
            
            return true;
        }
        
        return false;
    }
}