package com.thekingelessar.assault.game.eventhandlers.playermovement;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.map.MapBase;
import com.thekingelessar.assault.game.player.DeathType;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class PlayerMoveHandler implements Listener
{
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent)
    {
        Player player = playerMoveEvent.getPlayer();
        Location locTo = playerMoveEvent.getTo();
        
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        if (gameInstance != null && !gameInstance.gameStage.equals(GameStage.PREGAME) && !gameInstance.gameStage.equals(GameStage.FINISHED))
        {
            GameTeam oppositeTeam = gameInstance.getOppositeTeam(player);
            if (oppositeTeam != null)
            {
                MapBase enemyMapBase = oppositeTeam.mapBase;
                
                if (enemyMapBase.isInDefenderBoundingBox(locTo))
                {
                    cancelMovement(playerMoveEvent);
                    return;
                }
            }
            
            if (gameInstance.gameStage.equals(GameStage.BUILDING))
            {
                if (locTo.getZ() < gameInstance.gameMap.attackerBaseProtMaxZ)
                {
                    cancelMovement(playerMoveEvent);
                    return;
                }
            }
            else if (gameInstance.getDefendingTeam().equals(gameInstance.getPlayerTeam(player)))
            {
                if (locTo.getZ() < gameInstance.gameMap.attackerBaseProtMaxZ)
                {
                    cancelMovement(playerMoveEvent);
                    return;
                }
            }
            
            GameTeam gameTeam = gameInstance.getPlayerTeam(player);
            if (gameTeam != null)
            {
                GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
                
                if (((Entity) player).isOnGround())
                {
                    gamePlayer.startTimeInAir = 0;
                }
                else if (gamePlayer.startTimeInAir == 0)
                {
                    if (locTo.getY() > playerMoveEvent.getFrom().getY())
                    {
                        gamePlayer.startTimeInAir = System.nanoTime();
                    }
                }
                else if ((System.nanoTime() - gamePlayer.startTimeInAir) / 1000000000. > 5)
                {
                    if (PlayerMode.getPlayerMode(player).equals(PlayerMode.SPECTATOR))
                    {
                        gamePlayer.startTimeInAir = 0;
                    }
                    else
                    {
                        player.sendMessage(Assault.ASSAULT_PREFIX + "Looks like you're stuck—respawning!");
                        gamePlayer.respawn(null, true, DeathType.DEATH);
                    }
                }
                
                if (((Entity) player).isOnGround() && !gamePlayer.flightReset)
                {
                    gamePlayer.flightReset = true;
                }
            }
            
        }
        
        Location newLocation = playerMoveEvent.getTo();
        double voidY = newLocation.getY();
        
        if (voidY < 70)
        {
            if (gameInstance == null)
            {
                return;
            }
            
            GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
            gamePlayer.respawn(null, true, DeathType.VOID);
        }
    }
    
    private void cancelMovement(PlayerMoveEvent playerMoveEvent)
    {
        Player player = playerMoveEvent.getPlayer();
        
        Vector fromVec = playerMoveEvent.getFrom().toVector();
        Vector toVec = playerMoveEvent.getTo().toVector();
        Vector newVec = fromVec.subtract(toVec).normalize();
        newVec.setY(0.5);
        
        player.setVelocity(newVec);
        
        playerMoveEvent.setCancelled(true);
    }
    
}
