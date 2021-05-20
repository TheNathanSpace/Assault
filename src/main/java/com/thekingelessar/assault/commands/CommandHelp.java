package com.thekingelessar.assault.commands;

import com.thekingelessar.assault.Assault;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RESET;

public class CommandHelp implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            
            List<String> helpResponse = new ArrayList<>();
            helpResponse.add("-------------");
            helpResponse.add(Assault.assaultPrefix + "Help");
            helpResponse.add(GRAY + "/help" + RESET + ": Explanation of commands.");
            helpResponse.add(GRAY + "/all <message>" + RESET + ": Shows a chat message to all players in the game.");
            helpResponse.add(GRAY + "/respawn" + RESET + ": Kills you (in case you get stuck somewhere).");
            helpResponse.add(GRAY + "/forfeit" + RESET + ": Toggles your vote to forfeit.");
            helpResponse.add("-------------");
            
            player.sendMessage(String.join("\n", helpResponse));
            
            return true;
        }
        
        return false;
    }
}