package com.thekingelessar.assault.game.inventory.shops;

import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.IShop;
import com.thekingelessar.assault.game.inventory.ShopUtil;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemArmor;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemSword;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemTool;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopItemShop implements IShop
{
    public Inventory inventory;
    
    public Map<ItemStack, ShopItem> shopItemMap = new HashMap<>();
    public List<Integer> airSlots = new ArrayList<>();
    
    @Override
    public ShopItem getShopItem(ItemStack itemStack)
    {
        return this.shopItemMap.get(itemStack);
    }
    
    public ShopItemShop()
    {
    }
    
    public void constructShopItem(ItemStack boughtItemStack, String name, int cost, Currency currency, boolean newRow)
    {
        ItemStack shopItemStack = ShopUtil.constructShopItemStack(boughtItemStack.clone(), name, cost, currency);
        ShopItem shopItem = new ShopItem(cost, currency, shopItemStack, boughtItemStack);
        
        this.shopItemMap.put(shopItemStack, shopItem);
        
        ShopUtil.insertItem(this.inventory, airSlots, shopItem, newRow);
    }
    
    public void constructColoredBlockShopItem(ItemStack boughtItemStack, int amount, String name, int cost, Currency currency, boolean newRow)
    {
        ItemStack shopItemStack = ShopUtil.constructShopItemStack(boughtItemStack.clone(), name, cost, currency);
        shopItemStack.setAmount(amount);
        ShopItem shopItem = new ShopItem(cost, currency, shopItemStack, boughtItemStack);
        
        this.shopItemMap.put(shopItemStack, shopItem);
        
        ShopUtil.insertItem(this.inventory, airSlots, shopItem, newRow);
    }
    
    public void insertRandomShopItem(ShopItem shopItem, boolean newRow)
    {
        this.shopItemMap.put(shopItem.shopItemStack, shopItem);
        
        ShopUtil.insertItem(this.inventory, airSlots, shopItem, newRow);
    }
    
    public void constructShopItemSword(ItemStack boughtItemStack, String name, int cost, Currency currency, boolean newRow)
    {
        ItemStack shopItemStack = ShopUtil.constructShopItemStack(boughtItemStack.clone(), name, cost, currency);
        ShopItemSword shopItem = new ShopItemSword(cost, currency, shopItemStack, boughtItemStack);
        
        this.shopItemMap.put(shopItemStack, shopItem);
        
        ShopUtil.insertItem(this.inventory, airSlots, shopItem, newRow);
    }
    
    public ShopItemTool constructShopItemTool(ItemStack boughtItemStack, String name, int cost, Currency currency, boolean newRow, int level)
    {
        ItemStack shopItemStack = ShopUtil.constructShopItemStack(boughtItemStack.clone(), name, cost, currency);
        ShopItemTool shopItem = new ShopItemTool(cost, currency, shopItemStack, boughtItemStack, level);
        
        this.shopItemMap.put(shopItemStack, shopItem);
        ShopUtil.insertItem(this.inventory, airSlots, shopItem, newRow);
        
        return shopItem;
    }
    
    public ShopItemArmor constructShopItemArmor(ItemStack boughtChestplate, ItemStack boughtLeggings, String name, int cost, Currency currency, boolean newRow)
    {
        ItemStack shopItemStack = ShopUtil.constructShopItemStack(boughtChestplate.clone(), name, cost, currency);
        ShopItemArmor shopItem = new ShopItemArmor(cost, currency, shopItemStack, boughtChestplate, boughtLeggings);
        
        this.shopItemMap.put(shopItemStack, shopItem);
        ShopUtil.insertItem(this.inventory, airSlots, shopItem, newRow);
        
        return shopItem;
    }
    
    public static ShopItemTool constructShopItemToolLevel(ItemStack boughtItemStack, String name, int cost, Currency currency, int level)
    {
        ItemStack shopItemStack = ShopUtil.constructShopItemStack(boughtItemStack.clone(), name, cost, currency);
        return new ShopItemTool(cost, currency, shopItemStack, boughtItemStack, level);
    }
    
}