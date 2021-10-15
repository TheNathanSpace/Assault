package com.thekingelessar.assault.game.eventhandlers.world;

import com.thekingelessar.assault.util.version.XMaterial;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class VanillaWorldHandler implements Listener
{
    @EventHandler
    public void onCactusGrowth(BlockGrowEvent blockGrowEvent)
    {
        BlockState state = blockGrowEvent.getNewState();
        
        if (state.getType().equals(XMaterial.CACTUS.parseMaterial()))
        {
            blockGrowEvent.setCancelled(true);
        }
        
        if (state.getType().equals(XMaterial.SUGAR_CANE.parseMaterial()))
        {
            blockGrowEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onVineGrowth(BlockSpreadEvent blockSpreadEvent)
    {
        BlockState state = blockSpreadEvent.getNewState();
        
        if (state.getType().equals(XMaterial.VINE.parseMaterial()))
        {
            blockSpreadEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent spawnEvent)
    {
        if (!(spawnEvent.getEntity() instanceof Player))
        {
            spawnEvent.setCancelled(true);
        }
    }
    
}
