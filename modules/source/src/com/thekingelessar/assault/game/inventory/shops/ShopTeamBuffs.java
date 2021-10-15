package com.thekingelessar.assault.game.inventory.shops;

import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.IShop;
import com.thekingelessar.assault.game.inventory.ShopUtil;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemBuff;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.teambuffs.*;
import com.thekingelessar.assault.util.Util;
import com.thekingelessar.assault.util.version.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        
        constructShopItemBuff(new ItemStack(XMaterial.GOLDEN_PICKAXE.parseMaterial(), 1), new BuffHaste(gameTeam), "Haste Upgrade", "Gives your team permanent Haste II.", 1, false);
        constructShopItemBuff(new ItemStack(XMaterial.FEATHER.parseMaterial(), 1), new BuffSpeed(gameTeam), "Speed Upgrade", "Gives your team permanent Speed II.", 3, false);
        constructShopItemBuff(new ItemStack(XMaterial.GLISTERING_MELON_SLICE.parseMaterial(), 1), new BuffHealing(gameTeam), "Healing Upgrade", "Gives your team permanent Regeneration II.", 2, false);
        constructShopItemBuff(new ItemStack(XMaterial.GOLDEN_CHESTPLATE.parseMaterial(), 1), new BuffProtection(gameTeam), "Protection Upgrade", "Gives your team permanent Protection I.", 2, false);
        constructShopItemBuff(new ItemStack(XMaterial.GOLDEN_SWORD.parseMaterial(), 1), new BuffSharpness(gameTeam), "Sharpness Upgrade", "Gives your team permanent Sharpness I.", 3, false);
        constructShopItemBuff(new ItemStack(XMaterial.RABBIT_FOOT.parseMaterial(), 1), new BuffDoubleJump(gameTeam), "Double Jump Upgrade", "Gives your team permanent double jumping.", 2, false);
    }
    
    private void constructShopItemBuff(ItemStack shopItemStack, IBuff buff, String name, String description, int cost, boolean newRow)
    {
        ItemMeta itemMeta = shopItemStack.getItemMeta();
        itemMeta.setDisplayName(Util.RESET_CHAT + name);
        
        List<String> lore = new ArrayList<>();
        lore.add(Util.RESET_CHAT + description);
        lore.add("");
        lore.add(ChatColor.GRAY + "Cost: " + Util.RESET_CHAT + cost + " " + Currency.GAMER_POINTS.name);
        lore.add(Util.RESET_CHAT + "Click to buy!");
        itemMeta.setLore(lore);
        
        shopItemStack.setItemMeta(itemMeta);
        
        ItemStack alreadyPurchasedItemStack = shopItemStack.clone();
        ItemMeta alreadyPurchasedMeta = alreadyPurchasedItemStack.getItemMeta();
        alreadyPurchasedMeta.setDisplayName(ChatColor.GREEN + "[PURCHASED] " + Util.RESET_CHAT + name);
        
        List<String> alreadyPurchasedLore = new ArrayList<>();
        alreadyPurchasedLore.add(Util.RESET_CHAT + description);
        alreadyPurchasedLore.add("");
        alreadyPurchasedLore.add(ChatColor.GREEN + "Your team already purchased this!");
        alreadyPurchasedMeta.setLore(alreadyPurchasedLore);
        
        alreadyPurchasedItemStack.setItemMeta(alreadyPurchasedMeta);
        
        ShopItemBuff shopItem = new ShopItemBuff(cost, shopItemStack, alreadyPurchasedItemStack, buff);
        
        this.shopItemMap.put(shopItemStack, shopItem);
        
        ShopUtil.insertItem(this.inventory, airSlots, shopItem, newRow);
    }
}
