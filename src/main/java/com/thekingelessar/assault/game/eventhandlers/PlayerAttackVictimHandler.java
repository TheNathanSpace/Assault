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
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerAttackVictimHandler implements Listener
{
    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent entityAttackEvent)
    {
        if (entityAttackEvent.getEntity() instanceof Player)
        {
            if (entityAttackEvent.getDamager() instanceof Arrow)
            {
                Arrow arrow = (Arrow) entityAttackEvent.getDamager();
                if (arrow.getShooter() instanceof Player)
                {
                    Player damagedPlayer = ((Player) entityAttackEvent.getEntity()).getPlayer();
                    Player shooter = (Player) arrow.getShooter();
                    
                    GameInstance gameInstance = GameInstance.getPlayerGameInstance(damagedPlayer);
                    
                    if (gameInstance != null)
                    {
                        GameTeam damagedTeam = gameInstance.getPlayerTeam(damagedPlayer);
                        GameTeam shooterTeam = gameInstance.getPlayerTeam(shooter);
                        
                        if (damagedTeam != null && shooterTeam != null)
                        {
                            if (damagedTeam.equals(shooterTeam))
                            {
                                entityAttackEvent.setCancelled(true);
                                return;
                            }
                        }
                    }
                }
            }
        }
        
        if (!(entityAttackEvent.getDamager() instanceof Player || entityAttackEvent.getDamager() instanceof Arrow))
        {
            return;
        }
        
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
        }
        
        if (entityAttackEvent.getEntity() instanceof Player)
        {
            Player victim = (Player) entityAttackEvent.getEntity();
            
            if (victim.getWorld().equals(Assault.lobbyWorld))
            {
                entityAttackEvent.setCancelled(true);
            }
            
            GameInstance gameInstance = GameInstance.getPlayerGameInstance(victim);
            if (gameInstance != null)
            {
                PlayerMode victimMode = PlayerMode.getPlayerMode(victim);
                
                if (victimMode != null)
                {
                    // Cancel if victim can't take damage
                    if (!(victimMode.canBeDamaged))
                    {
                        entityAttackEvent.setCancelled(true);
                        return;
                    }
                }
                
                GameTeam victimTeam = gameInstance.getPlayerTeam(victim);
                
                if (entityAttackEvent.getDamager() instanceof Player || entityAttackEvent.getDamager() instanceof Arrow)
                {
                    Player attacker = null;
                    Arrow arrow = null;
                    if (entityAttackEvent.getDamager() instanceof Arrow)
                    {
                        arrow = (Arrow) entityAttackEvent.getDamager();
                        if (arrow.getShooter() instanceof Player)
                        {
                            attacker = (Player) arrow.getShooter();
                        }
                        else
                        {
                            return;
                        }
                    }
                    else
                    {
                        attacker = (Player) entityAttackEvent.getDamager();
                    }
                    
                    GameTeam attackerTeam = gameInstance.getPlayerTeam(attacker);
                    
                    if (victimTeam.equals(attackerTeam))
                    {
                        entityAttackEvent.setCancelled(true);
                        return;
                    }
                    
                    Location victimLocation = victim.getLocation();
                    
                    if (attackerTeam.teamStage.equals(TeamStage.DEFENDING) && victim.getLocation().getZ() < gameInstance.gameMap.attackerBaseProtMinZ)
                    {
                        entityAttackEvent.setCancelled(true);
                        return;
                    }
                    
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
                    
                    if (victim.getHealth() - entityAttackEvent.getDamage() < 1)
                    {
                        gameInstance.gameWorld.playSound(victim.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
                        
                        GamePlayer attackerPlayer = attackerTeam.getGamePlayer(attacker);
                        
                        GamePlayer victimPlayer = victimTeam.getGamePlayer(victim);
                        
                        if (arrow != null)
                        {
                            victimPlayer.addBowDeathFeed(attacker);
                            attackerPlayer.killPlayer(victim, true);
                        }
                        else
                        {
                            attackerPlayer.killPlayer(victim, false);
                        }
                    }
                }
            }
        }
    }
}
