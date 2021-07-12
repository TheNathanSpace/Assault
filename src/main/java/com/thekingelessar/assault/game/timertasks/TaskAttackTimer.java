package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.util.Title;
import com.thekingelessar.assault.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class TaskAttackTimer extends BukkitRunnable
{
    public int startTicks;
    
    public int startDelay;
    
    public int tickDelay;
    
    public GameInstance gameInstance;
    
    public boolean doneFirst = false;
    public boolean forfeitAlerted = false;
    
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
        if (gameInstance.getAttackingTeam().displaySeconds == 464)
        {
            if (!gameInstance.modInfiniteTime.enabled)
            {
                gameInstance.taskCountdownAttackEnd = new TaskCountdownAttackEnd(300, 0, 20, gameInstance);
                gameInstance.taskCountdownAttackEnd.runTaskTimer(Assault.INSTANCE, gameInstance.taskCountdownAttackEnd.startDelay, gameInstance.taskCountdownAttackEnd.tickDelay);
            }
        }
        
        if (!forfeitAlerted)
        {
            if (gameInstance.getAttackingTeam().canForfeit())
            {
                for (Player player : gameInstance.getAttackingTeam().getPlayers())
                {
                    player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1.5f, 2.0f);
                    player.sendRawMessage(Assault.assaultPrefix + gameInstance.getAttackingTeam().color.chatColor + "Your team " + ChatColor.RESET + "can now forfeit! Use /forfeit.");
                }
                forfeitAlerted = true;
            }
        }
        if (!doneFirst)
        {
            for (Map.Entry<GameTeam, Item> entry : gameInstance.currentObjective.entrySet())
            {
                
                Location objectiveLocation = entry.getKey().mapBase.objective.toLocation(gameInstance.gameWorld);
                objectiveLocation.add(0, 0.5, 0);
                
                Vector velocity = entry.getValue().getVelocity();
                velocity.setX(0);
                velocity.setY(0);
                velocity.setZ(0);
                entry.getValue().setVelocity(velocity);
                
                entry.getValue().teleport(objectiveLocation);
            }
            
            doneFirst = true;
        }
        
        double attackingTime = gameInstance.getAttackingTeam().displaySeconds;
        attackingTime += 1.;
        
        gameInstance.teams.get(gameInstance.attackingTeam).displaySeconds = attackingTime;
        
        gameInstance.updateScoreboards();
    }
    
    public void stopTimer()
    {
        this.gameInstance.currentObjective = new HashMap<>();
        
        this.gameInstance.taskAttackTimer = null;
        this.gameInstance.taskGiveCoins.cancel();
        this.gameInstance.taskGiveCoins = null;
        
        if (this.gameInstance.taskCountdownAttackEnd != null)
        {
            this.gameInstance.taskCountdownAttackEnd.cancel();
        }
        
        if (this.gameInstance.getAttackingTeam().finalAttackingTime != 480)
        {
            long nanosecondsTaken = System.nanoTime() - this.gameInstance.getAttackingTeam().startAttackingTime;
            this.gameInstance.getAttackingTeam().finalAttackingTime = nanosecondsTaken / 1000000000.;
        }
        
        this.gameInstance.getAttackingTeam().displaySeconds = Util.round(gameInstance.getAttackingTeam().finalAttackingTime, 2);
        
        this.cancel();
    }
    
}
