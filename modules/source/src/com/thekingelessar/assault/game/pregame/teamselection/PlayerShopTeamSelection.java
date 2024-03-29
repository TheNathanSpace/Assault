package com.thekingelessar.assault.game.pregame.teamselection;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.IShop;
import com.thekingelessar.assault.game.inventory.ShopUtil;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import com.thekingelessar.assault.util.Util;
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

public class PlayerShopTeamSelection implements IShop
{
    public GameInstance gameInstance;
    public Player player;
    public Inventory inventory;
    
    public Map<ItemStack, ShopItemTeam> shopItemMap = new HashMap<>();
    private List<Integer> airSlots = new ArrayList<>();
    
    @Override
    public ShopItem getShopItem(ItemStack itemStack)
    {
        return this.shopItemMap.get(itemStack);
    }
    
    public PlayerShopTeamSelection(GameInstance gameInstance, Player player)
    {
        this.player = player;
        
        for (int i = 0; i < 10; i++)
        {
            airSlots.add(i);
        }
        
        inventory = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Team Selection");
        
        for (SelectedTeam selectedTeam : gameInstance.selectedTeamList)
        {
            ItemStack woolItem = selectedTeam.teamColor.getWool();
            constructShopItemTeamSelector(woolItem, selectedTeam, false);
        }
    }
    
    private void constructShopItemTeamSelector(ItemStack notVotedItemStack, SelectedTeam selectedTeam, boolean newRow)
    {
        String teamName = selectedTeam.teamColor.getFormattedName(true, true, ChatColor.BOLD);
        teamName += Util.RESET_CHAT + ChatColor.BOLD.toString() + " Team" + Util.RESET_CHAT;
        
        ItemMeta itemMeta = notVotedItemStack.getItemMeta();
        itemMeta.setDisplayName(Util.RESET_CHAT + teamName + Util.RESET_CHAT);
        
        List<String> lore = new ArrayList<>();
        lore.add(Util.RESET_CHAT + "Click to join " + teamName + "!");
        itemMeta.setLore(lore);
        
        notVotedItemStack.setItemMeta(itemMeta);
        
        ItemStack alreadySelectedItemStack = notVotedItemStack.clone();
        alreadySelectedItemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        
        ItemMeta alreadySelectedMeta = alreadySelectedItemStack.getItemMeta();
        alreadySelectedMeta.setDisplayName(ChatColor.GREEN + "[JOINED] " + Util.RESET_CHAT + teamName);
        
        List<String> alreadyPurchasedLore = new ArrayList<>();
        alreadyPurchasedLore.add(Util.RESET_CHAT + "Click to " + ChatColor.RED + "leave team" + Util.RESET_CHAT + "!");
        alreadySelectedMeta.setLore(alreadyPurchasedLore);
        alreadySelectedMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        
        alreadySelectedItemStack.setItemMeta(alreadySelectedMeta);
        
        ShopItemTeam shopItem = new ShopItemTeam(notVotedItemStack, alreadySelectedItemStack, selectedTeam);
        
        this.shopItemMap.put(notVotedItemStack, shopItem);
        
        ShopUtil.insertItem(this.inventory, airSlots, shopItem, newRow);
    }
    
    public void updateCounts()
    {
        Map<ItemStack, ShopItemTeam> addItems = new HashMap<>();
        List<ItemStack> removeItems = new ArrayList<>();
        
        for (ShopItemTeam shopItemModifier : this.shopItemMap.values())
        {
            removeItems.add(shopItemModifier.chosenItemStack);
            removeItems.add(shopItemModifier.shopItemStack);
            
            ItemStack newItem = shopItemModifier.updateCount(this);
            addItems.put(newItem, shopItemModifier);
        }
        
        for (ItemStack itemStack : removeItems)
        {
            this.shopItemMap.remove(itemStack);
        }
        
        for (Map.Entry<ItemStack, ShopItemTeam> entry : addItems.entrySet())
        {
            this.shopItemMap.put(entry.getKey(), entry.getValue());
        }
    }
}
