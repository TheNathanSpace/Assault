package com.thekingelessar.assault;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Assault extends JavaPlugin
{
    
    static public FileConfiguration config = null;
    
    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();
        config = this.getConfig();
        
        super.onEnable();
    }
    
    @Override
    public void onDisable()
    {
        super.onDisable();
    }
    
}