package com.thekingelessar.assault.game.pregame.modifiers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.IShop;
import com.thekingelessar.assault.game.inventory.ShopUtil;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import com.thekingelessar.assault.util.Util;
import com.thekingelessar.assault.util.version.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
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
        
        constructShopItemModifier(XMaterial.CLOCK.parseItem(), gameInstance.modInfiniteTime, gameInstance.modInfiniteTime.name, gameInstance.modInfiniteTime.description, false);
        constructShopItemModifier(XMaterial.QUARTZ_BLOCK.parseItem(), gameInstance.modFirstTo5Stars, gameInstance.modFirstTo5Stars.name, gameInstance.modFirstTo5Stars.description, false);
        constructShopItemModifier(XMaterial.ENDER_EYE.parseItem(), gameInstance.modDisableWildcardItems, gameInstance.modDisableWildcardItems.name, gameInstance.modDisableWildcardItems.description, false);
        constructShopItemModifier(XMaterial.PAPER.parseItem(), gameInstance.modDontUseTeamSelector, gameInstance.modDontUseTeamSelector.name, gameInstance.modDontUseTeamSelector.description, false);
        constructShopItemModifier(XMaterial.IRON_SHOVEL.parseItem(), gameInstance.modManualStar, gameInstance.modManualStar.name, gameInstance.modManualStar.description, false);
    }
    
    private void constructShopItemModifier(ItemStack notVotedItemStack, GameModifier gameModifier, String name, String description, boolean newRow)
    {
        ItemMeta itemMeta = notVotedItemStack.getItemMeta();
        itemMeta.setDisplayName(Util.RESET_CHAT.toString() + ChatColor.BOLD + name + Util.RESET_CHAT);
        
        List<String> lore = new ArrayList<>();
        lore.add(Util.RESET_CHAT + description);
        lore.add("");
        lore.add(Util.RESET_CHAT + "Click to " + ChatColor.GREEN + "vote" + Util.RESET_CHAT + "!");
        itemMeta.setLore(lore);
        
        notVotedItemStack.setItemMeta(itemMeta);
        
        ItemStack alreadyVotedItemStack = notVotedItemStack.clone();
        alreadyVotedItemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    
        ItemMeta alreadyPurchasedMeta = alreadyVotedItemStack.getItemMeta();
        alreadyPurchasedMeta.setDisplayName(ChatColor.GREEN + "[VOTED] " + Util.RESET_CHAT + ChatColor.BOLD + name);
        
        List<String> alreadyPurchasedLore = new ArrayList<>();
        alreadyPurchasedLore.add(Util.RESET_CHAT + description);
        alreadyPurchasedLore.add("");
        alreadyPurchasedLore.add(Util.RESET_CHAT + "Click to " + ChatColor.RED + "remove vote" + Util.RESET_CHAT + "!");
        alreadyPurchasedMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        alreadyPurchasedMeta.setLore(alreadyPurchasedLore);
        
        alreadyVotedItemStack.setItemMeta(alreadyPurchasedMeta);
        
        ShopItemModifier shopItem = new ShopItemModifier(notVotedItemStack, alreadyVotedItemStack, gameModifier);
        
        this.shopItemMap.put(notVotedItemStack, shopItem);
        
        ShopUtil.insertItem(this.inventory, airSlots, shopItem, newRow);
    }
    
    public void updateCounts()
    {
        Map<ItemStack, ShopItemModifier> addItems = new HashMap<>();
        
        for (ShopItemModifier shopItemModifier : new ArrayList<>(this.shopItemMap.values()))
        {
            ItemStack newItem = shopItemModifier.updateCount(this);
            addItems.put(newItem, shopItemModifier);
    
            this.shopItemMap.remove(shopItemModifier.alreadyVotedItemStack);
            this.shopItemMap.remove(shopItemModifier.shopItemStack);
        }
        
        for (Map.Entry<ItemStack, ShopItemModifier> entry : addItems.entrySet())
        {
            this.shopItemMap.put(entry.getKey(), entry.getValue());
        }
    }
}
