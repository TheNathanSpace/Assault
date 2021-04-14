package com.thekingelessar.assault.game.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopUtil
{
    public static void insertItem(Inventory inventory, List<Integer> airSlots, ShopItem shopItem, boolean newRow)
    {
        int lastItem = -1;
        int lastEmpty = -1;
        
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
            
            inventory.setItem(startingSlot, shopItem.shopItemStack);
        }
        
        if (!newRow)
        {
            while (airSlots.contains(lastItem + 1))
            {
                lastItem++;
            }
            
            inventory.setItem(lastItem + 1, shopItem.shopItemStack);
            
        }
    }
}
