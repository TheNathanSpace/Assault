package com.thekingelessar.assault.game.pregame.modifiers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.IShop;
import com.thekingelessar.assault.game.inventory.ShopUtil;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerShopModifiers implements IShop
{
    public GameInstance gameInstance;
    public Player player;
    public Inventory inventory;
    
    public Map<ItemStack, ShopItemModifier> shopItemMap = new HashMap<>();
    private List<Integer> airSlots = new ArrayList<>();
    
    @Override
    public ShopItem getShopItem(ItemStack itemStack)
    {
        return this.shopItemMap.get(itemStack);
    }
    
    public PlayerShopModifiers(GameInstance gameInstance, Player player)
    {
        this.player = player;
        
        for (int i = 0; i < 10; i++)
        {
            airSlots.add(i);
        }
        
        inventory = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Game Modifiers");
        
        constructShopItemModifier(new ItemStack(Material.WATCH, 0), gameInstance.modInfiniteTime, gameInstance.modInfiniteTime.name, gameInstance.modInfiniteTime.description, false);
        constructShopItemModifier(new ItemStack(Material.QUARTZ, 0), gameInstance.modFirstTo5Stars, gameInstance.modFirstTo5Stars.name, gameInstance.modFirstTo5Stars.description, false);
        constructShopItemModifier(new ItemStack(Material.EYE_OF_ENDER, 0), gameInstance.modDisableWildcardItems, gameInstance.modDisableWildcardItems.name, gameInstance.modDisableWildcardItems.description, false);
        constructShopItemModifier(new ItemStack(Material.PAPER, 0), gameInstance.modUseTeamSelection, gameInstance.modUseTeamSelection.name, gameInstance.modUseTeamSelection.description, false);
    }
    
    private void constructShopItemModifier(ItemStack notVotedItemStack, GameModifier gameModifier, String name, String description, boolean newRow)
    {
        ItemMeta itemMeta = notVotedItemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET.toString() + ChatColor.BOLD + name + ChatColor.RESET);
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET + description);
        lore.add("");
        lore.add(ChatColor.RESET + "Click to " + ChatColor.GREEN + "vote" + ChatColor.RESET + "!");
        itemMeta.setLore(lore);
        
        notVotedItemStack.setItemMeta(itemMeta);
        
        ItemStack alreadyVotedItemStack = notVotedItemStack.clone();
        ItemMeta alreadyPurchasedMeta = alreadyVotedItemStack.getItemMeta();
        alreadyPurchasedMeta.setDisplayName(ChatColor.GREEN + "[VOTED] " + ChatColor.RESET + ChatColor.BOLD + name);
        
        List<String> alreadyPurchasedLore = new ArrayList<>();
        alreadyPurchasedLore.add(ChatColor.RESET + description);
        alreadyPurchasedLore.add("");
        alreadyPurchasedLore.add(ChatColor.RESET + "Click to " + ChatColor.RED + "remove vote" + ChatColor.RESET + "!");
        alreadyPurchasedMeta.setLore(alreadyPurchasedLore);
        
        alreadyVotedItemStack.setItemMeta(alreadyPurchasedMeta);
        
        ShopItemModifier shopItem = new ShopItemModifier(notVotedItemStack, alreadyVotedItemStack, gameModifier);
        
        this.shopItemMap.put(notVotedItemStack, shopItem);
        
        ShopUtil.insertItem(this.inventory, airSlots, shopItem, newRow);
    }
    
    public void updateCounts()
    {
        Map<ItemStack, ShopItemModifier> addItems = new HashMap<>();
        List<ItemStack> removeItems = new ArrayList<>();
        
        for (ShopItemModifier shopItemModifier : this.shopItemMap.values())
        {
            removeItems.add(shopItemModifier.alreadyVotedItemStack);
            removeItems.add(shopItemModifier.shopItemStack);
            
            ItemStack newItem = shopItemModifier.updateCount(this);
            addItems.put(newItem, shopItemModifier);
        }
        
        for (ItemStack itemStack : removeItems)
        {
            this.shopItemMap.remove(itemStack);
        }
        
        for (Map.Entry<ItemStack, ShopItemModifier> entry : addItems.entrySet())
        {
            this.shopItemMap.put(entry.getKey(), entry.getValue());
        }
    }
}
