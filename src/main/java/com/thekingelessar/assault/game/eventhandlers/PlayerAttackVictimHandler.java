package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
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
            
            if (attackerMode != null)
            {
                // Cancel if attacker can't damage
                if (!(attackerMode.canDamage))
                {
                    entityAttackEvent.setCancelled(true);
                    return;
                }
            }
            
            if (entityAttackEvent.getEntity() instanceof Player)
            {
                Player victim = (Player) entityAttackEvent.getEntity();
                PlayerMode victimMode = PlayerMode.getPlayerMode(victim);
                
                if (victimMode != null)
                {
                    // Cancel if victim can't take damage
                    if (!(victimMode.canBeDamaged))
                    {
                        entityAttackEvent.setCancelled(true);
                    }
                }
                
                if (victim.getHealth() - entityAttackEvent.getDamage() < 1)
                {
                    GameInstance attackerInstance = GameInstance.getPlayerGameInstance(attacker);
                    if (attackerInstance != null)
                    {
                        GameTeam gameTeam = attackerInstance.getPlayerTeam(attacker);
                        
                        if (gameTeam != null)
                        {
                            GamePlayer player = gameTeam.getGamePlayer(attacker);
                            
                            player.playerBank.gamerPoints += 1;
                            
                            GamePlayer victimPlayer = attackerInstance.getPlayerTeam(victim).getGamePlayer(victim);
                            player.playerBank.coins += (int) (0.2 * (victimPlayer.playerBank.coins));
                            
                            player.updateScoreboard();
                        }
                    }
                }
            }
        }
    }
}
