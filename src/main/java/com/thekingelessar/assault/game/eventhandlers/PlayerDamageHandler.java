package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.team.GameTeam;
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
                
                GameTeam playerTeam = playerGameInstance.getPlayerTeam(damagedPlayer.getUniqueId());
                damagedPlayer.teleport(playerGameInstance.gameMap.getSpawn(playerTeam, null).toLocation(playerGameInstance.gameWorld));
                
                entityDamageEvent.setCancelled(true);
            }
        }
    }
}
