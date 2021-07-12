package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.util.Title;
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
    
    private Title title = new Title();
    
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
            title.clearTitle(player);
        }
        
        title = new Title(ChatColor.WHITE + "Starting in " + ChatColor.LIGHT_PURPLE + (ticksLeft / 20) + ChatColor.WHITE + "!", "Vote for modifiers!");
        
        for (Player player : players)
        {
            title.send(player);
        }
        
        ticksLeft = ticksLeft - tickDelay;
    }
    
    public void finishTimer()
    {
        List<Player> players = gameInstance.gameWorld.getPlayers();
        for (Player player : players)
        {
            title.clearTitle(player);
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
            title.clearTitle(player);
        }
        
        title = new Title(" ", ChatColor.WHITE + "Game start " + ChatColor.RED + "canceled", 0, 20, 20);
        title.setTimingsToTicks();
        
        for (Player player : players)
        {
            title.send(player);
        }
        
        this.cancel();
        this.gameInstance.taskCountdownGameStart = null;
    }
}
