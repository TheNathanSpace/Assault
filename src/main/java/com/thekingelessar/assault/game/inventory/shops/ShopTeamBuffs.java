package com.thekingelessar.assault.game.inventory.shops;

import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.IShop;
import com.thekingelessar.assault.game.inventory.ShopUtil;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemBuff;
import com.thekingelessar.assault.game.inventory.teambuffs.BuffHaste;
import com.thekingelessar.assault.game.inventory.teambuffs.BuffHealing;
import com.thekingelessar.assault.game.inventory.teambuffs.BuffSpeed;
import com.thekingelessar.assault.game.inventory.teambuffs.IBuff;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopTeamBuffs implements IShop
{
    public Inventory inventory;
    
    public Map<ItemStack, ShopItem> shopItemMap = new HashMap<>();
    private List<Integer> airSlots = new ArrayList<>();
    
    @Override
    public ShopItem getShopItem(ItemStack itemStack)
    {
        return this.shopItemMap.get(itemStack);
    }
    
    public ShopTeamBuffs(GameTeam gameTeam, List<ItemStack> additionalItems)
    {
        for (int i = 0; i < 10; i++)
        {
            airSlots.add(i);
        }
        
        inventory = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Team Upgrade Shop");
        
        constructShopItemBuff(new ItemStack(Material.GOLD_PICKAXE, 1), new BuffHaste(gameTeam), "Haste Upgrade", "Gives your team permanent Haste I.", 4, false);
        constructShopItemBuff(new ItemStack(Material.FEATHER, 1), new BuffSpeed(gameTeam), "Speed Upgrade", "Gives your team permanent Speed I.", 6, false);
        constructShopItemBuff(new ItemStack(Material.SPECKLED_MELON, 1), new BuffHealing(gameTeam), "Healing Upgrade", "Gives your team permanent Regeneration II.", 8, false);
        
    }
    
    private void constructShopItemBuff(ItemStack shopItemStack, IBuff buff, String name, String description, int cost, boolean newRow)
    {
        ItemMeta itemMeta = shopItemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET + name);
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET + description);
        lore.add("");
        lore.add(ChatColor.GRAY + "Cost: " + ChatColor.RESET + cost + " " + Currency.GAMER_POINTS.name);
        lore.add(ChatColor.RESET + "Click to buy!");
        itemMeta.setLore(lore);
        
        shopItemStack.setItemMeta(itemMeta);
        
        ItemStack alreadyPurchasedItemStack = shopItemStack.clone();
        ItemMeta alreadyPurchasedMeta = alreadyPurchasedItemStack.getItemMeta();
        alreadyPurchasedMeta.setDisplayName(ChatColor.GREEN + "[PURCHASED] " + ChatColor.RESET + name);
        
        List<String> alreadyPurchasedLore = new ArrayList<>();
        alreadyPurchasedLore.add(ChatColor.RESET + description);
        alreadyPurchasedLore.add("");
        alreadyPurchasedLore.add(ChatColor.GREEN + "Your team already purchased this!");
        alreadyPurchasedMeta.setLore(alreadyPurchasedLore);
        
        alreadyPurchasedItemStack.setItemMeta(alreadyPurchasedMeta);
        
        ShopItemBuff shopItem = new ShopItemBuff(cost, shopItemStack, alreadyPurchasedItemStack, buff);
        
        this.shopItemMap.put(shopItemStack, shopItem);
        
        ShopUtil.insertItem(this.inventory, airSlots, shopItem, newRow);
    }
}
