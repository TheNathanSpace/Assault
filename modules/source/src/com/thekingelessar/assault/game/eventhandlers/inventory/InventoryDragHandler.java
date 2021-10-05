package com.thekingelessar.assault.game.eventhandlers.inventory;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryDragHandler implements Listener
{
    
    @EventHandler
    public void onInventoryClick(InventoryDragEvent inventoryDragEvent)
    {
        Player player = (Player) inventoryDragEvent.getWhoClicked();
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        
        if (gameInstance == null)
        {
            return;
        }
        
        if (gameInstance.gameStage.equals(GameStage.PREGAME))
        {
            return;
        }
        
        GameTeam playerTeam = gameInstance.getPlayerTeam(player);
        
        if (!playerTeam.getShopInventories().contains(inventoryDragEvent.getInventory()))
        {
            return;
        }
        
        inventoryDragEvent.setCancelled(true);
        //        handlePlacingTop(inventoryDragEvent);
    }
    
    // Couldn't get working satisfactorily. Wanted to protect shop inventories while allowing dragging in player inventories.
    private void handlePlacingTop(InventoryDragEvent inventoryDragEvent)
    {
        Inventory destInvent = inventoryDragEvent.getInventory();
        List<Integer> slotsClicked = new ArrayList<>(inventoryDragEvent.getRawSlots());
        
        ItemStack itemStack = inventoryDragEvent.getOldCursor();
        int payment = 0;
        
        Map<Integer, ItemStack> newItems = new HashMap<>(inventoryDragEvent.getNewItems());
        
        for (Integer slotClicked : slotsClicked)
        {
            if (slotClicked < destInvent.getSize())
            {
                payment += inventoryDragEvent.getNewItems().get(slotClicked).getAmount();
                System.out.println("Added " + inventoryDragEvent.getNewItems().get(slotClicked).getAmount() + " to payment!");
                
                ItemStack newStack = inventoryDragEvent.getNewItems().get(slotClicked);
                newStack.setAmount(0);
                newItems.put(slotClicked, newStack);
            }
        }
        
        if (itemStack != null)
        {
            itemStack.setAmount(inventoryDragEvent.getOldCursor().getAmount() - payment);
            inventoryDragEvent.setCursor(itemStack);
        }
        
        System.out.println("This inventory has " + inventoryDragEvent.getInventory().getSize() + " slots!");
        for (Map.Entry<Integer, ItemStack> entry : newItems.entrySet())
        {
            System.out.println("Slot " + entry.getKey() + " has " + entry.getValue().getAmount() + " of " + entry.getValue().getType());
            inventoryDragEvent.getInventory().setItem(entry.getKey(), entry.getValue());
        }
    }
}