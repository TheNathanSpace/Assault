package com.thekingelessar.assault.game.eventhandlers.world;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.Objective;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemTool;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.lobby.LobbyUtil;
import com.thekingelessar.assault.util.version.XMaterial;
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
        ItemStack itemStack = playerDropItemEvent.getItemDrop().getItemStack();
        
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        if (gameInstance != null)
        {
            GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
            PlayerMode playerMode = gameInstance.getPlayerMode(player);
            if (!playerMode.canDropItems)
            {
                playerDropItemEvent.setCancelled(true);
                return;
            }
            
            if (playerMode.equals(PlayerMode.BUILDING) && gameInstance.modManualStar.enabled)
            {
                if (itemStack.getType().equals(XMaterial.NETHER_STAR.parseMaterial()))
                {
                    boolean objectiveMade = Objective.dropObjective(gamePlayer);
                    
                    if (objectiveMade)
                    {
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), GameInstance.retrieveObjectiveItem.clone());
                        playerDropItemEvent.getItemDrop().remove();
                    }
                    else
                    {
                        playerDropItemEvent.setCancelled(true);
                    }
                    return;
                }
                else if (itemStack.getType().equals(XMaterial.BARRIER.parseMaterial()))
                {
                    Objective.recallObjective(gamePlayer);
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), GameInstance.manualPlacementStar.clone());
                    playerDropItemEvent.getItemDrop().remove();
                }
            }
        }
        
        if (LobbyUtil.undroppable.contains(itemStack))
        {
            playerDropItemEvent.setCancelled(true);
            return;
        }
        
        if (itemStack.getType().equals(XMaterial.COMPASS.parseMaterial()))
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