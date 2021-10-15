package com.thekingelessar.assault.game.inventory;

import com.thekingelessar.assault.util.Util;
import org.bukkit.ChatColor;

public enum Currency
{
    COINS(ChatColor.GOLD + "coins" + Util.RESET_CHAT),
    EMERALDS(ChatColor.GREEN + "emeralds" + Util.RESET_CHAT),
    GAMER_POINTS(ChatColor.AQUA + "gamer points" + Util.RESET_CHAT);
    
    public String name;
    
    Currency(String name)
    {
        this.name = name;
    }
}
