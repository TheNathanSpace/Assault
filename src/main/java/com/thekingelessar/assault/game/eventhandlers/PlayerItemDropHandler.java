package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.lobby.LobbyUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerItemDropHandler implements Listener
{
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent playerDropItemEvent)
    {
        Player player = playerDropItemEvent.getPlayer();
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        
        if (gameInstance != null)
        {
            PlayerMode playerMode = gameInstance.getPlayerMode(player);
            if (!playerMode.canDropItems)
            {
                playerDropItemEvent.setCancelled(true);
            }
        }
        
        ItemStack itemStack = playerDropItemEvent.getItemDrop().getItemStack();
        
        if (itemStack != null)
        {
            if (itemStack.equals(LobbyUtil.joinGameStar))
            {
                playerDropItemEvent.setCancelled(true);
                return;
            }
            
            List<Material> toolsList = Arrays.asList(Material.SHEARS, Material.WOOD_AXE, Material.WOOD_PICKAXE, Material.STONE_AXE, Material.STONE_PICKAXE, Material.GOLD_AXE, Material.GOLD_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE);
            if (toolsList.contains(itemStack.getType()))
            {
                playerDropItemEvent.getItemDrop().remove();
                return;
            }
            
            List<Material> swordList = Arrays.asList(Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD);
            
            if (itemStack.getType().equals(Material.WOOD_SWORD))
            {
                for (ItemStack inventoryItem : player.getInventory().getContents())
                {
                    if (inventoryItem != null)
                    {
                        if (swordList.contains(inventoryItem.getType()))
                        {
                            playerDropItemEvent.getItemDrop().remove();
                            return;
                        }
                    }
                }
                
                playerDropItemEvent.setCancelled(true);
            }
            
            if (swordList.contains(itemStack.getType()))
            {
                int swordCount = 0;
                for (ItemStack inventoryItem : player.getInventory().getContents())
                {
                    if (inventoryItem != null)
                    {
                        if (swordList.contains(inventoryItem.getType()))
                        {
                            swordCount++;
                        }
                    }
                }
                
                if (swordCount == 0)
                {
                    player.getInventory().addItem(new ItemStack(Material.WOOD_SWORD));
                }
            }
        }
    }
}