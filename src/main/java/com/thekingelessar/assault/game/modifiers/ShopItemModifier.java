package com.thekingelessar.assault.game.modifiers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopItemModifier extends ShopItem
{
    
    public GameModifier gameModifier;
    public ItemStack alreadyVotedItemStack;
    
    public ShopItemModifier(ItemStack notVotedItemStack, ItemStack alreadyVotedItemStack, GameModifier gameModifier)
    {
        super(0, Currency.GAMER_POINTS, notVotedItemStack, null);
        
        this.alreadyVotedItemStack = alreadyVotedItemStack;
        this.gameModifier = gameModifier;
    }
    
    @Override
    public void buyItem(Player player)
    {
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        if (gameInstance == null)
        {
            return;
        }
        
        if (gameModifier.votedPlayers.contains(player))
        {
            gameModifier.votedPlayers.remove(player);
        }
        else
        {
            gameModifier.votedPlayers.add(player);
        }
        
        gameInstance.updateModShops();
        
        super.playSound(player, gameModifier.votedPlayers.contains(player));
    }
    
    public void updateCount(ShopPlayerModifier shopPlayerModifier)
    {
        ItemStack newItemStack = null;
        
        if (gameModifier.votedPlayers.contains(shopPlayerModifier.player))
        {
            newItemStack = this.alreadyVotedItemStack;
        }
        else
        {
            newItemStack = this.shopItemStack;
        }
        
        int votes = this.gameModifier.votedPlayers.size();
        
        if (votes == 0)
        {
            newItemStack.setAmount(1);
        }
        else
        {
            newItemStack.setAmount(votes);
        }
        
        ItemMeta itemMeta = newItemStack.getItemMeta();
        String name = itemMeta.getDisplayName();
        String nameWithVotes = name + " | Votes: " + votes;
        itemMeta.setDisplayName(nameWithVotes);
        newItemStack.setItemMeta(itemMeta);
        
        shopPlayerModifier.shopItemMap.put(newItemStack, this);
        shopPlayerModifier.inventory.setItem(this.slot, newItemStack);
    }
    
}
