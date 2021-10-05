package com.thekingelessar.assault.game.team;

import com.thekingelessar.assault.util.version.XMaterial;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

public enum TeamColor
{
    BLACK(ChatColor.BLACK, Color.BLACK, "BLACK", "DARK_OAK", null),
    DARK_BLUE(ChatColor.DARK_BLUE, Color.NAVY, "BLUE", "WARPED", "DARK_OAK"),
    DARK_GREEN(ChatColor.DARK_GREEN, Color.GREEN, "GREEN", "WARPED", "JUNGLE"),
    DARK_AQUA(ChatColor.DARK_AQUA, Color.TEAL, "CYAN", "WARPED", "SPRUCE"),
    DARK_RED(ChatColor.DARK_RED, Color.MAROON, "RED", "CRIMSON", "ACACIA"),
    DARK_PURPLE(ChatColor.DARK_PURPLE, Color.PURPLE, "PURPLE", "CRIMSON", "SPRUCE"),
    GOLD(ChatColor.GOLD, Color.ORANGE, "ORANGE", "ACACIA", null),
    GRAY(ChatColor.GRAY, Color.SILVER, "LIGHT_GRAY", "BIRCH", null),
    DARK_GRAY(ChatColor.DARK_GRAY, Color.GRAY, "GRAY", "OAK", null),
    BLUE(ChatColor.BLUE, Color.BLUE, "LIGHT_BLUE", "WARPED", "BIRCH"),
    GREEN(ChatColor.GREEN, Color.LIME, "LIME", "WARPED", "BIRCH"),
    AQUA(ChatColor.AQUA, Color.AQUA, "CYAN", "WARPED", "SPRUCE"),
    RED(ChatColor.RED, Color.RED, "RED", "CRIMSON", "JUNGLE"),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, Color.FUCHSIA, "MAGENTA", "CRIMSON", "JUNGLE"),
    YELLOW(ChatColor.YELLOW, Color.YELLOW, "YELLOW", "BIRCH", null),
    WHITE(ChatColor.WHITE, Color.WHITE, "WHITE", "BIRCH", null);
    
    public static String WOOL_SUFFIX = "_WOOL";
    public static String TERRACOTTA_SUFFIX = "_TERRACOTTA";
    public static String STAINED_GLASS_SUFFIX = "_STAINED_GLASS";
    public static String PLANK_SUFFIX = "_PLANKS";
    
    public ChatColor chatColor;
    public Color color;
    public String coloredBlockPrefix;
    public String planksPrefix;
    public String fallbackPlanksPrefix;
    
    private TeamColor(ChatColor chatColor, Color color, String coloredBlockPrefix, String planksPrefix, String fallbackPlanksPrefix)
    {
        this.chatColor = chatColor;
        this.color = color;
        this.coloredBlockPrefix = coloredBlockPrefix;
        this.planksPrefix = planksPrefix;
        this.fallbackPlanksPrefix = fallbackPlanksPrefix;
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
    
    public ItemStack getWool()
    {
        return XMaterial.valueOf(this.coloredBlockPrefix + WOOL_SUFFIX).parseItem();
    }
    
    public ItemStack getTerracotta()
    {
        return XMaterial.valueOf(this.coloredBlockPrefix + TERRACOTTA_SUFFIX).parseItem();
    }
    
    public ItemStack getStainedGlass()
    {
        return XMaterial.valueOf(this.coloredBlockPrefix + STAINED_GLASS_SUFFIX).parseItem();
    }
    
    public ItemStack getPlank()
    {
        ItemStack firstChoice = XMaterial.valueOf(this.planksPrefix + PLANK_SUFFIX).parseItem();
        if (firstChoice == null)
        {
            return XMaterial.valueOf(this.fallbackPlanksPrefix + PLANK_SUFFIX).parseItem();
        }
        return firstChoice;
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
}
