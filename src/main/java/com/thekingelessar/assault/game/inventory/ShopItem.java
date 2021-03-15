package com.thekingelessar.assault.game.inventory;

import org.bukkit.inventory.ItemStack;

public class ShopItem
{
    public int cost;
    public Currency currency;
    
    public ItemStack shopItemStack;
    public ItemStack realItemStack;
    
    public ShopItem(int cost, Currency currency, ItemStack shopItemStack)
    {
        this.cost = cost;
        this.currency = currency;
        this.shopItemStack = shopItemStack;
        
        this.realItemStack = new ItemStack(shopItemStack.getType(), shopItemStack.getAmount());
    }
    
    public static ShopItem getShopItem(IShop shop, ItemStack itemStack)
    {
        for (ShopItem shopItem : shop.getShopItems())
        {
            if (shopItem.shopItemStack.equals(itemStack)) return shopItem;
        }
        
        return null;
    }
}
