package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import org.bukkit.Bukkit;

public class RegisterHandlers
{
    static public void registerHandlers()
    {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathHandler(), Assault.INSTANCE);
    }
}
