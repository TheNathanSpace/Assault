package com.thekingelessar.assault.game.inventory.shopitems;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.shopitems.tools.EnumAxeTier;
import com.thekingelessar.assault.game.inventory.shopitems.tools.EnumPickaxeTier;
import com.thekingelessar.assault.game.inventory.shops.ShopAttack;
import com.thekingelessar.assault.game.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ShopItemTool extends ShopItem
{
    public static List<Material> axes = Arrays.asList(Material.WOOD_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLD_AXE);
    public static List<Material> pickaxes = Arrays.asList(Material.WOOD_PICKAXE, Material.STONE_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE);
    
    public int level;
    
    public ShopItemTool(int cost, Currency currency, ItemStack shopItemStack, ItemStack boughtItemStack, int level)
    {
        super(cost, currency, shopItemStack, boughtItemStack);
        this.level = level;
    }
    
    public ShopItemTool(int cost, Currency currency, ItemStack shopItemStack, ItemStack boughtItemStack, int level, int slot)
    {
        super(cost, currency, shopItemStack, boughtItemStack);
        this.level = level;
        this.slot = slot;
    }
    
    @Override
    public void buyItem(Player player)
    {
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        if (gameInstance == null)
        {
            return;
        }
        
        GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
        
        for (ItemStack itemStack : player.getInventory().getContents())
        {
            if (itemStack != null)
            {
                if (this.boughtItemStack.getType().equals(itemStack.getType()))
                {
                    player.playSound(player.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
                    return;
                }
            }
        }
        
        boolean success;
        if (!gamePlayer.shopAttacking.axeTier.equals(EnumAxeTier.NONE) && this.boughtItemStack.getType().equals(gamePlayer.shopAttacking.axeTier.shopItemTool.boughtItemStack.getType()))
        {
            success = false;
        }
        else if (!gamePlayer.shopAttacking.pickaxeTier.equals(EnumPickaxeTier.NONE) && this.boughtItemStack.getType().equals(gamePlayer.shopAttacking.pickaxeTier.shopItemTool.boughtItemStack.getType()))
        {
            success = false;
        }
        else
        {
            success = gamePlayer.purchaseItem(this.cost, this.currency);
        }
        
        super.playSound(player, success);
        if (!success)
        {
            return;
        }
        
        boolean add = true;
        
        if (this.boughtItemStack.getType().equals(Material.WOOD_PICKAXE))
        {
            gamePlayer.shopAttacking.pickaxeTier = EnumPickaxeTier.WOOD_PICKAXE;
            ShopItemTool.updateItemPurchase(gamePlayer.shopAttacking, gamePlayer.shopAttacking.pickaxeTier.getHigherPickaxeTier().shopItemTool);
        }
        else if (this.boughtItemStack.getType().equals(Material.WOOD_AXE))
        {
            gamePlayer.shopAttacking.axeTier = EnumAxeTier.WOOD_AXE;
            ShopItemTool.updateItemPurchase(gamePlayer.shopAttacking, gamePlayer.shopAttacking.axeTier.getHigherAxeTier().shopItemTool);
        }
        else if (axes.contains(this.boughtItemStack.getType()) && !this.boughtItemStack.getType().equals(Material.WOOD_AXE))
        {
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length; i++)
            {
                ItemStack itemStack = contents[i];
                if (itemStack == null)
                {
                    continue;
                }
                if (axes.contains(itemStack.getType()))
                {
                    add = false;
                    ItemStack giveItemStack = this.boughtItemStack.clone();
                    ItemMeta itemMeta = giveItemStack.getItemMeta();
                    itemMeta.spigot().setUnbreakable(true);
                    giveItemStack.setItemMeta(itemMeta);
                    player.getInventory().setItem(i, giveItemStack);
                    
                    gamePlayer.spawnItems.removeIf(spawnItem -> axes.contains(spawnItem.getType()));
                    
                    gamePlayer.addSpawnItem(giveItemStack.clone());
                }
            }
            gamePlayer.shopAttacking.upgradeAxe();
        }
        else if (pickaxes.contains(this.boughtItemStack.getType()))
        {
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length; i++)
            {
                ItemStack itemStack = contents[i];
                if (itemStack == null)
                {
                    continue;
                }
                if (pickaxes.contains(itemStack.getType()))
                {
                    add = false;
                    ItemStack giveItemStack = this.boughtItemStack.clone();
                    ItemMeta itemMeta = giveItemStack.getItemMeta();
                    itemMeta.spigot().setUnbreakable(true);
                    giveItemStack.setItemMeta(itemMeta);
                    player.getInventory().setItem(i, giveItemStack);
                    
                    gamePlayer.spawnItems.removeIf(spawnItem -> pickaxes.contains(spawnItem.getType()));
                    
                    gamePlayer.addSpawnItem(giveItemStack.clone());
                }
            }
            gamePlayer.shopAttacking.upgradePickaxe();
        }
        
        if (success && add)
        {
            ItemStack giveItemStack = this.boughtItemStack.clone();
            ItemMeta itemMeta = giveItemStack.getItemMeta();
            itemMeta.spigot().setUnbreakable(true);
            giveItemStack.setItemMeta(itemMeta);
            player.getInventory().addItem(giveItemStack);
            gamePlayer.addSpawnItem(giveItemStack.clone());
        }
    }
    
    public static void updateItemPurchase(ShopAttack shopAttack, ShopItemTool shopItemTool)
    {
        if (axes.contains(shopItemTool.boughtItemStack.getType()))
        {
            shopItemTool.slot = shopAttack.axeSlot;
        }
        else if (pickaxes.contains(shopItemTool.boughtItemStack.getType()))
        {
            shopItemTool.slot = shopAttack.pickaxeSlot;
        }
        
        shopAttack.shopItemMap.put(shopItemTool.shopItemStack, shopItemTool);
        shopAttack.inventory.setItem(shopItemTool.slot, shopItemTool.shopItemStack);
    }
}
