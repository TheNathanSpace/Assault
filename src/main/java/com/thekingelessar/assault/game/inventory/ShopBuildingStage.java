package com.thekingelessar.assault.game.inventory;

import com.thekingelessar.assault.game.team.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopBuildingStage
{
    public List<ItemStack> shopItems = new ArrayList<>();
    
    public Inventory inventory;
    
    public ShopBuildingStage(TeamColor teamColor, List<ItemStack> additionalItems)
    {
        inventory = Bukkit.createInventory(null, 27, ChatColor.GRAY + "Shop");
        
        constructShopItem(new ItemStack(Material.WOOL, 16, DyeColor.valueOf(teamColor.toString()).getData()), "Wool", 4, Currency.COINS);
        
        for (int i = 0; i < shopItems.size(); i++)
        {
            inventory.setItem(i, shopItems.get(i));
        }
    }
    
    private void constructShopItem(ItemStack itemStack, String name, int cost, Currency currency)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET + name);
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Cost: " + ChatColor.RESET + cost + " " + currency.name);
        lore.add("");
        lore.add(ChatColor.RESET + "Click to buy!");
        itemMeta.setLore(lore);
        
        itemStack.setItemMeta(itemMeta);
        
        this.shopItems.add(itemStack);
    }
    
}
