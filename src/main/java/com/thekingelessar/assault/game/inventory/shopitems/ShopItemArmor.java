package com.thekingelessar.assault.game.inventory.shopitems;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ShopItemArmor extends ShopItem
{
    public final static List<Material> HELMETS = Arrays.asList(Material.CHAINMAIL_HELMET, Material.GOLD_HELMET, Material.DIAMOND_HELMET, Material.IRON_HELMET, Material.LEATHER_HELMET);
    public final static List<Material> CHESTPLATES = Arrays.asList(Material.CHAINMAIL_CHESTPLATE, Material.GOLD_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.IRON_CHESTPLATE, Material.LEATHER_CHESTPLATE);
    public final static List<Material> LEGGINGS = Arrays.asList(Material.CHAINMAIL_LEGGINGS, Material.GOLD_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.IRON_LEGGINGS, Material.LEATHER_LEGGINGS);
    public final static List<Material> BOOTS = Arrays.asList(Material.CHAINMAIL_BOOTS, Material.GOLD_BOOTS, Material.DIAMOND_BOOTS, Material.IRON_BOOTS, Material.LEATHER_BOOTS);
    
    public ItemStack boughtChestplate;
    public ItemStack boughtLeggings;
    
    public ShopItemArmor(int cost, Currency currency, ItemStack shopItemStack, ItemStack boughtChestplate, ItemStack boughtLeggings)
    {
        super(cost, currency, shopItemStack, null);
        this.boughtChestplate = boughtChestplate;
        this.boughtLeggings = boughtLeggings;
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
        
        for (ItemStack armorPiece : gamePlayer.spawnArmor)
        {
            Material armorMaterial = armorPiece.getType();
            if (ShopItemArmor.CHESTPLATES.contains(armorMaterial))
            {
                if (armorMaterial.equals(this.boughtChestplate.getType()))
                {
                    super.playSound(player, false);
                    return;
                }
            }
            else if (ShopItemArmor.LEGGINGS.contains(armorMaterial))
            {
                if (armorMaterial.equals(this.boughtLeggings.getType()))
                {
                    super.playSound(player, false);
                    return;
                }
            }
        }
        
        boolean success = gamePlayer.purchaseItem(this.cost, this.currency);
        
        if (!success)
        {
            super.playSound(player, false);
            return;
        }
        
        gamePlayer.setArmor(this.boughtChestplate.getType(), this.boughtLeggings.getType());
        super.playSound(player, true);
    }
}
