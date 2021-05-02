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
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class PlayerKickHandler implements Listener
{
    @EventHandler
    public void onPlayerKick(PlayerKickEvent playerKickEvent)
    {
        Player player = playerKickEvent.getPlayer();
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        
        if (gameInstance != null)
        {
            if (playerKickEvent.getReason().toLowerCase().contains("packet") || playerKickEvent.getReason().toLowerCase().contains("flying"))
            {
                List<Material> dropItems = new ArrayList<>();
                dropItems.add(Material.EMERALD);
                
                PlayerInventory inventory = player.getInventory();
                for (ItemStack itemStack : inventory.getContents())
                {
                    if (itemStack != null)
                    {
                        if (dropItems.contains(itemStack.getType()))
                        {
                            Location location = player.getLocation();
                            location.getWorld().dropItemNaturally(location, itemStack);
                        }
                    }
                }
                
                PlayerMode.setPlayerMode(player, PlayerMode.SPECTATOR, gameInstance);
                
                player.teleport(gameInstance.gameMap.waitingSpawn.toLocation(gameInstance.gameWorld));
                
                GameTeam gameTeam = gameInstance.getPlayerTeam(player);
                GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
                
                gamePlayer.taskCountdownRespawn = new TaskCountdownRespawn(60, 0, 20, gameInstance, player);
                gamePlayer.taskCountdownRespawn.runTaskTimer(Assault.INSTANCE, gamePlayer.taskCountdownRespawn.startDelay, gamePlayer.taskCountdownRespawn.tickDelay);
                
                playerKickEvent.setCancelled(true);
            }
        }
    }
}
