package com.thekingelessar.assault.game.inventory.shopitems;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.player.GamePlayer;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ShopItemColoredBlock extends ShopItem
{
    public List<Material> durabilityList = Arrays.asList(Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS);
    
    public ShopItemColoredBlock(int cost, Currency currency, ItemStack shopItemStack, ItemStack boughtItemStack)
    {
        super(cost, currency, shopItemStack, boughtItemStack);
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
            ItemStack givingStack = this.boughtItemStack.clone();
            givingStack.setDurability(DyeColor.valueOf(gameInstance.getPlayerTeam(player).color.toString()).getData());
            
            player.getInventory().addItem(givingStack);
        }
    
        super.playSound(player, success);
    }

}
