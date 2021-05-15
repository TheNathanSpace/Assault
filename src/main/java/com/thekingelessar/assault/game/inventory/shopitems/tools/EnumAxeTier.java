package com.thekingelessar.assault.game.inventory.shopitems.tools;

import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemTool;
import com.thekingelessar.assault.game.inventory.shops.ShopItemShop;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum EnumAxeTier
{
    NONE(null),
    WOOD_AXE(ShopItemShop.constructShopItemToolLevel(new ItemStack(Material.WOOD_AXE, 1), "Wooden Axe", 24, Currency.COINS, 0)),
    STONE_AXE(ShopItemShop.constructShopItemToolLevel(new ItemStack(Material.STONE_AXE, 1), "Stone Axe", 8, Currency.COINS, 1)),
    IRON_AXE(ShopItemShop.constructShopItemToolLevel(new ItemStack(Material.IRON_AXE, 1), "Iron Axe", 12, Currency.COINS, 2)),
    GOLD_AXE(ShopItemShop.constructShopItemToolLevel(new ItemStack(Material.GOLD_AXE, 1), "Gold Axe", 36, Currency.COINS, 3));
    
    public ShopItemTool shopItemTool;
    
    EnumAxeTier(ShopItemTool shopItemTool)
    {
        this.shopItemTool = shopItemTool;
    }
    
    public EnumAxeTier getLowerAxeTier()
    {
        EnumAxeTier[] pickaxeTiers = EnumAxeTier.values();
        for (EnumAxeTier axeTier : pickaxeTiers)
        {
            if (axeTier.shopItemTool == null)
            {
                continue;
            }
            
            if (this.shopItemTool.level - axeTier.shopItemTool.level == 1)
            {
                return axeTier;
            }
        }
        
        return null;
    }
    
    public EnumAxeTier getHigherAxeTier()
    {
        EnumAxeTier[] pickaxeTiers = EnumAxeTier.values();
        for (EnumAxeTier axeTier : pickaxeTiers)
        {
            if (axeTier.shopItemTool == null)
            {
                continue;
            }
    
            if (axeTier.shopItemTool.level - this.shopItemTool.level == 1)
            {
                return axeTier;
            }
        }
        
        return null;
    }
    
}
