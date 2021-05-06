package com.thekingelessar.assault.game.inventory;

import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopUtil
{
    
    public static int insertItem(Inventory inventory, List<Integer> airSlots, ShopItem shopItem, boolean newRow)
    {
        int lastItem = -1;
        int lastEmpty = -1;
        
        int insertedSlot = -1;
        
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++)
        {
            ItemStack inventoryItem = contents[i];
            
            if (inventoryItem == null || inventoryItem.getType().equals(Material.AIR))
            {
                lastEmpty = i;
            }
            else
            {
                lastItem = i;
            }
        }
        
        if (newRow)
        {
            int startingSlot = -1;
            for (int i = 1; i < 11; i++)
            {
                if ((lastItem + i) % 9 == 0)
                {
                    startingSlot = lastItem + i + 1;
                    break;
                }
            }
            
            insertedSlot = startingSlot;
            shopItem.insertItem(inventory, insertedSlot);
        }
        
        if (!newRow)
        {
            while (airSlots.contains(lastItem + 1))
            {
                lastItem++;
            }
            
            insertedSlot = lastItem + 1;
            shopItem.insertItem(inventory, insertedSlot);
            
        }
        
        return insertedSlot;
    }
    
    public static ItemStack constructShopItemStack(ItemStack itemStack, String name, int cost, Currency currency)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET + name);
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Cost: " + ChatColor.RESET + cost + " " + currency.name);
        lore.add("");
        lore.add(ChatColor.RESET + "Click to buy!");
        itemMeta.setLore(lore);
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
}
