package com.thekingelessar.assault.game.eventhandlers.playermovement;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.player.DeathType;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.world.map.MapBase;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

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
                
                if (enemyMapBase.isInDefenderBoundingBox(locTo))
                {
                    cancelMovement(playerMoveEvent);
                    return;
                }
            }
            
            if (locTo.getZ() > gameInstance.gameMap.maxZ)
            {
                cancelMovement(playerMoveEvent);
                return;
            }
            if (locTo.getZ() < gameInstance.gameMap.minZ)
            {
                cancelMovement(playerMoveEvent);
                return;
            }
            
            if (gameInstance.gameStage.equals(GameStage.BUILDING))
            {
                if (locTo.getZ() < gameInstance.gameMap.attackerBaseProtMaxZ)
                {
                    cancelMovement(playerMoveEvent);
                    return;
                }
            }
            else if (gameInstance.getDefendingTeam().equals(gameInstance.getPlayerTeam(player)))
            {
                if (locTo.getZ() < gameInstance.gameMap.attackerBaseProtMaxZ)
                {
                    cancelMovement(playerMoveEvent);
                    return;
                }
            }
            
            GameTeam gameTeam = gameInstance.getPlayerTeam(player);
            if (gameTeam != null)
            {
                GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
                gamePlayer.setCompassObjective();
                
                if (gamePlayer.startTimeInAir != 0)
                {
                    if (((Entity) player).isOnGround())
                    {
                        gamePlayer.startTimeInAir = 0;
                    } // todo: when only barely touching web, ded
                    else if (locTo.getBlock() != null && locTo.getBlock().getType().equals(Material.WEB))
                    {
                        System.out.println("Block at FEET is web");
                        gamePlayer.startTimeInAir = 0;
                    }
                    else if ((player.getEyeLocation().getBlock() != null && player.getEyeLocation().getBlock().getRelative(BlockFace.UP) != null && player.getEyeLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.WEB)))
                    {
                        System.out.println("Block at EYE is web");
                        gamePlayer.startTimeInAir = 0;
                    }
                    else if (locTo.getWorld().getBlockAt(locTo) != null && locTo.getWorld().getBlockAt(locTo).getType().equals(Material.WEB))
                    {
                        System.out.println("Block at LOCATION is web");
                    }
                    else if ((locTo.getBlock() != null && locTo.getBlock().getRelative(BlockFace.UP) != null && locTo.getBlock().getRelative(BlockFace.UP).getType().equals(Material.LADDER)))
                    {
                        System.out.println("In ladder");
                        gamePlayer.startTimeInAir = 0;
                    }
                    else if (player.getVehicle() != null)
                    {
                        System.out.println("In vehicle");
                        gamePlayer.startTimeInAir = 0;
                    }
                    else if ((System.nanoTime() - gamePlayer.startTimeInAir) / 1000000000. > 5) // todo: nano time comparison?
                    {
                        if (PlayerMode.getPlayerMode(player).equals(PlayerMode.SPECTATOR))
                        {
                            gamePlayer.startTimeInAir = 0;
                        }
                        else
                        {
                            player.sendMessage(Assault.ASSAULT_PREFIX + "Looks like you're stuck—respawning!");
                            gamePlayer.respawn(null, true, DeathType.DEATH);
                        }
                    }
                }
                else
                {
                    if (locTo.getY() > playerMoveEvent.getFrom().getY())
                    {
                        gamePlayer.startTimeInAir = System.nanoTime();
                    }
                }
                
                if (((Entity) player).isOnGround() && !gamePlayer.flightReset)
                {
                    gamePlayer.flightReset = true;
                }
            }
            
        }
        
        Location newLocation = playerMoveEvent.getTo();
        double voidY = newLocation.getY();
        
        if (gameInstance == null)
        {
            return;
        }
        if (voidY < gameInstance.gameMap.voidLevel && gameInstance.gameMap.voidEnabled)
        {
            GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
            if (gamePlayer != null)
            {
                gamePlayer.respawn(null, true, DeathType.VOID);
            }
        }
        
        if (gameInstance.gameStage.equals(GameStage.PREGAME))
        {
            return;
        }
        
        if (PlayerMode.getPlayerMode(player) != null && PlayerMode.getPlayerMode(player).equals(PlayerMode.SPECTATOR))
        {
            return;
        }
        
        if (gameInstance.gameStage.equals(GameStage.BUILDING))
        {
            GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
            if (gamePlayer != null)
            {
                if (newLocation.getX() > gameInstance.gameMap.borderX && gamePlayer.gameTeam.mapBase.objectives.get(0).x < gameInstance.gameMap.borderX)
                {
                    cancelMovement(playerMoveEvent);
                    player.sendMessage(Assault.ASSAULT_PREFIX + "You can't go over here!");
                    return;
                }
                
                if (newLocation.getX() < gameInstance.gameMap.borderX && gamePlayer.gameTeam.mapBase.objectives.get(0).x > gameInstance.gameMap.borderX)
                {
                    cancelMovement(playerMoveEvent);
                    player.sendMessage(Assault.ASSAULT_PREFIX + "You can't go over here!");
                    return;
                }
            }
            
            return;
        }
        if (gameInstance.gameStage.equals(GameStage.ATTACKING))
        {
            if (newLocation.getX() > gameInstance.gameMap.borderX && gameInstance.getDefendingTeam().mapBase.objectives.get(0).x < gameInstance.gameMap.borderX)
            {
                GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
                if (gamePlayer != null)
                {
                    cancelMovement(playerMoveEvent);
                    player.sendMessage(Assault.ASSAULT_PREFIX + "You can't go over here!");
                    return;
                }
            }
            
            if (newLocation.getX() < gameInstance.gameMap.borderX && gameInstance.getDefendingTeam().mapBase.objectives.get(0).x > gameInstance.gameMap.borderX)
            {
                GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
                if (gamePlayer != null)
                {
                    cancelMovement(playerMoveEvent);
                    player.sendMessage(Assault.ASSAULT_PREFIX + "You can't go over here!");
                    return;
                }
            }
        }
    }
    
    private void cancelMovement(PlayerMoveEvent playerMoveEvent)
    {
        Player player = playerMoveEvent.getPlayer();
        
        Vector fromVec = playerMoveEvent.getFrom().toVector();
        Vector toVec = playerMoveEvent.getTo().toVector();
        Vector newVec = fromVec.subtract(toVec).normalize();
        newVec.setY(0.5);
        
        player.setVelocity(newVec);
        
        playerMoveEvent.setCancelled(true);
    }
    
}
