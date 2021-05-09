package com.thekingelessar.assault.commands;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.team.GameTeam;
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
            Player player = (Player) sender;
            GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
            
            if (gameInstance != null)
            {
                GameTeam gameTeam = gameInstance.getPlayerTeam(player);
                
                if (gameTeam.canForfeit())
                {
                    gameTeam.toggleForfeit(player);
                }
                else
                {
                    player.sendRawMessage(Assault.assaultPrefix + "Your team cannot forfeit right now! You probably need to wait longer.");
                    return true;
                }
            }
        }
        
        return false;
    }
}