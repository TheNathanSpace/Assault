package com.thekingelessar.assault.game.team;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

public enum TeamColor
{
    BLACK(ChatColor.BLACK),
    DARK_BLUE(ChatColor.DARK_BLUE),
    DARK_GREEN(ChatColor.DARK_GREEN),
    DARK_AQUA(ChatColor.DARK_AQUA),
    DARK_RED(ChatColor.DARK_RED),
    DARK_PURPLE(ChatColor.DARK_PURPLE),
    GOLD(ChatColor.GOLD),
    GRAY(ChatColor.GRAY),
    DARK_GRAY(ChatColor.DARK_GRAY),
    BLUE(ChatColor.BLUE),
    GREEN(ChatColor.GREEN),
    AQUA(ChatColor.AQUA),
    RED(ChatColor.RED),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE),
    YELLOW(ChatColor.YELLOW),
    WHITE(ChatColor.WHITE);
    
    public ChatColor chatColor;
    
    private TeamColor(ChatColor color)
    {
        this.chatColor = color;
    }
    
    public String getFormattedName(boolean lower, boolean capitalized, ChatColor formatting)
    {
        String fixedName = this.toString().replace("_", " ");
        
        if (lower)
        {
            fixedName = fixedName.toLowerCase();
        }
        
        if (capitalized)
        {
            fixedName = StringUtils.capitalize(fixedName);
        }
        
        if (formatting != null)
        {
            return this.chatColor.toString() + formatting + fixedName;
        }
        
        return this.chatColor + fixedName;
    }
}
