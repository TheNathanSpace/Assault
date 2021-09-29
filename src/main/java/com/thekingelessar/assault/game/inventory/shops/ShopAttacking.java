package com.thekingelessar.assault.game.inventory.shops;

import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.IShop;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemTool;
import com.thekingelessar.assault.game.inventory.shopitems.tools.EnumAxeTier;
import com.thekingelessar.assault.game.inventory.shopitems.tools.EnumPickaxeTier;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Arrays;

public class ShopAttacking extends ShopItemShop implements IShop
{
    public static final PotionEffect JUMP_30_5 = new PotionEffect(PotionEffectType.JUMP, 20 * 30, 4, true, true);
    public static final PotionEffect INVIS_30_1 = new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 30, 0, true, true);
    public static final PotionEffect POISON_6_2 = new PotionEffect(PotionEffectType.POISON, 20 * 6 + 12, 1, true, true);
    
    GamePlayer gamePlayer;
    
    public ItemStack storageItem;
    
    public EnumAxeTier axeTier;
    public EnumPickaxeTier pickaxeTier;
    
    public int axeSlot;
    public int pickaxeSlot;
    
    public ShopAttacking(TeamColor teamColor, GamePlayer gamePlayer)
    {
        this.gamePlayer = gamePlayer;
        
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
        constructShopItemArmor(new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS), "Iron Armor", 2, Currency.EMERALDS, false);
        
        ShopItemTool woodAxeItem = constructShopItemTool(new ItemStack(Material.WOOD_AXE, 1), "Wooden Axe", 24, Currency.COINS, true, 0);
        axeSlot = woodAxeItem.slot;
        axeTier = EnumAxeTier.NONE;
        
        ShopItemTool woodPickItem = constructShopItemTool(new ItemStack(Material.WOOD_PICKAXE, 1), "Wooden Pickaxe", 24, Currency.COINS, false, 0);
        pickaxeSlot = woodPickItem.slot;
        pickaxeTier = EnumPickaxeTier.NONE;
        
        constructShopItemTool(new ItemStack(Material.SHEARS, 1), "Shears", 28, Currency.COINS, false, 0);
        
        constructShopItem(new ItemStack(Material.OBSIDIAN, 4), "Obsidian", 4, Currency.EMERALDS, true);
        constructShopItem(new ItemStack(Material.TNT, 1), "TNT", 4, Currency.EMERALDS, false);
        constructShopItem(new Potion(PotionType.JUMP).toItemStack(1), "Jump Boost Potion (30 sec)", 1, Currency.EMERALDS, false);
        constructShopItem(new Potion(PotionType.INVISIBILITY).toItemStack(1), "Invisibility Potion (30 sec)", 2, Currency.EMERALDS, false);
        constructShopItem(new Potion(PotionType.POISON).splash().toItemStack(1), "Splash Potion of Poison (4 hearts)", 1, Currency.EMERALDS, false);
        
        boolean randomNewRow = true;
        for (ShopItem shopItem : this.gamePlayer.gameInstance.randomShopItems)
        {
            insertRandomShopItem(shopItem, randomNewRow);
            randomNewRow = false;
        }
        
        storageItem = new ItemStack(Material.CHEST);
        ItemMeta chestMeta = storageItem.getItemMeta();
        chestMeta.setDisplayName(ChatColor.RESET + "Secret Storage");
        chestMeta.setLore(Arrays.asList(ChatColor.RESET + "Click to open the secret storage.", ChatColor.RESET + "The secret storage can be used", ChatColor.RESET + "by your" + teamColor.chatColor + " entire team§r."));
        storageItem.setItemMeta(chestMeta);
        
        inventory.setItem(53, storageItem);
    }
    
    public boolean upgradePickaxe()
    {
        if (this.pickaxeTier == null || this.pickaxeTier.equals(EnumPickaxeTier.NONE))
        {
            return false;
        }
        
        if (this.pickaxeTier.shopItemTool.level == 3)
        {
            return false;
        }
        
        if (this.pickaxeTier.getHigherPickaxeTier() == null)
        {
            return false;
        }
        
        this.pickaxeTier = this.pickaxeTier.getHigherPickaxeTier();
        
        if (this.pickaxeTier.getHigherPickaxeTier() != null)
        {
            ShopItemTool.updateItemPurchase(this, this.pickaxeTier.getHigherPickaxeTier().shopItemTool);
        }
        
        gamePlayer.spawnItems.removeIf(spawnItem -> ShopItemTool.pickaxes.contains(spawnItem.getType()));
        gamePlayer.addSpawnItem(this.pickaxeTier.shopItemTool.boughtItemStack);
        
        return true;
    }
    
    public boolean upgradeAxe()
    {
        if (this.axeTier == null || this.axeTier.equals(EnumAxeTier.NONE))
        {
            return false;
        }
        
        if (this.axeTier.shopItemTool.level == 3)
        {
            return false;
        }
        
        if (this.axeTier.getHigherAxeTier() == null)
        {
            return false;
        }
        
        this.axeTier = this.axeTier.getHigherAxeTier();
        
        if (this.axeTier.getHigherAxeTier() != null)
        {
            ShopItemTool.updateItemPurchase(this, this.axeTier.getHigherAxeTier().shopItemTool);
        }
        
        gamePlayer.spawnItems.removeIf(spawnItem -> ShopItemTool.axes.contains(spawnItem.getType()));
        gamePlayer.addSpawnItem(this.axeTier.shopItemTool.boughtItemStack);
        
        return true;
    }
    
    public boolean downgradePickaxe()
    {
        if (this.pickaxeTier == null || this.pickaxeTier.equals(EnumPickaxeTier.NONE))
        {
            return false;
        }
        
        if (this.pickaxeTier.shopItemTool.level == 0)
        {
            return false;
        }
        
        if (this.pickaxeTier.getLowerPickaxeTier() == null)
        {
            return false;
        }
        
        this.pickaxeTier = this.pickaxeTier.getLowerPickaxeTier();
        ShopItemTool.updateItemPurchase(this, this.pickaxeTier.getHigherPickaxeTier().shopItemTool);
        
        gamePlayer.spawnItems.removeIf(spawnItem -> ShopItemTool.pickaxes.contains(spawnItem.getType()));
        gamePlayer.addSpawnItem(this.pickaxeTier.shopItemTool.boughtItemStack);
        
        return true;
    }
    
    public boolean downgradeAxe()
    {
        if (this.axeTier == null || this.axeTier.equals(EnumAxeTier.NONE))
        {
            return false;
        }
        
        if (this.axeTier.shopItemTool.level == 0)
        {
            return false;
        }
        
        if (this.axeTier.getLowerAxeTier() == null)
        {
            return false;
        }
        
        this.axeTier = this.axeTier.getLowerAxeTier();
        ShopItemTool.updateItemPurchase(this, this.axeTier.getHigherAxeTier().shopItemTool);
        
        gamePlayer.spawnItems.removeIf(spawnItem -> ShopItemTool.axes.contains(spawnItem.getType()));
        gamePlayer.addSpawnItem(this.axeTier.shopItemTool.boughtItemStack);
        
        return true;
    }
    
}
