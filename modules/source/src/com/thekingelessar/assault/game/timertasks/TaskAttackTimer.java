package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.util.version.XSound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskAttackTimer extends BukkitRunnable
{
    public int startTicks;
    
    public int startDelay;
    
    public int tickDelay;
    
    public GameInstance gameInstance;
    
    public boolean doneFirst = false;
    public boolean forfeitAlerted = false;
    
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
        if (gameInstance.modFirstTo5Stars.enabled)
        {
            if (gameInstance.getAttackingTeam().displaySeconds == gameInstance.gameMap.firstToFiveStarsTimeLimit - 16)
            {
                if (!gameInstance.modInfiniteTime.enabled)
                {
                    gameInstance.taskCountdownAttackEnd = new TaskCountdownAttackEnd(300, 0, 20, gameInstance);
                    gameInstance.taskCountdownAttackEnd.runTaskTimer(Assault.INSTANCE, gameInstance.taskCountdownAttackEnd.startDelay, gameInstance.taskCountdownAttackEnd.tickDelay);
                }
            }
        }
        else
        {
            if (gameInstance.getAttackingTeam().displaySeconds == gameInstance.gameMap.attackTimeLimit - 16)
            {
                if (!gameInstance.modInfiniteTime.enabled)
                {
                    gameInstance.taskCountdownAttackEnd = new TaskCountdownAttackEnd(300, 0, 20, gameInstance);
                    gameInstance.taskCountdownAttackEnd.runTaskTimer(Assault.INSTANCE, gameInstance.taskCountdownAttackEnd.startDelay, gameInstance.taskCountdownAttackEnd.tickDelay);
                }
            }
        }
        
        if (!forfeitAlerted)
        {
            if (gameInstance.getAttackingTeam().canForfeit())
            {
                for (Player player : gameInstance.getAttackingTeam().getPlayers())
                {
                    player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_GUITAR.parseSound(), 1.5f, 2.0f);
                }
                forfeitAlerted = true;
            }
        }
        
        double attackingTime = gameInstance.getAttackingTeam().displaySeconds;
        attackingTime += 1.;
        
        gameInstance.attackingTeam.displaySeconds = attackingTime;
        
        gameInstance.updateScoreboards();
    }
    
    public void stopTimer()
    {
        this.gameInstance.taskAttackTimer = null;
        this.gameInstance.taskGiveCoins.cancel();
        this.gameInstance.taskGiveCoins = null;
        
        if (this.gameInstance.taskCountdownAttackEnd != null)
        {
            this.gameInstance.taskCountdownAttackEnd.cancel();
        }
        
        this.cancel();
    }
    
}
