package com.thekingelessar.assault.game.pregame.teamselection;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopItemTeam extends ShopItem
{
    
    public SelectedTeam selectedTeam;
    public ItemStack chosenItemStack;
    
    public ShopItemTeam(ItemStack notChosenItemStack, ItemStack chosenItemStack, SelectedTeam selectedTeam)
    {
        super(0, Currency.GAMER_POINTS, notChosenItemStack, null);
        
        this.chosenItemStack = chosenItemStack;
        this.selectedTeam = selectedTeam;
    }
    
    @Override
    public void buyItem(Player player)
    {
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        if (gameInstance == null)
        {
            return;
        }
        
        boolean joined = this.selectedTeam.addPlayer(player);
        
        gameInstance.updateTeamSelectionShops();
        
        super.playSound(player, joined);
    }
    
    public ItemStack updateCount(PlayerShopTeamSelection playerShopTeamSelection)
    {
        ItemStack newItemStack = null;
        
        if (this.selectedTeam.members.contains(playerShopTeamSelection.player))
        {
            newItemStack = this.chosenItemStack.clone();
        }
        else
        {
            newItemStack = this.shopItemStack.clone();
        }
        
        int players = this.selectedTeam.members.size();
        
        newItemStack.setAmount(players);
        
        ItemMeta itemMeta = newItemStack.getItemMeta();
        List<String> loreList = new ArrayList<>();
        loreList.add(ChatColor.RESET + ChatColor.UNDERLINE.toString() + String.format("Players (%s):", players));
        for (Player player : this.selectedTeam.members)
        {
            loreList.add(ChatColor.RESET + " - " + player.getName());
        }
        
        loreList.add("");
        if (this.selectedTeam.members.contains(playerShopTeamSelection.player))
        {
            loreList.add(ChatColor.RESET + "Click to " + ChatColor.RED + "leave team" + ChatColor.RESET + "!");
        }
        else
        {
            String teamName = selectedTeam.teamColor.getFormattedName(true, true, ChatColor.BOLD);
            teamName += ChatColor.RESET + ChatColor.BOLD.toString() + " Team" + ChatColor.RESET;
            loreList.add(ChatColor.RESET + "Click to join " + teamName + "!");
        }
        
        itemMeta.setLore(loreList);
        newItemStack.setItemMeta(itemMeta);
        
        playerShopTeamSelection.inventory.setItem(this.slot, newItemStack);
        
        return newItemStack;
    }
    
}
