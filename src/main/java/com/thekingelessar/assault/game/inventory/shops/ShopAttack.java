package com.thekingelessar.assault.game.inventory.shops;

import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.IShop;
import com.thekingelessar.assault.game.inventory.ShopItem;
import com.thekingelessar.assault.game.inventory.ShopUtil;
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

public class ShopAttack implements IShop
{
    public List<ShopItem> shopItems = new ArrayList<>();
    
    public Inventory inventory;
    
    public List<ShopItem> getShopItems()
    {
        return shopItems;
    }
    
    private List<Integer> airSlots = new ArrayList<>();
    
    public ShopAttack(TeamColor teamColor, List<ItemStack> additionalItems)
    {
        for (int i = 0; i < 10; i++)
        {
            airSlots.add(i);
        }
        
        inventory = Bukkit.createInventory(null, 54, ChatColor.GRAY + "Shop");
        
        constructShopItem(new ItemStack(Material.WOOL, 16, DyeColor.valueOf(teamColor.toString()).getData()), "Wool", 4, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.STAINED_CLAY, 16, DyeColor.valueOf(teamColor.toString()).getData()), "Clay", 16, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.STAINED_GLASS, 8, DyeColor.valueOf(teamColor.toString()).getData()), "Glass", 16, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.WOOD, 4), "Wood Planks", 6, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.COBBLESTONE, 8), "Cobblestone", 16, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.WEB, 2), "Cobwebs", 16, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.GRAVEL, 4), "Gravel", 12, Currency.COINS, false);
        
        constructShopItem(new ItemStack(Material.STONE_SWORD, 1), "Stone Sword", 16, Currency.COINS, true);
        constructShopItem(new ItemStack(Material.IRON_SWORD, 1), "Iron Sword", 40, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.BOW, 1), "Bow", 32, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.ARROW, 8), "Arrows", 32, Currency.COINS, false);
        
        constructShopItem(new ItemStack(Material.WOOD_AXE, 1), "Wooden Axe", 24, Currency.COINS, true);
        constructShopItem(new ItemStack(Material.WOOD_PICKAXE, 1), "Wooden Pickaxe", 24, Currency.COINS, false);
        constructShopItem(new ItemStack(Material.SHEARS, 1), "Shears", 28, Currency.COINS, false);
        
        constructShopItem(new ItemStack(Material.OBSIDIAN, 4), "Obsidian", 4, Currency.EMERALDS, true);
        constructShopItem(new ItemStack(Material.TNT, 1), "TNT", 4, Currency.EMERALDS, false);
        
    }
    
    private void constructShopItem(ItemStack itemStack, String name, int cost, Currency currency, boolean newRow)
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
        
        ShopUtil.insertItem(this.inventory, airSlots, shopItem, newRow);
    }
    
    private void replaceShopItem(ItemStack itemStack, String name, int cost, Currency currency, int shopItemsIndex, int inventoryIndex)
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
        
        this.shopItems.set(shopItemsIndex, shopItem);
        inventory.setItem(inventoryIndex, itemStack);
    }
    
    public void updateTools(ToolTier toolTier)
    {
        switch (toolTier)
        {
            case STONE:
                replaceShopItem(new ItemStack(Material.STONE_AXE, 1), "Stone Axe", 24, Currency.COINS, getShopItemsIndex(Material.WOOD_AXE), getInventoryIndex(Material.WOOD_AXE));
                replaceShopItem(new ItemStack(Material.STONE_PICKAXE, 1), "Stone Pickaxe", 24, Currency.COINS, getShopItemsIndex(Material.WOOD_PICKAXE), getInventoryIndex(Material.WOOD_PICKAXE));
                break;
            case IRON:
                replaceShopItem(new ItemStack(Material.IRON_AXE, 1), "Iron Axe", 24, Currency.COINS, getShopItemsIndex(Material.STONE_AXE), getInventoryIndex(Material.STONE_AXE));
                replaceShopItem(new ItemStack(Material.IRON_PICKAXE, 1), "Iron Pickaxe", 24, Currency.COINS, getShopItemsIndex(Material.STONE_PICKAXE), getInventoryIndex(Material.STONE_PICKAXE));
                break;
            case GOLD:
                replaceShopItem(new ItemStack(Material.GOLD_AXE, 1), "Gold Axe", 24, Currency.COINS, getShopItemsIndex(Material.IRON_AXE), getInventoryIndex(Material.IRON_AXE));
                replaceShopItem(new ItemStack(Material.GOLD_PICKAXE, 1), "Gold Pickaxe", 24, Currency.COINS, getShopItemsIndex(Material.IRON_PICKAXE), getInventoryIndex(Material.IRON_PICKAXE));
                break;
            case DIAMOND:
                replaceShopItem(new ItemStack(Material.DIAMOND_AXE, 1), "Diamond Axe", 24, Currency.COINS, getShopItemsIndex(Material.GOLD_AXE), getInventoryIndex(Material.GOLD_AXE));
                replaceShopItem(new ItemStack(Material.DIAMOND_PICKAXE, 1), "Diamond Pickaxe", 24, Currency.COINS, getShopItemsIndex(Material.GOLD_PICKAXE), getInventoryIndex(Material.GOLD_PICKAXE));
                break;
        }
    }
    
    private int getShopItemsIndex(Material item)
    {
        for (int i = 0; i < shopItems.size(); i++)
        {
            ShopItem shopItem = shopItems.get(i);
            if (shopItem.shopItemStack.getType().equals(item))
            {
                return i;
            }
        }
        
        return -1;
    }
    
    private int getInventoryIndex(Material item)
    {
        for (int i = 0; i < inventory.getContents().length; i++)
        {
            ItemStack inventoryItem = inventory.getItem(i);
            if (inventoryItem.getType().equals(item))
            {
                return i;
            }
        }
        
        return -1;
    }
    
    public enum ToolTier
    {
        WOOD,
        STONE,
        IRON,
        GOLD,
        DIAMOND;
    }
    
}
