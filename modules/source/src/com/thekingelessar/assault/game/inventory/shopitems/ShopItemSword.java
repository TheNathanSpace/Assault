package com.thekingelessar.assault.game.inventory.shopitems;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.util.version.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ShopItemSword extends ShopItem
{
    public List<Material> swordList = Arrays.asList(XMaterial.STONE_SWORD.parseMaterial(), XMaterial.IRON_SWORD.parseMaterial(), XMaterial.GOLDEN_SWORD.parseMaterial(), XMaterial.DIAMOND_SWORD.parseMaterial());
    
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
                ItemStack giveItemStack = this.boughtItemStack.clone();
                Assault.unbreakable.setUnbreakable(giveItemStack);
                player.getInventory().addItem(giveItemStack);
            }
            else
            {
                ItemStack[] contents = player.getInventory().getContents();
                for (int i = 0; i < contents.length; i++)
                {
                    ItemStack inventoryItem = contents[i];
                    if (inventoryItem != null)
                    {
                        if (inventoryItem.getType().equals(XMaterial.WOODEN_SWORD.parseMaterial()))
                        {
                            ItemStack giveItemStack = this.boughtItemStack.clone();
                            Assault.unbreakable.setUnbreakable(giveItemStack);
                            player.getInventory().setItem(i, giveItemStack);
                            break;
                        }
                    }
                }
            }
        }
        
        super.playSound(player, success);
    }
}
