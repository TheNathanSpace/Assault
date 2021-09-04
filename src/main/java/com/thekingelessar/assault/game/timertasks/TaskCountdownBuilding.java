package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.util.Title;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

public class TaskCountdownBuilding extends BukkitRunnable
{
    public int startTicks;
    
    public int startDelay;
    
    public int tickDelay;
    public int ticksLeft;
    
    public GameInstance gameInstance;
    
    public boolean doneFirst = false;
    
    private Title title = new Title();
    
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
        if (!doneFirst)
        {
            for (Map.Entry<GameTeam, Item> entry : gameInstance.guidingObjectives.entrySet())
            {
                
                Location objectiveLocation = entry.getKey().mapBase.objective.toLocation(gameInstance.gameWorld);
                objectiveLocation.add(0, 0.5, 0);
                
                Vector velocity = entry.getValue().getVelocity();
                velocity.setX(0);
                velocity.setY(0);
                velocity.setZ(0);
                
                entry.getValue().teleport(objectiveLocation);
            }
            
            doneFirst = true;
        }
        
        if (ticksLeft < 20)
        {
            finishTimer();
            return;
        }
        
        if (ticksLeft < (gameInstance.gameMap.buildingTime) * 20 + 1)
        {
            List<Player> players = gameInstance.getPlayers();
            for (Player player : players)
            {
                title.clearTitle(player);
            }
            
            title = new Title(" ", ChatColor.WHITE + "Building finished in " + ChatColor.LIGHT_PURPLE + (ticksLeft / 20) + ChatColor.WHITE + " seconds");
            
            for (Player player : players)
            {
                title.send(player);
            }
        }
        
        gameInstance.buildingSecondsLeft = ticksLeft / 20;
        
        gameInstance.updateScoreboards();
        
        ticksLeft = ticksLeft - tickDelay;
    }
    
    public void finishTimer()
    {
        this.gameInstance.taskCountdownBuilding = null;
        this.gameInstance.gameStage = GameStage.ATTACK_ROUNDS;
        
        this.gameInstance.startAttackMode();
        
        this.cancel();
    }
}
