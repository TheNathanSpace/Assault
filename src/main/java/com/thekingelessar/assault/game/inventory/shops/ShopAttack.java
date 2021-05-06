package com.thekingelessar.assault.game.inventory.shops;

import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.IShop;
import com.thekingelessar.assault.game.team.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopAttack extends ShopItemShop implements IShop
{
    
    public ShopAttack(TeamColor teamColor)
    {
        for (int i = 0; i < 10; i++)
        {
            airSlots.add(i);
        }
        
        inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Shop");
        
        constructShopItem(new ItemStack(Material.WOOL, 16, DyeColor.valueOf(teamColor.toString()).getData()), "Wool", 4, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.STAINED_CLAY, 16, DyeColor.valueOf(teamColor.toString()).getData()), "Clay", 16, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.STAINED_GLASS, 8, DyeColor.valueOf(teamColor.toString()).getData()), "Glass", 16, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.WOOD, 4), "Wood Planks", 6, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.COBBLESTONE, 8), "Cobblestone", 16, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.WEB, 2), "Cobwebs", 16, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.GRAVEL, 4), "Gravel", 12, Currency.COINS, false);
        
        constructShopItemSword(new ItemStack(Material.STONE_SWORD, 1), "Stone Sword", 16, Currency.COINS, true);
        constructShopItemSword(new ItemStack(Material.IRON_SWORD, 1), "Iron Sword", 40, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.BOW, 1), "Bow", 32, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.ARROW, 8), "Arrows", 32, Currency.COINS, false);
        
        constructShopItemTool(new ItemStack(Material.WOOD_AXE, 1), "Wooden Axe", 24, Currency.COINS, true);
        constructShopItemTool(new ItemStack(Material.WOOD_PICKAXE, 1), "Wooden Pickaxe", 24, Currency.COINS, false);
        constructShopItemTool(new ItemStack(Material.SHEARS, 1), "Shears", 28, Currency.COINS, false);
        
        constructShopItem(new ItemStack(Material.OBSIDIAN, 4), "Obsidian", 4, Currency.EMERALDS, true);
        constructShopItem(new ItemStack(Material.TNT, 1), "TNT", 4, Currency.EMERALDS, false);
        
    }
    
}
