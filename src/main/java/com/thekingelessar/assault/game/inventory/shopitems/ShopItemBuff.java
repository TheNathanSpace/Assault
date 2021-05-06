package com.thekingelessar.assault.game.inventory.shopitems;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.shops.ShopTeamBuffs;
import com.thekingelessar.assault.game.inventory.teambuffs.IBuff;
import com.thekingelessar.assault.game.player.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopItemBuff extends ShopItem
{
    
    public IBuff buff;
    public ItemStack alreadyPurchasedItemStack;
    
    public ShopItemBuff(int cost, ItemStack shopItemStack, ItemStack alreadyPurchasedItemStack, IBuff buff)
    {
        super(cost, Currency.GAMER_POINTS, shopItemStack, null);
        
        this.alreadyPurchasedItemStack = alreadyPurchasedItemStack;
        this.buff = buff;
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
            if (gamePlayer.gameTeam.buffList.contains(this.buff))
            {
                success = false;
            }
            else
            {
                gamePlayer.gameTeam.gamerPoints -= this.cost;
                gamePlayer.gameTeam.buffList.add(this.buff);
                this.updateItemPurchase(gamePlayer.gameTeam.shopTeamBuffs);
            }
        }
        
        super.playSound(player, success);
    }
    
    public void updateItemPurchase(ShopTeamBuffs shopTeamBuffs)
    {
        shopTeamBuffs.shopItemMap.put(this.alreadyPurchasedItemStack, this);
        shopTeamBuffs.inventory.setItem(this.slot, this.alreadyPurchasedItemStack);
    }
    
}
