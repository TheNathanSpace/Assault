package com.thekingelessar.assault.game.inventory.shopitems.tools;

import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemTool;
import com.thekingelessar.assault.game.inventory.shops.ShopItemShop;
import com.thekingelessar.assault.util.version.XMaterial;
import org.bukkit.inventory.ItemStack;

public enum EnumAxeTier
{
    NONE(null),
    WOOD_AXE(ShopItemShop.constructShopItemToolLevel(new ItemStack(XMaterial.WOODEN_AXE.parseMaterial(), 1), "Wooden Axe", 24, Currency.COINS, 0)),
    STONE_AXE(ShopItemShop.constructShopItemToolLevel(new ItemStack(XMaterial.STONE_AXE.parseMaterial(), 1), "Stone Axe", 8, Currency.COINS, 1)),
    IRON_AXE(ShopItemShop.constructShopItemToolLevel(new ItemStack(XMaterial.IRON_AXE.parseMaterial(), 1), "Iron Axe", 12, Currency.COINS, 2)),
    GOLD_AXE(ShopItemShop.constructShopItemToolLevel(new ItemStack(XMaterial.GOLDEN_AXE.parseMaterial(), 1), "Gold Axe", 36, Currency.COINS, 3));
    
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
