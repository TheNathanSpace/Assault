package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
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
import org.bukkit.event.inventory.ClickType;
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
        
        if (gameInstance.gameStage.equals(GameStage.PREGAME))
        {
            return;
        }
        
        GameTeam playerTeam = gameInstance.getPlayerTeam(player);
        GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
        
        Inventory inventoryOpen = inventoryClickEvent.getInventory();
        ItemStack itemClicked = inventoryClickEvent.getCurrentItem();
        
        if (!playerTeam.getShopInventories().contains(inventoryClickEvent.getInventory()) && !inventoryClickEvent.getInventory().equals(playerTeam.shopAttacking.secretStorage))
        {
            return;
        }
        
        if (placingItemInTop(inventoryClickEvent))
        {
            inventoryClickEvent.setCancelled(true);
            return;
        }
        
        ShopItem shopItemClicked = null;
        if (playerTeam.shopBuilding != null && inventoryOpen.equals(playerTeam.shopBuilding.inventory))
        {
            ShopBuilding shop = playerTeam.shopBuilding;
            shopItemClicked = ShopItem.getShopItem(shop, itemClicked);
        }
        else if (playerTeam.shopAttacking != null && inventoryOpen.equals(playerTeam.shopAttacking.inventory))
        {
            if (itemClicked.equals(playerTeam.shopAttacking.storageItem))
            {
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5F, 1.3F);
                player.openInventory(playerTeam.shopAttacking.secretStorage);
                inventoryClickEvent.setCancelled(true);
                return;
            }
            
            ShopAttack shop = playerTeam.shopAttacking;
            shopItemClicked = ShopItem.getShopItem(shop, itemClicked);
        }
        else if (playerTeam.shopAttacking != null && inventoryClickEvent.getInventory().equals(playerTeam.shopAttacking.secretStorage))
        {
            if (itemClicked.equals(playerTeam.shopAttacking.goldItem))
            {
                player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 0.5F, 1.3F);
                player.openInventory(playerTeam.shopAttacking.inventory);
                inventoryClickEvent.setCancelled(true);
                return;
            }
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
        
        //        if (gameInstance.gameStage.equals(GameStage.BUILDING))
        //        {
        //            boolean noCoinsLeft = true;
        //            for (Player currentPlayer : gameInstance.getPlayers())
        //            {
        //                GamePlayer currentGamePlayer = gameInstance.getGamePlayer(currentPlayer);
        //                if (currentGamePlayer.playerBank.coins > 0)
        //                {
        //                    noCoinsLeft = false;
        //                    break;
        //                }
        //            }
        //
        //            if (noCoinsLeft)
        //            {
        //                gameInstance.taskCountdownBuilding.ticksLeft = 200;
        //
        //                for (Player currentPlayer : gameInstance.getPlayers())
        //                {
        //                    currentPlayer.sendRawMessage(Assault.assaultPrefix + "No coins left! Building time ended.");
        //                }
        //            }
        //        }
        
        inventoryClickEvent.setCancelled(true);
    }
    
    private boolean placingItemInTop(InventoryClickEvent inventoryClickEvent)
    {
        Inventory destInvent = inventoryClickEvent.getInventory();
        int slotClicked = inventoryClickEvent.getRawSlot();
        if (slotClicked < destInvent.getSize())
        {
            if (inventoryClickEvent.getWhoClicked().getItemOnCursor() != null && !inventoryClickEvent.getWhoClicked().getItemOnCursor().getType().equals(Material.AIR))
            {
                return true;
            }
        }
        else
        {
            if (inventoryClickEvent.getClick().equals(ClickType.SHIFT_LEFT) || inventoryClickEvent.getClick().equals(ClickType.SHIFT_RIGHT))
            {
                return true;
            }
        }
        
        return false;
    }
}