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

public class ShopBuilding implements IShop
{
    public List<ShopItem> shopItems = new ArrayList<>();
    
    public Inventory inventory;
    
    public List<ShopItem> getShopItems()
    {
        return shopItems;
    }
    
    public ShopBuilding(TeamColor teamColor, List<ItemStack> additionalItems)
    {
        inventory = Bukkit.createInventory(null, 27, ChatColor.GRAY + "Shop");
        
        for (int i = 0; i < 10; i++)
        {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }
        
        constructShopItem(new ItemStack(Material.WOOL, 16, DyeColor.valueOf(teamColor.toString()).getData()), "Wool", 4, Currency.COINS);
        constructShopItem(new ItemStack(Material.STAINED_CLAY, 16, DyeColor.valueOf(teamColor.toString()).getData()), "Clay", 16, Currency.COINS);
        constructShopItem(new ItemStack(Material.STAINED_GLASS, 8, DyeColor.valueOf(teamColor.toString()).getData()), "Glass", 16, Currency.COINS);
        constructShopItem(new ItemStack(Material.WOOD, 4), "Wood Planks", 6, Currency.COINS);
        constructShopItem(new ItemStack(Material.COBBLESTONE, 8), "Cobblestone", 16, Currency.COINS);
        constructShopItem(new ItemStack(Material.WEB, 2), "Cobwebs", 16, Currency.COINS);
        constructShopItem(new ItemStack(Material.GRAVEL, 4), "Gravel", 12, Currency.COINS);
        
        //        constructShopItem(new ItemStack(Material.OBSIDIAN, 1), "Obsidian", 88, Currency.COINS);
        //        constructShopItem(new ItemStack(Material.LADDER, 8), "Ladder", 8, Currency.COINS);
        
        
        for (int i = 0; i < shopItems.size(); i++)
        {
            inventory.setItem(i + 10, shopItems.get(i).shopItemStack);
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
        
        ShopItem shopItem = new ShopItem(cost, currency, itemStack);
        
        this.shopItems.add(shopItem);
    }
    
}
