package com.thekingelessar.assault.game.inventory.shops;

import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.ShopUtil;
import com.thekingelessar.assault.game.inventory.TeamBuffItem;
import com.thekingelessar.assault.game.inventory.teambuffs.BuffHaste;
import com.thekingelessar.assault.game.inventory.teambuffs.BuffHealing;
import com.thekingelessar.assault.game.inventory.teambuffs.BuffSpeed;
import com.thekingelessar.assault.game.inventory.teambuffs.IBuff;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopTeamBuffs
{
    public List<TeamBuffItem> shopItems = new ArrayList<>();
    
    public Inventory inventory;
    
    public List<TeamBuffItem> getShopItems()
    {
        return shopItems;
    }
    
    private List<Integer> airSlots = new ArrayList<>();
    
    public ShopTeamBuffs(GameTeam gameTeam, List<ItemStack> additionalItems)
    {
        for (int i = 0; i < 10; i++)
        {
            airSlots.add(i);
        }
        
        inventory = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Team Upgrade Shop");
        
        constructShopItem(new ItemStack(Material.GOLD_PICKAXE, 1), new BuffHaste(gameTeam), "Haste Upgrade", "Gives your team permanent Haste I.", 4, false);
        constructShopItem(new ItemStack(Material.FEATHER, 1), new BuffSpeed(gameTeam), "Speed Upgrade", "Gives your team permanent Speed I.", 6, false);
        constructShopItem(new ItemStack(Material.SPECKLED_MELON, 1), new BuffHealing(gameTeam), "Healing Upgrade", "Gives your team permanent Regeneration II.", 8, false);
        
    }
    
    private void constructShopItem(ItemStack itemStack, IBuff buff, String name, String description, int cost, boolean newRow)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET + name);
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET + description);
        lore.add("");
        lore.add(ChatColor.GRAY + "Cost: " + ChatColor.RESET + cost + " " + Currency.GAMER_POINTS.name);
        lore.add(ChatColor.RESET + "Click to buy!");
        itemMeta.setLore(lore);
        
        itemStack.setItemMeta(itemMeta);
        
        TeamBuffItem shopItem = new TeamBuffItem(cost, itemStack, buff);
        
        this.shopItems.add(shopItem);
        
        ShopUtil.insertItem(this.inventory, airSlots, shopItem, newRow);
    }
    
    public void modifyShopItem(TeamBuffItem oldShopItem, ItemStack itemStack, String name, String description, int slot)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET + name);
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET + description);
        lore.add("");
        lore.add(ChatColor.RESET + "Your team already has this upgrade!");
        itemMeta.setLore(lore);
        
        itemStack.setItemMeta(itemMeta);
        
        this.shopItems.remove(oldShopItem);
        
        inventory.setItem(slot, itemStack);
    }
    
}
