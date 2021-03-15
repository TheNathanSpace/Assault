package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.ShopBuilding;
import com.thekingelessar.assault.game.inventory.ShopItem;
import com.thekingelessar.assault.game.player.GamePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClickHandler implements Listener
{
    @EventHandler
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent)
    {
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        
        if (gameInstance == null) return;
        
        GamePlayer gamePlayer = gameInstance.getPlayerTeam(player).getGamePlayer(player);
        
        Inventory inventoryOpen = inventoryClickEvent.getInventory();
        ItemStack itemClicked = inventoryClickEvent.getCurrentItem();
        
        if (inventoryOpen.equals(gameInstance.getPlayerTeam(player).shopBuilding.inventory))
        {
            ShopBuilding shop = gameInstance.getPlayerTeam(player).shopBuilding;
            ShopItem shopItemClicked = ShopItem.getShopItem(shop, itemClicked);
            
            boolean canPurchase = gamePlayer.purchaseItem(shopItemClicked.cost, shopItemClicked.currency);
            
            if (canPurchase)
            {
                player.getInventory().addItem(shopItemClicked.realItemStack);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.8F, 1.0F);
            }
            
            inventoryClickEvent.setCancelled(true);
        }
    }
}
