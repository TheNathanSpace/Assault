package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.PlayerMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerAttackVictimHandler implements Listener
{
    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent entityAttackEvent)
    {
        if (entityAttackEvent.getDamager() instanceof Player)
        {
            Player attacker = (Player) entityAttackEvent.getDamager();
            PlayerMode attackerMode = PlayerMode.getPlayerMode(attacker);
            
            // Cancel if attacker can't damage
            if (!(attackerMode.canDamage))
            {
                entityAttackEvent.setCancelled(true);
                return;
            }
            
            if (entityAttackEvent.getEntity() instanceof Player)
            {
                Player victim = (Player) entityAttackEvent.getEntity();
                PlayerMode victimMode = PlayerMode.getPlayerMode(victim);
                
                // Cancel if victim can't take damage
                if (!(victimMode.canBeDamaged))
                {
                    entityAttackEvent.setCancelled(true);
                }
            }
        }
        
    }
}
