package com.thekingelessar.assault.game.eventhandlers.world;

import com.thekingelessar.assault.game.GameEndManager;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class PlayerPickupItemHandler implements Listener
{
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent playerPickupItemEvent)
    {
        Material pickedUp = playerPickupItemEvent.getItem().getItemStack().getType();
        Player player = playerPickupItemEvent.getPlayer();
        
        List<Material> swordList = Arrays.asList(Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD);
        if (swordList.contains(pickedUp))
        {
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length; i++)
            {
                ItemStack inventoryItem = contents[i];
                if (inventoryItem != null && inventoryItem.getType().equals(Material.WOOD_SWORD))
                {
                    player.getInventory().setItem(i, playerPickupItemEvent.getItem().getItemStack());
                    playerPickupItemEvent.setCancelled(true);
                    playerPickupItemEvent.getItem().remove();
                    break;
                }
            }
        }
        
        if (pickedUp.equals(Material.NETHER_STAR))
        {
            GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
            
            if (gameInstance != null)
            {
                GameTeam gameTeam = gameInstance.getPlayerTeam(player);
                
                if (gameInstance.gameStage.equals(GameStage.BUILDING) || !gameInstance.getAttackingTeam().equals(gameTeam))
                {
                    playerPickupItemEvent.setCancelled(true);
                    return;
                }
                
                if (gameInstance.taskAttackTimer == null)
                {
                    playerPickupItemEvent.setCancelled(true);
                    return;
                }
                
                gameInstance.endRound(false);
                
                if (gameInstance.teamsGone == 1)
                {
                    boolean onlyOneTeamLeft = false;
                    for (GameTeam instanceGameTeam : gameInstance.teams.values())
                    {
                        if (instanceGameTeam.getPlayers().size() == 0)
                        {
                            onlyOneTeamLeft = true;
                            break;
                        }
                    }
                    
                    if (onlyOneTeamLeft)
                    {
                        gameInstance.winningTeam = gameInstance.getRemainingTeam();
                        gameInstance.gameEndManager.declareWinners(GameEndManager.WinState.DEFENDERS_LEFT);
                    }
                    else
                    {
                        gameInstance.gameEndManager.declareWinners(GameEndManager.WinState.LOWEST_TIME);
                    }
                }
            }
        }
    }
}