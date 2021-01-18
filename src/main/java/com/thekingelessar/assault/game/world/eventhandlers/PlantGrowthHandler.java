package com.thekingelessar.assault.game.world.eventhandlers;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

public class PlantGrowthHandler implements Listener
{
    @EventHandler
    public void onCactusGrowth(BlockGrowEvent blockGrowEvent)
    {
        BlockState state = blockGrowEvent.getNewState();
        
        if (state.getType().equals(Material.CACTUS))
        {
            blockGrowEvent.setCancelled(true);
        }
        
        if (state.getType().equals(Material.SUGAR_CANE_BLOCK))
        {
            blockGrowEvent.setCancelled(true);
        }
    }
}
