package com.thekingelessar.assault.game.eventhandlers.world;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemTool;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.lobby.LobbyUtil;
import com.thekingelessar.assault.util.xsupport.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

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
            if (LobbyUtil.undroppable.contains(itemStack))
            {
                playerDropItemEvent.setCancelled(true);
                return;
            }
            
            if (itemStack.getType().equals(XMaterial.COMPASS))
            {
                playerDropItemEvent.setCancelled(true);
                return;
            }
            
            if (ShopItemTool.axes.contains(itemStack.getType()) || ShopItemTool.pickaxes.contains(itemStack.getType()) || itemStack.getType().equals(XMaterial.SHEARS.parseMaterial()))
            {
                playerDropItemEvent.setCancelled(true);
                return;
            }
            
            List<Material> swordList = Arrays.asList(XMaterial.STONE_SWORD.parseMaterial(), XMaterial.IRON_SWORD.parseMaterial(), XMaterial.GOLDEN_SWORD.parseMaterial(), XMaterial.DIAMOND_SWORD.parseMaterial());
            
            if (itemStack.getType().equals(XMaterial.WOODEN_SWORD.parseMaterial()))
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
                    player.getInventory().addItem(new ItemStack(XMaterial.WOODEN_SWORD.parseMaterial()));
                }
            }
        }
    }
}