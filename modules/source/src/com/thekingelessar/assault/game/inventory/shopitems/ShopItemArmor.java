package com.thekingelessar.assault.game.inventory.shopitems;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.util.version.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ShopItemArmor extends ShopItem
{
    public final static List<Material> HELMETS = Arrays.asList(XMaterial.CHAINMAIL_HELMET.parseMaterial(), XMaterial.GOLDEN_HELMET.parseMaterial(), XMaterial.DIAMOND_HELMET.parseMaterial(), XMaterial.IRON_HELMET.parseMaterial(), XMaterial.LEATHER_HELMET.parseMaterial());
    public final static List<Material> CHESTPLATES = Arrays.asList(XMaterial.CHAINMAIL_CHESTPLATE.parseMaterial(), XMaterial.GOLDEN_CHESTPLATE.parseMaterial(), XMaterial.DIAMOND_CHESTPLATE.parseMaterial(), XMaterial.IRON_CHESTPLATE.parseMaterial(), XMaterial.LEATHER_CHESTPLATE.parseMaterial());
    public final static List<Material> LEGGINGS = Arrays.asList(XMaterial.CHAINMAIL_LEGGINGS.parseMaterial(), XMaterial.GOLDEN_LEGGINGS.parseMaterial(), XMaterial.DIAMOND_LEGGINGS.parseMaterial(), XMaterial.IRON_LEGGINGS.parseMaterial(), XMaterial.LEATHER_LEGGINGS.parseMaterial());
    public final static List<Material> BOOTS = Arrays.asList(XMaterial.CHAINMAIL_BOOTS.parseMaterial(), XMaterial.GOLDEN_BOOTS.parseMaterial(), XMaterial.DIAMOND_BOOTS.parseMaterial(), XMaterial.IRON_BOOTS.parseMaterial(), XMaterial.LEATHER_BOOTS.parseMaterial());
    
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
