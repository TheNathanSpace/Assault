package com.thekingelessar.assault.game.inventory.shopitems;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ShopItemSword extends ShopItem
{
    public List<Material> swordList = Arrays.asList(Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD);
    
    public ShopItemSword(int cost, Currency currency, ItemStack shopItemStack, ItemStack boughtItemStack)
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
        
        boolean success = gamePlayer.purchaseItem(this.cost, this.currency);
        if (success)
        {
            boolean hasGoodSword = false;
            for (ItemStack inventoryItem : player.getInventory().getContents())
            {
                if (inventoryItem != null)
                {
                    if (swordList.contains(inventoryItem.getType()))
                    {
                        hasGoodSword = true;
                        break;
                    }
                }
            }
            
            if (hasGoodSword)
            {
                player.getInventory().addItem(this.boughtItemStack.clone());
            }
            else
            {
                ItemStack[] contents = player.getInventory().getContents();
                for (int i = 0; i < contents.length; i++)
                {
                    ItemStack inventoryItem = contents[i];
                    if (inventoryItem != null)
                    {
                        if (inventoryItem.getType().equals(Material.WOOD_SWORD))
                        {
                            player.getInventory().setItem(i, this.boughtItemStack.clone());
                            break;
                        }
                    }
                }
            }
        }
    
        super.playSound(player, success);
    }
}
