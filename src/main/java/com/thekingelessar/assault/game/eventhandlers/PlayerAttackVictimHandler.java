package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.map.MapBase;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.util.Util;
import org.bukkit.Location;
import org.bukkit.Sound;
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
                
                if (victim.getWorld().equals(Assault.lobbyWorld))
                {
                    entityAttackEvent.setCancelled(true);
                }
                
                GameInstance gameInstance = GameInstance.getPlayerGameInstance(attacker);
                if (gameInstance != null)
                {
                    Location victimLocation = victim.getLocation();
                    for (GameTeam gameTeam : gameInstance.teams.values())
                    {
                        MapBase mapBase = gameTeam.mapBase;
                        
                        if (Util.isInside(victimLocation, mapBase.defenderBoundingBox.get(0).toLocation(gameInstance.gameWorld), mapBase.defenderBoundingBox.get(1).toLocation(gameInstance.gameWorld)))
                        {
                            entityAttackEvent.setCancelled(true);
                            return;
                        }
                    }
                    
                    gameInstance.lastDamagedBy.put(victim, attacker);
                }
                
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
                    if (gameInstance != null)
                    {
                        
                        gameInstance.gameWorld.playSound(victim.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
                        
                        GameTeam gameTeam = gameInstance.getPlayerTeam(attacker);
                        
                        if (gameTeam != null)
                        {
                            GamePlayer gamePlayer = gameTeam.getGamePlayer(attacker);
                            
                            if (gameTeam.teamStage.equals(TeamStage.ATTACKING))
                            {
                                gameTeam.gamerPoints += 1;
                            }
                            
                            GamePlayer victimPlayer = gameInstance.getPlayerTeam(victim).getGamePlayer(victim);
                            gamePlayer.playerBank.coins += (int) (0.2 * (victimPlayer.playerBank.coins));
                            
                            attacker.playSound(attacker.getLocation(), Sound.ORB_PICKUP, 0.8F, 1.0F);
                            
                            gamePlayer.updateScoreboard();
                        }
                    }
                }
            }
        }
    }
}
