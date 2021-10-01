package com.thekingelessar.assault.game.pregame.teamselection;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.IShop;
import com.thekingelessar.assault.game.inventory.ShopUtil;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
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
            ItemStack woolItem = new ItemStack(Material.WOOL, 16, DyeColor.valueOf(selectedTeam.teamColor.toString()).getData());
            constructShopItemTeamSelector(woolItem, selectedTeam, false);
        }
    }
    
    private void constructShopItemTeamSelector(ItemStack notVotedItemStack, SelectedTeam selectedTeam, boolean newRow)
    {
        String teamName = selectedTeam.teamColor.getFormattedName(true, true, ChatColor.BOLD);
        teamName += ChatColor.RESET + ChatColor.BOLD.toString() + " Team" + ChatColor.RESET;
        
        ItemMeta itemMeta = notVotedItemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET + teamName + ChatColor.RESET);
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET + "Click to join " + teamName + "!");
        itemMeta.setLore(lore);
        
        notVotedItemStack.setItemMeta(itemMeta);
        
        ItemStack alreadySelectedItemStack = notVotedItemStack.clone();
        alreadySelectedItemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        
        ItemMeta alreadySelectedMeta = alreadySelectedItemStack.getItemMeta();
        alreadySelectedMeta.setDisplayName(ChatColor.GREEN + "[JOINED] " + ChatColor.RESET + teamName);
        
        List<String> alreadyPurchasedLore = new ArrayList<>();
        alreadyPurchasedLore.add(ChatColor.RESET + "Click to " + ChatColor.RED + "leave team" + ChatColor.RESET + "!");
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
