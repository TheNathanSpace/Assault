package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.timertasks.TaskCountdownRespawn;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

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
                
                if (PlayerMode.getPlayerMode(damagedPlayer).equals(PlayerMode.BUILDING))
                {
                    damagedPlayer.teleport(playerGameInstance.gameMap.getSpawn(playerGameInstance.getPlayerTeam(damagedPlayer), null).toLocation(playerGameInstance.gameWorld));
                    damagedPlayer.setHealth(damagedPlayer.getMaxHealth());
                }
                
                List<Material> dropItems = new ArrayList<>();
                dropItems.add(Material.EMERALD);
                
                PlayerInventory inventory = damagedPlayer.getInventory();
                for (ItemStack itemStack : inventory.getContents())
                {
                    if (dropItems.contains(itemStack.getType()))
                    {
                        Location location = damagedPlayer.getLocation();
                        location.getWorld().dropItemNaturally(location, itemStack);
                    }
                }
                
                PlayerMode.setPlayerMode(damagedPlayer, PlayerMode.SPECTATOR, playerGameInstance);
                
                damagedPlayer.teleport(playerGameInstance.gameMap.waitingSpawn.toLocation(playerGameInstance.gameWorld));
                
                GameTeam gameTeam = playerGameInstance.getPlayerTeam(damagedPlayer);
                GamePlayer gamePlayer = gameTeam.getGamePlayer(damagedPlayer);
                
                gamePlayer.taskCountdownRespawn = new TaskCountdownRespawn(60, 0, 20, playerGameInstance, damagedPlayer);
                gamePlayer.taskCountdownRespawn.runTaskTimer(Assault.INSTANCE, gamePlayer.taskCountdownRespawn.startDelay, gamePlayer.taskCountdownRespawn.tickDelay);
                
                entityDamageEvent.setCancelled(true);
            }
        }
    }
}
