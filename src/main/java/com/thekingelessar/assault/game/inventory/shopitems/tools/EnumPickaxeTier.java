package com.thekingelessar.assault.game.inventory.shopitems.tools;

import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemTool;
import com.thekingelessar.assault.game.inventory.shops.ShopItemShop;
import com.thekingelessar.assault.util.xsupport.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum EnumPickaxeTier
{
    NONE(null),
    WOOD_PICKAXE(ShopItemShop.constructShopItemToolLevel(new ItemStack(XMaterial.WOODEN_PICKAXE.parseMaterial(), 1), "Wooden Pickaxe", 24, Currency.COINS, 0)),
    STONE_PICKAXE(ShopItemShop.constructShopItemToolLevel(new ItemStack(XMaterial.STONE_PICKAXE.parseMaterial(), 1), "Stone Pickaxe", 8, Currency.COINS, 1)),
    GOLD_PICKAXE(ShopItemShop.constructShopItemToolLevel(new ItemStack(XMaterial.GOLDEN_PICKAXE.parseMaterial(), 1), "Gold Pickaxe", 16, Currency.COINS, 2)),
    DIAMOND_PICKAXE(ShopItemShop.constructShopItemToolLevel(new ItemStack(XMaterial.DIAMOND_PICKAXE.parseMaterial(), 1), "Diamond Pickaxe", 1, Currency.EMERALDS, 3));
    
    public ShopItemTool shopItemTool;
    
    EnumPickaxeTier(ShopItemTool shopItemTool)
    {
        this.shopItemTool = shopItemTool;
    }
    
    public EnumPickaxeTier getLowerPickaxeTier()
    {
        EnumPickaxeTier[] pickaxeTiers = EnumPickaxeTier.values();
        for (EnumPickaxeTier pickaxeTier : pickaxeTiers)
        {
            if (pickaxeTier.shopItemTool == null)
            {
                continue;
            }
            
            if (this.shopItemTool.level - pickaxeTier.shopItemTool.level == 1)
            {
                return pickaxeTier;
            }
        }
        
        return null;
    }
    
    public EnumPickaxeTier getHigherPickaxeTier()
    {
        EnumPickaxeTier[] pickaxeTiers = EnumPickaxeTier.values();
        for (EnumPickaxeTier pickaxeTier : pickaxeTiers)
        {
            if (pickaxeTier.shopItemTool == null)
            {
                continue;
            }
            
            if (pickaxeTier.shopItemTool.level - this.shopItemTool.level == 1)
            {
                return pickaxeTier;
            }
        }
        
        return null;
    }
    
}
