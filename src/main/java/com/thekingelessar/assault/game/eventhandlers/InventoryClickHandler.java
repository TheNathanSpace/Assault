package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.ShopBuilding;
import com.thekingelessar.assault.game.inventory.ShopItem;
import com.thekingelessar.assault.game.player.GamePlayer;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
            
            if (shopItemClicked == null)
            {
                return;
            }
            
            boolean canPurchase = gamePlayer.purchaseItem(shopItemClicked.cost, shopItemClicked.currency);
            
            if (canPurchase)
            {
                ItemStack givingItemStack = shopItemClicked.realItemStack.clone();
                
                List<Material> durabilityList = new ArrayList<>();
                durabilityList.add(Material.WOOL);
                durabilityList.add(Material.STAINED_CLAY);
                durabilityList.add(Material.STAINED_GLASS);
                
                if (durabilityList.contains(shopItemClicked.realItemStack.getType()))
                {
                    givingItemStack.setDurability(DyeColor.valueOf(gameInstance.getPlayerTeam(player).color.toString()).getData());
                }
                
                player.getInventory().addItem(givingItemStack);
                
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.8F, 1.0F);
                
                gamePlayer.updateScoreboard();
            }
            
            inventoryClickEvent.setCancelled(true);
        }
    }
}
