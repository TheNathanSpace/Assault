package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.game.GameEndManager;
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
        this.gameInstance.getAttackingTeam().finalAttackingTime = this.gameInstance.gameMap.attackTimeLimit;
        if (this.gameInstance.modFirstToFive.enabled)
        {
            this.gameInstance.getAttackingTeam().finalAttackingTime = this.gameInstance.gameMap.firstToFiveStarsTimeLimit;
        }
        
        this.gameInstance.endRound(false);
        
        if (gameInstance.teamsGone == 1)
        {
            if (gameInstance.isTie())
            {
                gameInstance.gameEndManager.declareWinners(GameEndManager.WinState.TIE);
            }
            else if (gameInstance.isTimeTie() && gameInstance.modFirstToFive.enabled)
            {
                gameInstance.gameEndManager.declareWinners(GameEndManager.WinState.MOST_STARS);
            }
            else
            {
                gameInstance.gameEndManager.declareWinners(GameEndManager.WinState.LOWEST_TIME);
            }
        }
        
        this.cancel();
    }
}
