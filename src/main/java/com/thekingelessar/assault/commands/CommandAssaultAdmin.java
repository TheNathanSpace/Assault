package com.thekingelessar.assault.commands;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAssaultAdmin implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args == null || args.length == 0) return false;
        
        if (sender instanceof Player)
        {
            if (!((Player) sender).hasPermission("assault.command.assaultadmin"))
            {
                return true;
            }
            
            Player player = (Player) sender;
            GameInstance testInstance = GameInstance.getPlayerGameInstance(player);
            if (testInstance != null)
            {
                if (!testInstance.gameStage.equals(GameStage.BUILDING))
                {
                    player.sendRawMessage(ChatColor.RED + "Wait until the game starts!");
                    return true;
                }
                
                GameTeam gameTeam = testInstance.getPlayerTeam(player);
                GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
                switch (args[0])
                {
                    case "attack":
                        testInstance.taskCountdownBuilding.ticksLeft = 60;
                        break;
                    case "gp":
                        gameTeam.gamerPoints += Integer.parseInt(args[1]);
                        break;
                    case "coins":
                        gamePlayer.playerBank.coins += Integer.parseInt(args[1]);
                        break;
                    
                }
                return true;
            }
            return false;
        }
        return false;
    }
}