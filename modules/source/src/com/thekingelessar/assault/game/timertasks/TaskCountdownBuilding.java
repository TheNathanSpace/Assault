package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TaskCountdownBuilding extends BukkitRunnable
{
    public int startTicks;
    
    public int startDelay;
    
    public int tickDelay;
    public int ticksLeft;
    
    public GameInstance gameInstance;
    
    public boolean doneFirst = false;
    
    public TaskCountdownBuilding(int startTicks, int startDelay, int tickDelay, GameInstance gameInstance)
    {
        this.startTicks = startTicks;
        this.ticksLeft = startTicks;
        
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
        if (ticksLeft < 20)
        {
            finishTimer();
            return;
        }
        
        if (ticksLeft < 201)
        {
            List<Player> players = gameInstance.getPlayers();
            for (Player player : players)
            {
                player.sendTitle(" ", ChatColor.WHITE + "Building finished in " + ChatColor.LIGHT_PURPLE + (ticksLeft / 20) + ChatColor.WHITE + " seconds");
            }
        }
        
        gameInstance.buildingSecondsLeft = ticksLeft / 20;
        
        gameInstance.updateScoreboards();
        
        ticksLeft = ticksLeft - tickDelay;
    }
    
    public void finishTimer()
    {
        this.gameInstance.taskCountdownBuilding = null;
        this.gameInstance.gameStage = GameStage.ATTACKING;
        
        this.gameInstance.startAttackMode();
        
        this.cancel();
    }
}
