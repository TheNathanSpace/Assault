package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.map.MapBase;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.timertasks.TaskCountdownRespawn;
import com.thekingelessar.assault.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerMoveHandler implements Listener
{
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent)
    {
        Player player = playerMoveEvent.getPlayer();
        Location locTo = playerMoveEvent.getTo();
        
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        if (gameInstance != null && !gameInstance.gameStage.equals(GameStage.PREGAME) && !gameInstance.gameStage.equals(GameStage.FINISHED))
        {
            GameTeam oppositeTeam = gameInstance.getOppositeTeam(player);
            if (oppositeTeam != null)
            {
                MapBase enemyMapBase = oppositeTeam.mapBase;
                
                if (Util.isInside(locTo, enemyMapBase.defenderBoundingBox.get(0).toLocation(gameInstance.gameWorld), enemyMapBase.defenderBoundingBox.get(1).toLocation(gameInstance.gameWorld)))
                {
                    playerMoveEvent.setCancelled(true);
                    return;
                }
            }
            
            if (gameInstance.getDefendingTeam().equals(gameInstance.getPlayerTeam(player)))
            {
                if (locTo.getZ() < gameInstance.gameMap.attackerBaseProtMinZ)
                {
                    playerMoveEvent.setCancelled(true);
                    return;
                }
            }
            
        }
        
        Location newLocation = playerMoveEvent.getTo();
        double voidY = newLocation.getY();
        
        if (voidY < 70)
        {
            if (gameInstance != null)
            {
                if (gameInstance.lastDamagedBy.get(player) != null)
                {
                    Player attacker = gameInstance.lastDamagedBy.get(player);
                    gameInstance.getPlayerTeam(attacker).gamerPoints += 1;
                    
                    GamePlayer attackerPlayer = gameInstance.getGamePlayer(attacker);
                    GamePlayer victimPlayer = gameInstance.getGamePlayer(player);
                    
                    attackerPlayer.playerBank.coins += (int) (0.2 * (victimPlayer.playerBank.coins));
                    attacker.playSound(attacker.getLocation(), Sound.ORB_PICKUP, 0.8F, 1.0F);
                    
                    int emeraldCount = 0;
                    for (ItemStack itemStack : player.getInventory().getContents())
                    {
                        if (itemStack != null && itemStack.getType().equals(Material.EMERALD))
                        {
                            emeraldCount += itemStack.getAmount();
                        }
                    }
                    
                    if (emeraldCount > 0)
                    {
                        attacker.getInventory().addItem(new ItemStack(Material.EMERALD, emeraldCount));
                    }
                    
                    attackerPlayer.updateScoreboard();
                    
                    gameInstance.lastDamagedBy.put(player, null);
                }
                
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
