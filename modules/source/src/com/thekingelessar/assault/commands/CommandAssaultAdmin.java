package com.thekingelessar.assault.commands;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.database.AssaultTableManager;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.logging.Level;

public class CommandAssaultAdmin implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args == null || args.length == 0) return false;
        
        if (args[0].equalsIgnoreCase("export"))
        {
            File exportFile;
            if (args.length >= 2)
            {
                StringBuilder filename = new StringBuilder();
                
                for (String s : args)
                {
                    String arg = s + " ";
                    filename.append(arg);
                }
                
                exportFile = AssaultTableManager.getInstance().exportStatistics(new File("plugins/Assault/exported/" + filename.toString()));
            }
            else
            {
                exportFile = AssaultTableManager.getInstance().exportStatistics(null);
            }
            
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                if (exportFile == null)
                {
                    player.sendMessage(Assault.ASSAULT_PREFIX + "Error exporting statistics—see server log for more details.");
                }
                else
                {
                    player.sendMessage(Assault.ASSAULT_PREFIX + "Statistics exported to " + exportFile + "!");
                }
            }
            
            if (exportFile == null)
            {
                Assault.INSTANCE.getLogger().log(Level.INFO, "Could not export statistics to " + exportFile + ".");
            }
            else
            {
                Assault.INSTANCE.getLogger().log(Level.INFO, "Statistics exported to " + exportFile + ".");
            }
            
            return true;
        }
        
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