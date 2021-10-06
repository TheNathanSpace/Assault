package com.thekingelessar.assault.game.eventhandlers.world;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.Objective;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemTool;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.lobby.LobbyUtil;
import com.thekingelessar.assault.util.version.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
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
            }
            
            if (itemStack.getType().equals(XMaterial.NETHER_STAR.parseMaterial()))
            {
                if (playerMode.equals(PlayerMode.BUILDING) && gameInstance.modManualStar.enabled)
                {
                    Item droppedItem = playerDropItemEvent.getItemDrop();
                    int highestY = player.getWorld().getHighestBlockYAt(player.getLocation());
                    Location objectiveLocation = player.getLocation().clone();
                    objectiveLocation.setY(highestY);
                    
                    Objective objective = new Objective(gameInstance, gamePlayer.gameTeam, droppedItem, objectiveLocation, player.getUniqueId());
                    objective.sendToLocation();
                    gameInstance.buildingObjectives.add(objective);
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