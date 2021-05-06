package com.thekingelessar.assault.game.inventory.shopitems;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ShopItemTool extends ShopItem
{
    public List<Material> toolsList = Arrays.asList(Material.SHEARS, Material.WOOD_AXE, Material.WOOD_PICKAXE, Material.STONE_AXE, Material.STONE_PICKAXE, Material.GOLD_AXE, Material.GOLD_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE);
    
    public ShopItemTool(int cost, Currency currency, ItemStack shopItemStack, ItemStack boughtItemStack)
    {
        super(cost, currency, shopItemStack, boughtItemStack);
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
        
        boolean success = gamePlayer.purchaseItem(this.cost, this.currency);
        if (success)
        {
            player.getInventory().addItem(this.boughtItemStack.clone());
            // todo: record player has tool
        }
        
        super.playSound(player, success);
    }
}
