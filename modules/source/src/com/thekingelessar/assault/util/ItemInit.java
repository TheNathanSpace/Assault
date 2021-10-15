package com.thekingelessar.assault.util;

import com.thekingelessar.assault.util.version.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public class ItemInit
{
    public static ItemStack initGameModifierItemStack()
    {
        ItemStack itemStack = new ItemStack(XMaterial.COMPARATOR.parseMaterial());
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Modifiers");
        itemMeta.setLore(Collections.singletonList(Util.RESET_CHAT + "Click this to open up the modifier voting menu!"));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static ItemStack initManualStar()
    {
        ItemStack itemStack = new ItemStack(XMaterial.NETHER_STAR.parseMaterial());
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Objective");
        itemMeta.setLore(Arrays.asList(
                Util.RESET_CHAT + "This is your team's objective!",
                Util.RESET_CHAT + "To place, drop this at the chosen location!",
                Util.RESET_CHAT + "You don't " + ChatColor.ITALIC + "have" + Util.RESET_CHAT + " to place this.",
                Util.RESET_CHAT + "If you aren't sure, you can always pick this up during the Building phase.",
                Util.RESET_CHAT + "If your team drops more than one, they'll have multiple objectives!"));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static ItemStack initRetrieveStar()
    {
        ItemStack itemStack = new ItemStack(XMaterial.BARRIER.parseMaterial());
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Retrieve Objective");
        itemMeta.setLore(Arrays.asList(
                Util.RESET_CHAT + "To retrieve your objective, drop this!",
                Util.RESET_CHAT + ChatColor.BOLD.toString() + "This cannot be undone!"));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static ItemStack initTeamSelectionItemStack()
    {
        ItemStack itemStack = new ItemStack(XMaterial.PAPER.parseMaterial());
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Team Selection");
        itemMeta.setLore(Collections.singletonList(Util.RESET_CHAT + "Click this to open up the team selection menu!"));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static ItemStack initStar()
    {
        ItemStack itemStack = new ItemStack(XMaterial.NETHER_STAR.parseMaterial());
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Join Game");
        itemMeta.setLore(Arrays.asList(Util.RESET_CHAT + "Click this to join a game of Assault!", Util.RESET_CHAT + "", Util.RESET_CHAT + "If there's no game available,", Util.RESET_CHAT + "you'll be added to the queue."));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static ItemStack initInQueueStar()
    {
        ItemStack itemStack = new ItemStack(XMaterial.NETHER_STAR.parseMaterial());
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "You are in the queue!");
        itemMeta.setLore(Arrays.asList(Util.RESET_CHAT + "You have been added to the queue!", Util.RESET_CHAT + "", Util.RESET_CHAT + "When there are enough players,", Util.RESET_CHAT + "the game will start."));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static ItemStack initBarrier()
    {
        ItemStack itemStack = new ItemStack(XMaterial.BARRIER.parseMaterial());
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Leave Queue");
        itemMeta.setLore(Collections.singletonList(Util.RESET_CHAT + "Click this to leave the queue."));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static ItemStack initBook()
    {
        ItemStack itemStack = new ItemStack(XMaterial.BOOK.parseMaterial());
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.BLUE + ChatColor.BOLD.toString() + "Guide");
        itemMeta.setLore(Collections.singletonList(Util.RESET_CHAT + "Click this to open up the guide!"));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
}
