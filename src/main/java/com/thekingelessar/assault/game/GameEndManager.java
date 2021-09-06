package com.thekingelessar.assault.game;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.timertasks.TaskCountdownGameEnd;
import com.thekingelessar.assault.game.world.WorldManager;
import com.thekingelessar.assault.util.FireworkUtils;
import com.thekingelessar.assault.util.Title;
import com.thekingelessar.assault.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GameEndManager
{
    GameInstance gameInstance;
    
    public GameEndManager(GameInstance gameInstance)
    {
        this.gameInstance = gameInstance;
    }
    
    // alertLastEnemyLeft
    
    // separate end of round and declare winners
    
    public enum WinState
    {
        LOWEST_TIME,
        ATTACKERS_LEFT,
        DEFENDERS_LEFT,
        BUILDING_LEFT,
        TIE;
    }
    
    
    public void declareWinners(WinState winState)
    {
        gameInstance.gameStage = GameStage.FINISHED;
        
        String winnerTitleString = gameInstance.winningTeam.color.chatColor + ChatColor.BOLD.toString() + "YOU" + ChatColor.WHITE + " WIN!";
        String loserTitleString = gameInstance.winningTeam.color.getFormattedName(true, true, ChatColor.BOLD) + ChatColor.WHITE + " team wins!";
        
        String subtitleString;
        
        switch (winState)
        {
            case LOWEST_TIME:
                subtitleString = "Time: " + ChatColor.LIGHT_PURPLE + Util.secondsToMinutes(gameInstance.winningTeam.displaySeconds, true) + ChatColor.WHITE + " seconds";
                break;
            
            case ATTACKERS_LEFT:
                subtitleString = "All of the " + gameInstance.getAttackingTeam().color.chatColor + "attackers" + ChatColor.RESET + " left!";
                break;
            
            case DEFENDERS_LEFT:
                GameTeam disconnectedTeam = gameInstance.getRemainingTeam().getOppositeTeam();
                subtitleString = "The " + disconnectedTeam.color.chatColor + "enemy team" + ChatColor.RESET + " disconnected!";
                break;
            
            case BUILDING_LEFT:
                disconnectedTeam = gameInstance.getRemainingTeam().getOppositeTeam();
                subtitleString = "The " + disconnectedTeam.color.chatColor + "enemy team" + ChatColor.RESET + " disconnected!";
                break;
            
            case TIE:
                List<GameTeam> teamList = new ArrayList<>(gameInstance.teams.values());
                winnerTitleString = "IT'S A TIE!";
                loserTitleString = "IT'S A TIE!";
                subtitleString = teamList.get(0).color.getFormattedName(true, true, ChatColor.BOLD) + ChatColor.WHITE + " and " + teamList.get(1).color.getFormattedName(true, true, ChatColor.BOLD) + ChatColor.WHITE + " tied!";
                break;
            
            default:
                throw new IllegalStateException("Unexpected value: " + winState);
        }
        
        Title winnerTitle = new Title(winnerTitleString, subtitleString, 0, 6, 1);
        Title loserTitle = new Title(loserTitleString, subtitleString, 0, 6, 1);
        
        for (Player player : gameInstance.winningTeam.getPlayers())
        {
            for (int i = 0; i < 5; i++)
            {
                FireworkUtils.spawnRandomFirework(player.getLocation(), gameInstance.winningTeam.color);
            }
            
            winnerTitle.clearTitle(player);
            winnerTitle.send(player);
            
            PlayerMode playerMode = PlayerMode.setPlayerMode(player, PlayerMode.HAM, gameInstance);
        }
        
        for (Player player : gameInstance.winningTeam.getOppositeTeam().getPlayers())
        {
            if (gameInstance.isTie())
            {
                for (int i = 0; i < 5; i++)
                {
                    FireworkUtils.spawnRandomFirework(player.getLocation(), gameInstance.winningTeam.getOppositeTeam().color);
                }
            }
            
            loserTitle.clearTitle(player);
            loserTitle.send(player);
            
            PlayerMode playerMode = PlayerMode.setPlayerMode(player, PlayerMode.HAM, gameInstance);
        }
        
        gameInstance.taskCountdownGameEnd = new TaskCountdownGameEnd(240, 20, 20, gameInstance);
        gameInstance.taskCountdownGameEnd.runTaskTimer(Assault.INSTANCE, gameInstance.taskCountdownGameEnd.startDelay, gameInstance.taskCountdownGameEnd.tickDelay);
        
        gameInstance.updateScoreboards();
        
    }
    
    public void cleanupGameInstance()
    {
        for (GameTeam gameTeam : gameInstance.teams.values())
        {
            gameTeam.mapBase.destroyShops();
        }
        
        for (BukkitRunnable taskTimer : gameInstance.allTimers)
        {
            if (taskTimer != null)
            {
                taskTimer.cancel();
            }
        }
        
        for (GameTeam gameTeam : gameInstance.teams.values())
        {
            gameTeam.buffList.clear();
            
            for (Player player : gameTeam.getPlayers())
            {
                GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
                gamePlayer.scoreboard.delete();
                gameTeam.removeMember(player);
            }
            
            gameTeam.teamScoreboard.unregister();
        }
        
        WorldManager.closeWorld(gameInstance.gameWorld);
        Assault.gameInstances.remove(gameInstance);
    }
}
