package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import org.bukkit.Bukkit;

public class RegisterHandlers
{
    static public void registerHandlers()
    {
        Bukkit.getServer().getPluginManager().registerEvents(new GravelFallHandler(), Assault.INSTANCE);
        
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryCloseHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryDragHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new ItemDespawnHandler(), Assault.INSTANCE);
        
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerAttackVictimHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerBlockBreakHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerBlockPlaceHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerChatHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDamageHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerFoodLevelChange(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerItemDropHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinServerHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerLeaveServerHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerMoveHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerMoveWorldHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerPickupItemHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerRightClickHandler(), Assault.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerToggleFlightHandler(), Assault.INSTANCE);
        
        Bukkit.getServer().getPluginManager().registerEvents(new VanillaWorldHandler(), Assault.INSTANCE);
        
    }
}
