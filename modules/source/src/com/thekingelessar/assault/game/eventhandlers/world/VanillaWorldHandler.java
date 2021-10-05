package com.thekingelessar.assault.game.eventhandlers.world;

import com.thekingelessar.assault.util.version.XMaterial;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class VanillaWorldHandler implements Listener
{
    @EventHandler
    public void onCactusGrowth(BlockGrowEvent blockGrowEvent)
    {
        BlockState state = blockGrowEvent.getNewState();
        
        if (state.getType().equals(Material.CACTUS))
        {
            blockGrowEvent.setCancelled(true);
        }
        
        if (state.getType().equals(XMaterial.SUGAR_CANE))
        {
            blockGrowEvent.setCancelled(true);
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
