package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.timertasks.TaskCountdownGameEnd;
import com.thekingelessar.assault.game.timertasks.TaskCountdownSwapAttackers;
import com.thekingelessar.assault.util.FireworkUtils;
import com.thekingelessar.assault.util.Title;
import com.thekingelessar.assault.util.Util;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.List;

public class PlayerPickupItemHandler implements Listener
{
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent playerPickupItemEvent)
    {
        Material pickedUp = playerPickupItemEvent.getItem().getItemStack().getType();
        if (pickedUp.equals(Material.NETHER_STAR))
        {
            Player player = playerPickupItemEvent.getPlayer();
            GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
            
            if (gameInstance != null)
            {
                GameTeam gameTeam = gameInstance.getPlayerTeam(player);
                
                if (gameInstance.gameStage.equals(GameStage.BUILDING_BASE) || !gameInstance.getAttackingTeam().equals(gameTeam))
                {
                    playerPickupItemEvent.setCancelled(true);
                    return;
                }
                
                if (gameInstance.taskAttackTimer == null)
                {
                    playerPickupItemEvent.setCancelled(true);
                    return;
                }
                
                gameInstance.taskAttackTimer.stopTimer();
                
                List<Player> winners = gameInstance.getAttackingTeam().getPlayers();
                for (Player winPlayer : winners)
                {
                    for (int i = 0; i < 3; i++)
                    {
                        FireworkUtils.spawnRandomFirework(winPlayer.getLocation(), gameInstance.getAttackingTeam().color);
                    }
                }
                
                for (Player currentPlayer : gameInstance.getPlayers())
                {
                    GameTeam theirTeam = gameInstance.getPlayerTeam(currentPlayer);
                    GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
                    
                    if (gamePlayer.taskCountdownRespawn != null)
                    {
                        gamePlayer.taskCountdownRespawn.finishTimer();
                    }
                    
                    PlayerMode playerMode = PlayerMode.setPlayerMode(currentPlayer, PlayerMode.BETWEEN, gameInstance);
                }
                
                if (gameInstance.teamsGone == 1)
                {
                    long nanosecondsTaken = System.nanoTime() - gameInstance.getAttackingTeam().startAttackingTime;
                    gameInstance.getAttackingTeam().finalAttackingTime = nanosecondsTaken / 1000000000.;
                    gameInstance.getAttackingTeam().displaySeconds = Util.round(gameInstance.getAttackingTeam().finalAttackingTime, 2);
                    
                    gameInstance.updateScoreboards();
                    
                    String mainTitle = gameInstance.getWinningTeam().color.getFormattedName(true, true, ChatColor.BOLD) + ChatColor.WHITE + " team wins!";
                    Title title = new Title(mainTitle, ChatColor.WHITE + "Time: " + ChatColor.LIGHT_PURPLE + gameInstance.getWinningTeam().displaySeconds + ChatColor.WHITE + " seconds", 0, 6, 1);
                    
                    for (Player currentPlayer : gameInstance.gameWorld.getPlayers())
                    {
                        PlayerMode playerMode = PlayerMode.setPlayerMode(currentPlayer, PlayerMode.HAM, gameInstance);
                        title.send(currentPlayer);
                    }
                    
                    gameInstance.taskCountdownGameEnd = new TaskCountdownGameEnd(240, 20, 20, gameInstance);
                    gameInstance.taskCountdownGameEnd.runTaskTimer(Assault.INSTANCE, gameInstance.taskCountdownGameEnd.startDelay, gameInstance.taskCountdownGameEnd.tickDelay);
                }
                else
                {
                    gameInstance.updateScoreboards();
                    
                    gameInstance.taskCountdownSwapAttackers = new TaskCountdownSwapAttackers(200, 0, 20, gameInstance);
                    gameInstance.taskCountdownSwapAttackers.runTaskTimer(Assault.INSTANCE, gameInstance.taskCountdownSwapAttackers.startDelay, gameInstance.taskCountdownSwapAttackers.tickDelay);
                }
            }
        }
    }
}