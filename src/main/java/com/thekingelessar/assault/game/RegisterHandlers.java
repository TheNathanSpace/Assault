package com.thekingelessar.assault.game;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.eventhandlers.PlayerDeathHandler;
import com.thekingelessar.assault.game.world.eventhandlers.PlantGrowthHandler;
import org.bukkit.Bukkit;

public class RegisterHandlers
{
    static public void registerHandlers()
    {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlantGrowthHandler(), Assault.INSTANCE);
    }
}
