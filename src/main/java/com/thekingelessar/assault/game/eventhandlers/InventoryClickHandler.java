package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.ShopItem;
import com.thekingelessar.assault.game.inventory.TeamBuffItem;
import com.thekingelessar.assault.game.inventory.shops.ShopAttack;
import com.thekingelessar.assault.game.inventory.shops.ShopBuilding;
import com.thekingelessar.assault.game.inventory.shops.ShopTeamBuffs;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.lobby.LobbyUtil;
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
import java.util.Arrays;
import java.util.List;

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
            TeamBuffItem clickedBuff = TeamBuffItem.getShopItem(shop, itemClicked);
            
            if (clickedBuff == null)
            {
                return;
            }
            
            if (clickedBuff.purchased)
            {
                player.playSound(player.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
                inventoryClickEvent.setCancelled(true);
                return;
            }
            
            boolean canPurchase = gamePlayer.purchaseItem(clickedBuff.cost, clickedBuff.currency);
            
            if (canPurchase)
            {
                playerTeam.buffList.add(clickedBuff.buff);
                
                gamePlayer.updateScoreboard();
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.8F, 1.0F);
                
                clickedBuff.purchase(playerTeam.shopTeamBuffs.inventory, inventoryClickEvent.getSlot());
                
                inventoryClickEvent.setCancelled(true);
                return;
            }
            
            inventoryClickEvent.setCancelled(true);
            return;
        }
        
        if (shopItemClicked == null)
        {
            return;
        }
        
        boolean canPurchase = gamePlayer.purchaseItem(shopItemClicked.cost, shopItemClicked.currency);
    
        List<Material> toolsList = Arrays.asList(Material.SHEARS, Material.WOOD_AXE, Material.WOOD_PICKAXE, Material.STONE_AXE, Material.STONE_PICKAXE, Material.GOLD_AXE, Material.GOLD_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE);
        
        for (ItemStack itemStack : player.getInventory().getContents())
        {
            if (itemStack != null)
            {
                if (toolsList.contains(itemStack.getType()) && shopItemClicked.realItemStack.getType().equals(itemStack.getType()))
                {
                    player.playSound(player.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
                    inventoryClickEvent.setCancelled(true);
                    return;
                }
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
            
            List<Material> swordList = Arrays.asList(Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD);
            if (swordList.contains(givingItemStack.getType()))
            {
                boolean hasGoodSword = false;
                for (ItemStack inventoryItem : player.getInventory().getContents())
                {
                    if (inventoryItem != null)
                    {
                        if (swordList.contains(inventoryItem.getType()))
                        {
                            hasGoodSword = true;
                            break;
                        }
                    }
                }
                
                if (hasGoodSword)
                {
                    player.getInventory().addItem(givingItemStack);
                }
                else
                {
                    ItemStack[] contents = player.getInventory().getContents();
                    for (int i = 0; i < contents.length; i++)
                    {
                        ItemStack inventoryItem = contents[i];
                        if (inventoryItem != null)
                        {
                            if (inventoryItem.getType().equals(Material.WOOD_SWORD))
                            {
                                player.getInventory().setItem(i, givingItemStack);
                                break;
                            }
                        }
                    }
                }
            }
            else
            {
                player.getInventory().addItem(givingItemStack);
            }
            
            if (toolsList.contains(givingItemStack.getType()) && !(playerTeam.shopTeamBuffs != null && inventoryOpen.equals(playerTeam.shopTeamBuffs.inventory)))
            {
                gamePlayer.addSpawnItem(givingItemStack);
            }
            
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.8F, 1.0F);
            
            gamePlayer.updateScoreboard();
        }
        
        inventoryClickEvent.setCancelled(true);
    }
}
