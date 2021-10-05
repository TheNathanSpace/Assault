package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.game.GameInstance;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskTickTimer extends BukkitRunnable
{
    public int startDelay;
    public int tickDelay;
    
    public GameInstance gameInstance;
    
    public TaskTickTimer(int startDelay, int tickDelay, GameInstance gameInstance)
    {
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
        gameInstance.doBuffs();
    }
    
}
