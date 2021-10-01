package com.thekingelessar.assault.game.eventhandlers.combat;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.DeathType;
import com.thekingelessar.assault.game.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageHandler implements Listener
{
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent entityDamageEvent)
    {
        if (entityDamageEvent.getEntity() instanceof Item)
        {
            Item item = (Item) entityDamageEvent.getEntity();
            if (item.getItemStack().getType().equals(Material.NETHER_STAR))
            {
                entityDamageEvent.getEntity().setFireTicks(0);
                entityDamageEvent.setCancelled(true);
            }
        }
        
        if (entityDamageEvent.getEntity() instanceof Player)
        {
            Player damagedPlayer = (Player) entityDamageEvent.getEntity();
    
            if (damagedPlayer.getWorld().equals(Assault.lobbyWorld))
            {
                entityDamageEvent.setCancelled(true);
            }
    
            GameInstance playerGameInstance = GameInstance.getPlayerGameInstance(damagedPlayer);
            if (damagedPlayer.getHealth() - entityDamageEvent.getFinalDamage() <= 0)
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
