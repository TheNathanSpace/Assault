package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.util.Title;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TaskCountdownAttackEnd extends BukkitRunnable
{
    public int startTicks;
    
    public int startDelay;
    
    public int tickDelay;
    public int ticksLeft;
    
    public GameInstance gameInstance;
    
    private Title title = new Title();
    
    public TaskCountdownAttackEnd(int startTicks, int startDelay, int tickDelay, GameInstance gameInstance)
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
        
        List<Player> players = gameInstance.getPlayers();
        for (Player player : players)
        {
            title.clearTitle(player);
        }
        
        title = new Title(" ", ChatColor.WHITE + "This round finished in " + ChatColor.LIGHT_PURPLE + (ticksLeft / 20) + ChatColor.WHITE + " seconds");
        
        for (Player player : players)
        {
            title.send(player);
        }
        
        ticksLeft = ticksLeft - tickDelay;
    }
    
    public void finishTimer()
    {
        this.gameInstance.taskCountdownAttackEnd = null;
    
        this.gameInstance.finishRound(gameInstance.getAttackingTeam());
    
        if (gameInstance.teamsGone == 0)
        {
            gameInstance.updateScoreboards();
            
            gameInstance.taskCountdownSwapAttackers = new TaskCountdownSwapAttackers(200, 0, 20, gameInstance);
            gameInstance.taskCountdownSwapAttackers.runTaskTimer(Assault.INSTANCE, gameInstance.taskCountdownSwapAttackers.startDelay, gameInstance.taskCountdownSwapAttackers.tickDelay);
        }
        else
        {
            gameInstance.declareWinners(null, false);
        }
        
        this.cancel();
    }
}
