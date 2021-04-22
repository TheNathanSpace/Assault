package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.util.Title;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskAttackTimer extends BukkitRunnable
{
    public int startTicks;
    
    public int startDelay;
    
    public int tickDelay;
    
    public GameInstance gameInstance;
    
    private Title title = new Title();
    
    public TaskAttackTimer(int startTicks, int startDelay, int tickDelay, GameInstance gameInstance)
    {
        this.startTicks = startTicks;
        
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
        Integer attackingTime = gameInstance.getAttackingTeam().displaySeconds;
        attackingTime++;
        
        gameInstance.teams.get(gameInstance.attackingTeam).displaySeconds = attackingTime;
        
        gameInstance.updateScoreboards();
    }
    
    public void finishTimer()
    {
        this.gameInstance.taskAttackTimer = null;
        
        this.cancel();
    }
}
