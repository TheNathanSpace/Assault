package com.thekingelessar.assault.game.inventory;

import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import org.bukkit.inventory.ItemStack;

public interface IShop
{
    public ShopItem getShopItem(ItemStack itemStack);
}
