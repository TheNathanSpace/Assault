package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.game.GameInstance;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TaskCountdownGameStart extends BukkitRunnable
{
    public int startTicks;
    public int startDelay;
    
    public int tickDelay;
    public int ticksLeft;
    
    public GameInstance gameInstance;
    
    public TaskCountdownGameStart(int startTicks, int startDelay, int tickDelay, GameInstance gameInstance)
    {
        this.startTicks = startTicks;
        this.startDelay = startDelay;
        this.tickDelay = tickDelay;
        this.ticksLeft = this.startTicks;
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
        
        List<Player> players = gameInstance.gameWorld.getPlayers();
        for (Player player : players)
        {
            player.sendTitle(ChatColor.WHITE + "Starting in " + ChatColor.LIGHT_PURPLE + (ticksLeft / 20) + ChatColor.WHITE + "!", "Vote for modifiers!");
        }
        
        ticksLeft = ticksLeft - tickDelay;
    }
    
    public void finishTimer()
    {
        List<Player> players = gameInstance.gameWorld.getPlayers();
        for (Player player : players)
        {
            player.resetTitle();
        }
        
        this.cancel();
        this.gameInstance.taskCountdownGameStart = null;
        this.gameInstance.startBuildMode();
    }
    
    public void cancelTimer()
    {
        List<Player> players = gameInstance.gameWorld.getPlayers();
        for (Player player : players)
        {
            player.sendTitle(" ", ChatColor.WHITE + "Game start " + ChatColor.RED + "canceled!", 0, 50, 10);
        }
        
        this.cancel();
        this.gameInstance.taskCountdownGameStart = null;
    }
}
