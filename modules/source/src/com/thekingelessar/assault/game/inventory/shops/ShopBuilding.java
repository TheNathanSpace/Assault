package com.thekingelessar.assault.game.inventory.shops;

import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.IShop;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.util.version.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopBuilding extends ShopItemShop implements IShop
{
    
    public ShopBuilding(TeamColor teamColor, List<ItemStack> additionalItems)
    {
        for (int i = 0; i < 10; i++)
        {
            airSlots.add(i);
        }
        
        inventory = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Shop");
    
        constructColoredBlockShopItem(teamColor.getWool(), 16, "Wool", 4, Currency.COINS, false);
        constructColoredBlockShopItem(teamColor.getTerracotta(), 12, "Clay", 16, Currency.COINS, false);
        constructColoredBlockShopItem(teamColor.getStainedGlass(), 24, "Glass", 16, Currency.COINS, false);
        constructColoredBlockShopItem(teamColor.getPlank(), 4, "Wood Planks", 6, Currency.COINS, false);
        constructShopItem(new ItemStack(XMaterial.COBBLESTONE.parseMaterial(), 8), "Cobblestone", 16, Currency.COINS, false);
        constructShopItem(new ItemStack(XMaterial.COBWEB.parseMaterial(), 2), "Cobwebs", 16, Currency.COINS, false);
        constructShopItem(new ItemStack(XMaterial.GRAVEL.parseMaterial(), 4), "Gravel", 12, Currency.COINS, false);
    }
}
