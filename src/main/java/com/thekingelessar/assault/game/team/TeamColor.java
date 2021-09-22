package com.thekingelessar.assault.game.team;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public enum TeamColor
{
    BLACK(ChatColor.BLACK, Color.BLACK, 15),
    DARK_BLUE(ChatColor.DARK_BLUE, Color.NAVY, 11),
    DARK_GREEN(ChatColor.DARK_GREEN, Color.GREEN, 13),
    DARK_AQUA(ChatColor.DARK_AQUA, Color.TEAL, 11),
    DARK_RED(ChatColor.DARK_RED, Color.MAROON, 14),
    DARK_PURPLE(ChatColor.DARK_PURPLE, Color.PURPLE, 10),
    GOLD(ChatColor.GOLD, Color.ORANGE, 1),
    GRAY(ChatColor.GRAY, Color.SILVER, 8),
    DARK_GRAY(ChatColor.DARK_GRAY, Color.GRAY, 7),
    BLUE(ChatColor.BLUE, Color.BLUE, 11),
    GREEN(ChatColor.GREEN, Color.LIME, 5),
    AQUA(ChatColor.AQUA, Color.AQUA, 11),
    RED(ChatColor.RED, Color.RED, 14),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, Color.FUCHSIA, 6),
    YELLOW(ChatColor.YELLOW, Color.YELLOW, 4),
    WHITE(ChatColor.WHITE, Color.WHITE, 0);
    
    public ChatColor chatColor;
    public Color color;
    public int dataValue;
    
    private TeamColor(ChatColor chatColor, Color color, int dataValue)
    {
        this.chatColor = chatColor;
        this.color = color;
        this.dataValue = dataValue;
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
    
    public static TeamColor valueOfCaseInsensitive(String name)
    {
        for (TeamColor status : TeamColor.values())
        {
            if (status.name().equalsIgnoreCase(name))
            {
                return status;
            }
        }
        return null;
    }
    
    public static List<TeamColor> findByDataValue(int i)
    {
        List<TeamColor> returnedColors = new ArrayList<>();
        
        TeamColor[] teamColors = TeamColor.values();
        for (TeamColor teamColor : teamColors)
        {
            if (teamColor.dataValue == i)
            {
                returnedColors.add(teamColor);
            }
        }
        
        return returnedColors;
    }
}
