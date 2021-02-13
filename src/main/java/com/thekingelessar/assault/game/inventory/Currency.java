package com.thekingelessar.assault.game.inventory;

import org.bukkit.ChatColor;

public enum Currency
{
    COINS(ChatColor.GOLD + "coins" + ChatColor.RESET),
    EMERALDS(ChatColor.GREEN + "emeralds" + ChatColor.RESET),
    GAMER_POINTS(ChatColor.AQUA + "gamer points" + ChatColor.RESET);
    
    public String name;
    
    Currency(String name) {
        this.name = name;
    }
}
