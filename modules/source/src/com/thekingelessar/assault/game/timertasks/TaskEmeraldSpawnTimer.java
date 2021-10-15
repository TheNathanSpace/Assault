package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.util.Coordinate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TaskEmeraldSpawnTimer extends BukkitRunnable
{
    public int startTicks;
    
    public int startDelay;
    
    public int tickDelay;
    
    public int secondsBetweenEmeralds;
    
    public int currentTicks = 0;
    
    public GameInstance gameInstance;
    
    public Item spawnedItem = null;
    
    public TaskEmeraldSpawnTimer(int startTicks, int startDelay, int tickDelay, GameInstance gameInstance, int secondsBetweenEmeralds)
    {
        this.startTicks = startTicks;
        
        this.secondsBetweenEmeralds = secondsBetweenEmeralds;
        
        this.startDelay = startDelay;
        this.tickDelay = tickDelay;
        this.gameInstance = gameInstance;
    }
    
    @Override
    public void run()
    {
        advanceTimer();
    }
    
    public void advanceTimer()
    {
        double remainder = currentTicks % 20;
        
        if (Assault.useHolographicDisplays)
        {
            if (currentTicks != 0 && remainder == 0)
            {
                int ticksRemoved = 400;
                int secondsLeft = currentTicks;
                while (secondsLeft > ticksRemoved)
                {
                    secondsLeft -= ticksRemoved;
                }
                
                secondsLeft = 20 - secondsLeft / 20;
                
                gameInstance.line2.setText(String.format("§rSpawning in §d%s§r seconds!", secondsLeft));
            }
        }
        
        if (currentTicks != 0 && remainder == 0 && (currentTicks / 20) % secondsBetweenEmeralds == 0)
        {
            for (Coordinate coordinate : gameInstance.getDefendingTeam().mapBase.emeraldSpawns)
            {
                Location location = coordinate.toLocation(gameInstance.gameWorld);
                this.spawnedItem = gameInstance.gameWorld.dropItem(location, new ItemStack(Material.EMERALD));
                
                Vector velocity = spawnedItem.getVelocity();
                this.spawnedItem.setVelocity(velocity.zero());
                
                spawnedItem.teleport(location);
            }
        }
        
        currentTicks += 1;
    }
    
    public void stopTimer()
    {
        this.gameInstance.emeraldSpawnTimer = null;
        gameInstance.emeraldSpawnHologram.delete();
        this.cancel();
    }
    
}
