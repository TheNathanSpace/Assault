package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.DeathType;
import com.thekingelessar.assault.game.player.GamePlayer;
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
            
            GameInstance playerGameInstance = GameInstance.getPlayerGameInstance(damagedPlayer);
            if (damagedPlayer.getHealth() - entityDamageEvent.getDamage() < 1)
            {
                if (playerGameInstance == null)
                {
                    return;
                }
                
                GamePlayer gamePlayer = playerGameInstance.getGamePlayer(damagedPlayer);
                
                playerGameInstance.lastDamagedBy.put(damagedPlayer, null);
                
                switch (entityDamageEvent.getCause())
                {
                    case ENTITY_ATTACK:
                        gamePlayer.respawn(null, true, DeathType.SWORD);
                        break;
                    case CONTACT:
                        gamePlayer.respawn(null, true, DeathType.CONTACT);
                        break;
                    case PROJECTILE:
                        gamePlayer.respawn(null, true, DeathType.BOW);
                        break;
                    case FALL:
                        gamePlayer.respawn(null, true, DeathType.FALL);
                        break;
                    case DROWNING:
                        gamePlayer.respawn(null, true, DeathType.DROWNING);
                        break;
                    case ENTITY_EXPLOSION:
                        gamePlayer.respawn(null, true, DeathType.EXPLOSION);
                        break;
                }
                
                entityDamageEvent.setCancelled(true);
            }
        }
    }
}
