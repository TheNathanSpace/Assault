package com.thekingelessar.assault.game.eventhandlers.combat;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.game.world.map.MapBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.logging.Level;

public class PlayerAttackVictimHandler implements Listener
{
    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent entityAttackEvent)
    {
        // Cancel friendly bow fire
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
        
        // If not attacked by player or arrow, return
        if (!(entityAttackEvent.getDamager() instanceof Player || entityAttackEvent.getDamager() instanceof Arrow))
        {
            return;
        }
        
        // Cancel if attacker can't damage
        if (entityAttackEvent.getDamager() instanceof Player)
        {
            Player attacker = (Player) entityAttackEvent.getDamager();
            PlayerMode attackerMode = PlayerMode.getPlayerMode(attacker);
            
            if (attackerMode != null)
            {
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
                    
                    if (attackerTeam.teamStage.equals(TeamStage.DEFENDING) && victim.getLocation().getZ() < gameInstance.gameMap.attackerBaseProtMaxZ)
                    {
                        entityAttackEvent.setCancelled(true);
                        return;
                    }
                    
                    for (GameTeam gameTeam : gameInstance.teams)
                    {
                        MapBase mapBase = gameTeam.mapBase;
                        
                        if (mapBase.isInDefenderBoundingBox(victimLocation))
                        {
                            entityAttackEvent.setCancelled(true);
                            return;
                        }
                    }
                    
                    gameInstance.lastDamagedBy.put(victim, attacker);
                    
                    if (victim.getHealth() - entityAttackEvent.getFinalDamage() <= 0)
                    {
                        try
                        {
                            gameInstance.gameWorld.playSound(victim.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
                        }
                        catch (Throwable throwable)
                        {
                            gameInstance.gameWorld.playSound(victim.getLocation(), Sound.valueOf("ENTITY_SKELETON_HURT"), 1.0F, 1.0F);
                        }
                        
                        GamePlayer attackerPlayer = attackerTeam.getGamePlayer(attacker);
                        
                        GamePlayer victimPlayer = victimTeam.getGamePlayer(victim);
                        
                        if (arrow != null)
                        {
                            String deathMessage = victimPlayer.addBowDeathFeed(attacker);
                            Assault.INSTANCE.getLogger().log(Level.INFO, ChatColor.stripColor(String.format("DEATH [%s]: %s", gameInstance.gameUUID, deathMessage)));
                            attackerPlayer.killPlayer(victim, true, false);
                        }
                        else
                        {
                            attackerPlayer.killPlayer(victim, false, false);
                        }
                        
                        entityAttackEvent.setCancelled(true);
                    }
                }
            }
        }
    }
}
