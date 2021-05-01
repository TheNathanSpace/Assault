package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.game.timertasks.TaskCountdownRespawn;
import com.thekingelessar.assault.util.Util;
import org.bukkit.Location;
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
            GameTeam gameTeam = gameInstance.getPlayerTeam(player);
            if (gameTeam != null)
            {
                if (Util.isOnCarpet(locTo))
                {
                    if (!Util.getCarpetColor(locTo).contains(gameTeam.color))
                    {
                        playerMoveEvent.setCancelled(true);
                        return;
                    }
                }
            }
        }
        
        Location newLocation = playerMoveEvent.getTo();
        double voidY = newLocation.getY();
        
        if (voidY < 70)
        {
            if (gameInstance != null)
            {
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
