package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.map.MapBase;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.timertasks.TaskCountdownRespawn;
import com.thekingelessar.assault.util.Util;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveHandler implements Listener
{
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent)
    {
        Player player = playerMoveEvent.getPlayer();
        Location locTo = playerMoveEvent.getTo();
        
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        if (gameInstance != null)
        {
            GameTeam oppositeTeam = gameInstance.getOppositeTeam(player);
            if (oppositeTeam != null)
            {
                MapBase enemyMapBase = oppositeTeam.mapBase;
                
                if (Util.isInside(locTo, enemyMapBase.defenderBoundingBox.get(0).toLocation(gameInstance.gameWorld), enemyMapBase.defenderBoundingBox.get(1).toLocation(gameInstance.gameWorld)))
                {
                    playerMoveEvent.setCancelled(true);
                    return;
                }
            }
        }
        
        Location newLocation = playerMoveEvent.getTo();
        double voidY = newLocation.getY();
        
        if (voidY < 70)
        {
            if (gameInstance != null)
            {
                if (gameInstance.lastDamagedBy.get(player) != null)
                {
                    Player attacker = gameInstance.lastDamagedBy.get(player);
                    gameInstance.getPlayerTeam(attacker).gamerPoints += 1;
                    
                    GamePlayer attackerPlayer = gameInstance.getPlayerTeam(attacker).getGamePlayer(attacker);
                    GamePlayer victimPlayer = gameInstance.getPlayerTeam(player).getGamePlayer(player);
                    
                    attackerPlayer.playerBank.coins += (int) (0.2 * (victimPlayer.playerBank.coins));
                    attacker.playSound(attacker.getLocation(), Sound.ORB_PICKUP, 0.8F, 1.0F);
                    attackerPlayer.updateScoreboard();
                    
                    gameInstance.lastDamagedBy.put(player, null);
                }
                
                PlayerMode.setPlayerMode(player, PlayerMode.SPECTATOR, gameInstance);
                
                player.teleport(gameInstance.gameMap.waitingSpawn.toLocation(gameInstance.gameWorld));
                
                GameTeam gameTeam = gameInstance.getPlayerTeam(player);
                GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
                
                gamePlayer.taskCountdownRespawn = new TaskCountdownRespawn(60, 0, 20, gameInstance, player);
                gamePlayer.taskCountdownRespawn.runTaskTimer(Assault.INSTANCE, gamePlayer.taskCountdownRespawn.startDelay, gamePlayer.taskCountdownRespawn.tickDelay);
            }
        }
    }
}
