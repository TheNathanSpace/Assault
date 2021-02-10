package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import org.bukkit.Bukkit;

public class RegisterHandlers
{
    static public void registerHandlers()
    {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDamageHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlantGrowthHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerAttackVictimHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerBlockBreakHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerBlockPlaceHandler(), Assault.INSTANCE);
    }
}