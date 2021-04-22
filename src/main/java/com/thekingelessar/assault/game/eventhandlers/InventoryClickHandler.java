package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.ShopItem;
import com.thekingelessar.assault.game.inventory.shops.ShopAttack;
import com.thekingelessar.assault.game.inventory.shops.ShopBuilding;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
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
        
        ShopItem shopItemClicked = null;
        
        GameTeam playerTeam = gameInstance.getPlayerTeam(player);
        
        if (playerTeam.shopBuilding != null && inventoryOpen.equals(playerTeam.shopBuilding.inventory))
        {
            ShopBuilding shop = playerTeam.shopBuilding;
            shopItemClicked = ShopItem.getShopItem(shop, itemClicked);
        }
        else if (playerTeam.shopAttack != null && inventoryOpen.equals(playerTeam.shopAttack.inventory))
        {
            ShopAttack shop = playerTeam.shopAttack;
            shopItemClicked = ShopItem.getShopItem(shop, itemClicked);
        }
        
        if (shopItemClicked == null)
        {
            return;
        }
        
        boolean canPurchase = gamePlayer.purchaseItem(shopItemClicked.cost, shopItemClicked.currency);
        
        List<Material> toolsList = new ArrayList<>();
        toolsList.add(Material.SHEARS);
        toolsList.add(Material.WOOD_AXE);
        toolsList.add(Material.WOOD_PICKAXE);
        toolsList.add(Material.STONE_AXE);
        toolsList.add(Material.STONE_PICKAXE);
        toolsList.add(Material.GOLD_AXE);
        toolsList.add(Material.GOLD_PICKAXE);
        toolsList.add(Material.DIAMOND_AXE);
        toolsList.add(Material.DIAMOND_PICKAXE);
        
        for (ItemStack itemStack : player.getInventory().getContents())
        {
            if (toolsList.contains(itemStack.getType()))
            {
                player.playSound(player.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
                inventoryClickEvent.setCancelled(true);
                return;
            }
        }
        
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
            
            gamePlayer.addSpawnItem(givingItemStack);
            
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.8F, 1.0F);
            
            gamePlayer.updateScoreboard();
        }
        
        inventoryClickEvent.setCancelled(true);
    }
}
