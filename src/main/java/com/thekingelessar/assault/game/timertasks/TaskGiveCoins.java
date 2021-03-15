package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskGiveCoins extends BukkitRunnable
{
    public int startDelay;
    public int tickDelay;
    
    public GameInstance gameInstance;
    public int coins;
    
    public TaskGiveCoins(int startDelay, int tickDelay, GameInstance gameInstance, int coins)
    {
        this.startDelay = startDelay;
        this.tickDelay = tickDelay;
        this.gameInstance = gameInstance;
        this.coins = coins;
    }
    
    @Override
    public void run()
    {
        advanceTimer();
    }
    
    public void advanceTimer()
    {
        for (GameTeam gameTeam : gameInstance.teams.values())
        {
            for (GamePlayer player : gameTeam.members)
            {
                player.playerBank.coins += this.coins;
            }
        }
        
        gameInstance.updateScoreboards();
    }
    
    public void finishTimer()
    {
        this.cancel();
    }
}
