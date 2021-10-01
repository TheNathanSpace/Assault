package com.thekingelessar.assault.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public class ItemInit
{
    public static ItemStack initGameModifierItemStack()
    {
        ItemStack itemStack = new ItemStack(Material.REDSTONE_COMPARATOR);
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Modifiers");
        itemMeta.setLore(Collections.singletonList(ChatColor.RESET + "Click this to open up the modifier voting menu!"));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static ItemStack initTeamSelectionItemStack()
    {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Team Selection");
        itemMeta.setLore(Collections.singletonList(ChatColor.RESET + "Click this to open up the team selection menu!"));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static ItemStack initStar()
    {
        ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Join Game");
        itemMeta.setLore(Arrays.asList(ChatColor.RESET + "Click this to join a game of Assault!", ChatColor.RESET + "", ChatColor.RESET + "If there's no game available,", ChatColor.RESET + "you'll be added to the queue."));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static ItemStack initInQueueStar()
    {
        ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "You are in the queue!");
        itemMeta.setLore(Arrays.asList(ChatColor.RESET + "You have been added to the queue!", ChatColor.RESET + "", ChatColor.RESET + "When there are enough players,", ChatColor.RESET + "the game will start."));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static ItemStack initBarrier()
    {
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Leave Queue");
        itemMeta.setLore(Collections.singletonList(ChatColor.RESET + "Click this to leave the queue."));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static ItemStack initBook()
    {
        ItemStack itemStack = new ItemStack(Material.BOOK);
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.BLUE + ChatColor.BOLD.toString() + "Guide");
        itemMeta.setLore(Collections.singletonList(ChatColor.RESET + "Click this to open up the guide!"));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
}
