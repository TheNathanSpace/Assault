package com.thekingelessar.assault.game.eventhandlers.inventory;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import com.thekingelessar.assault.game.inventory.shops.ShopAttack;
import com.thekingelessar.assault.game.inventory.shops.ShopBuilding;
import com.thekingelessar.assault.game.inventory.shops.ShopTeamBuffs;
import com.thekingelessar.assault.game.modifiers.PlayerShopModifiers;
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
        ItemStack itemStack = inventoryClickEvent.getCurrentItem();
        
        if (gameInstance == null)
        {
            if (player.getWorld().equals(Assault.lobbyWorld))
            {
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
            if (itemStack != null && itemStack.equals(GameInstance.gameModifierItemStack))
            {
                player.openInventory(gameInstance.modifierShopMap.get(player).inventory);
                inventoryClickEvent.setCancelled(true);
                return;
            }
        }
        
        GameTeam playerTeam = gameInstance.getPlayerTeam(player);
        GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
        
        Inventory inventoryOpen = inventoryClickEvent.getInventory();
        ItemStack itemClicked = inventoryClickEvent.getCurrentItem();
        
        if (playerTeam != null)
        {
            if (!playerTeam.getShopInventories().contains(inventoryClickEvent.getInventory()) && !inventoryClickEvent.getInventory().equals(playerTeam.secretStorage))
            {
                return;
            }
            
            if (!inventoryClickEvent.getInventory().equals(playerTeam.secretStorage) && placingItemInTop(inventoryClickEvent))
            {
                inventoryClickEvent.setCancelled(true);
                return;
            }
        }
        
        ShopItem shopItemClicked = null;
        if (gameInstance.modifierShopMap != null && inventoryOpen.equals(gameInstance.modifierShopMap.get(player).inventory))
        {
            PlayerShopModifiers shop = gameInstance.modifierShopMap.get(player);
            shopItemClicked = ShopItem.getShopItem(shop, itemClicked);
        }
        else if (playerTeam.shopBuilding != null && inventoryOpen.equals(playerTeam.shopBuilding.inventory))
        {
            ShopBuilding shop = playerTeam.shopBuilding;
            shopItemClicked = ShopItem.getShopItem(shop, itemClicked);
        }
        else if (inventoryOpen.equals(gamePlayer.shopAttacking.inventory))
        {
            if (itemClicked.equals(gamePlayer.shopAttacking.storageItem))
            {
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5F, 1.3F);
                player.openInventory(playerTeam.secretStorage);
                inventoryClickEvent.setCancelled(true);
                return;
            }
            
            ShopAttack shop = gamePlayer.shopAttacking;
            shopItemClicked = ShopItem.getShopItem(shop, itemClicked);
        }
        else if (inventoryClickEvent.getInventory().equals(playerTeam.secretStorage))
        {
            if (itemClicked.equals(playerTeam.goldItem))
            {
                player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 0.5F, 1.3F);
                player.openInventory(gamePlayer.shopAttacking.inventory);
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
        
        if (gamePlayer != null)
        {
            gamePlayer.updateScoreboard();
        }
        
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