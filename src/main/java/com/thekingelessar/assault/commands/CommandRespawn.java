package com.thekingelessar.assault.commands;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.DeathType;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRespawn implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            if (!((Player) sender).hasPermission("assault.command.respawn"))
            {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use that command");
                return true;
            }
    
            Player player = (Player) sender;
            
            GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
            
            if (gameInstance != null)
            {
                GameTeam gameTeam = gameInstance.getPlayerTeam(player);
                if (gameTeam != null)
                {
                    GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
                    gamePlayer.respawn(null, true, DeathType.DEATH);
                    return true;
                }
            }
        }
        
        return false;
    }
}