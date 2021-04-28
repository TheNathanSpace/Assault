package com.thekingelessar.assault.game.inventory;

import com.thekingelessar.assault.game.inventory.shops.ShopTeamBuffs;
import com.thekingelessar.assault.game.inventory.teambuffs.IBuff;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TeamBuffItem
{
    public int cost;
    public Currency currency = Currency.GAMER_POINTS;
    
    public boolean purchased = false;
    
    public ItemStack shopItemStack;
    public IBuff buff;
    
    public TeamBuffItem(int cost, ItemStack shopItemStack, IBuff buff)
    {
        this.cost = cost;
        this.shopItemStack = shopItemStack;
        this.buff = buff;
    }
    
    public void purchase(Inventory inventory, int slot)
    {
        this.purchased = true;
        this.shopItemStack = new ItemStack(Material.BARRIER);
        inventory.setItem(slot, this.shopItemStack);
    }
    
    public static TeamBuffItem getShopItem(ShopTeamBuffs shop, ItemStack itemStack)
    {
        for (TeamBuffItem shopItem : shop.getShopItems())
        {
            if (shopItem.shopItemStack.equals(itemStack)) return shopItem;
        }
        
        return null;
    }
}
