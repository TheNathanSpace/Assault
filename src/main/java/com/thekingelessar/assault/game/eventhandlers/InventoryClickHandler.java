package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemBuff;
import com.thekingelessar.assault.game.inventory.shops.ShopAttack;
import com.thekingelessar.assault.game.inventory.shops.ShopBuilding;
import com.thekingelessar.assault.game.inventory.shops.ShopTeamBuffs;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.lobby.LobbyUtil;
import org.bukkit.Material;
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
        
        if (gameInstance == null)
        {
            if (player.getWorld().equals(Assault.lobbyWorld))
            {
                ItemStack itemStack = inventoryClickEvent.getCurrentItem();
                
                if (itemStack != null)
                {
                    if (itemStack.equals(LobbyUtil.joinGameStar))
                    {
                        LobbyUtil.joinQueue(player);
                    }
                    
                    if (itemStack.getType().equals(Material.BARRIER))
                    {
                        LobbyUtil.leaveQueue(player);
                    }
                }
                
                inventoryClickEvent.setCancelled(true);
            }
            
            return;
        }
        
        GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
        
        Inventory inventoryOpen = inventoryClickEvent.getInventory();
        ItemStack itemClicked = inventoryClickEvent.getCurrentItem();
        
        ShopItem shopItemClicked = null;
        
        GameTeam playerTeam = gameInstance.getPlayerTeam(player);
        
        if (playerTeam.shopBuilding != null && inventoryOpen.equals(playerTeam.shopBuilding.inventory))
        {
            ShopBuilding shop = playerTeam.shopBuilding;
            shopItemClicked = ShopItem.getShopItem(shop, itemClicked);
        }
        else if (playerTeam.shopAttacking != null && inventoryOpen.equals(playerTeam.shopAttacking.inventory))
        {
            ShopAttack shop = playerTeam.shopAttacking;
            shopItemClicked = ShopItem.getShopItem(shop, itemClicked);
        }
        else if (playerTeam.shopTeamBuffs != null && inventoryOpen.equals(playerTeam.shopTeamBuffs.inventory))
        {
            ShopTeamBuffs shop = playerTeam.shopTeamBuffs;
            shopItemClicked = ShopItem.getShopItem(shop, itemClicked);
        }
        
        if (shopItemClicked == null)
        {
            return;
        }
        
        shopItemClicked.buyItem(player);
        gamePlayer.updateScoreboard();
        
        inventoryClickEvent.setCancelled(true);
    }
}
