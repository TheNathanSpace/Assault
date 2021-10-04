package com.thekingelessar.assault.game.inventory.shopitems;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.IShop;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.util.xsupport.XSound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopItem
{
    public int cost;
    public Currency currency;
    
    public ItemStack shopItemStack;
    public ItemStack boughtItemStack;
    
    public int slot;
    
    public ShopItem(int cost, Currency currency, ItemStack shopItemStack, ItemStack boughtItemStack)
    {
        this.cost = cost;
        this.currency = currency;
        this.shopItemStack = shopItemStack;
        
        this.boughtItemStack = boughtItemStack;
    }
    
    public void buyItem(Player player)
    {
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        if (gameInstance == null)
        {
            return;
        }
        
        GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
        
        boolean success = gamePlayer.purchaseItem(this.cost, this.currency);
        if (success)
        {
            player.getInventory().addItem(this.boughtItemStack.clone());
        }
        
        this.playSound(player, success);
    }
    
    public void playSound(Player player, boolean success)
    {
        if (success)
        {
            player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1.0F, 2.0F);
        }
        else
        {
            player.playSound(player.getLocation(), XSound.ENTITY_SKELETON_HURT.parseSound(), 1.0F, 1.0F);
        }
    }
    
    public void insertItem(Inventory inventory, int slot)
    {
        this.slot = slot;
        inventory.setItem(slot, this.shopItemStack);
    }
    
    public static ShopItem getShopItem(IShop shop, ItemStack itemStack)
    {
        return shop.getShopItem(itemStack);
    }
}
