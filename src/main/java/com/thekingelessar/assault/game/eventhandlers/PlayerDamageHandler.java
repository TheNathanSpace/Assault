package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.PlayerMode;
import com.thekingelessar.assault.game.timertasks.TaskCountdownRespawn;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageHandler implements Listener
{
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent entityDamageEvent)
    {
        if (entityDamageEvent.getEntity() instanceof Player)
        {
            Player damagedPlayer = (Player) entityDamageEvent.getEntity();
            
            if (damagedPlayer.getHealth() - entityDamageEvent.getDamage() < 1)
            {
                GameInstance playerGameInstance = GameInstance.getPlayerGameInstance(damagedPlayer);
                
                // If the player who died isn't in a game
                if (playerGameInstance == null)
                {
                    return;
                }
                
                damagedPlayer.setGameMode(GameMode.SPECTATOR);
                PlayerMode.setPlayerMode(damagedPlayer, PlayerMode.SPECTATOR, playerGameInstance);
                
                damagedPlayer.teleport(playerGameInstance.gameMap.waitingSpawn.toLocation(playerGameInstance.gameWorld));
                
                TaskCountdownRespawn respawnTimer = new TaskCountdownRespawn(60, 0, 20, playerGameInstance, damagedPlayer);
                respawnTimer.runTaskTimer(Assault.INSTANCE, respawnTimer.startDelay, respawnTimer.tickDelay);
                
                entityDamageEvent.setCancelled(true);
            }
        }
    }
}
