package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.util.Title;
import com.thekingelessar.assault.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TaskCountdownSwapAttackers extends BukkitRunnable
{
    public int startTicks;
    public int startDelay;
    
    public int tickDelay;
    public int ticksLeft;
    
    public GameInstance gameInstance;
    
    private Title title = new Title();
    
    public TaskCountdownSwapAttackers(int startTicks, int startDelay, int tickDelay, GameInstance gameInstance)
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
        
        String mainTitle = gameInstance.getAttackingTeam().color.getFormattedName(true, true, ChatColor.BOLD) + ChatColor.WHITE + ": " + Util.secondsToMinutes(gameInstance.getAttackingTeam().displaySeconds, false);
        title = new Title(mainTitle, ChatColor.WHITE + "Swapping teams in " + ChatColor.LIGHT_PURPLE + (ticksLeft / 20) + ChatColor.WHITE + " seconds");
        
        List<Player> players = gameInstance.gameWorld.getPlayers();
        for (Player player : players)
        {
            title.clearTitle(player);
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
        this.gameInstance.taskCountdownSwapAttackers = null;
        this.gameInstance.swapAttackingTeams();
    }
}
