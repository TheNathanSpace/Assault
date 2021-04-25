package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.util.FireworkUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static com.thekingelessar.assault.Assault.lobbyWorld;

public class TaskCountdownGameEnd extends BukkitRunnable
{
    public int startTicks;
    public int startDelay;
    
    public int tickDelay;
    public int ticksLeft;
    
    public GameInstance gameInstance;
    
    public TaskCountdownGameEnd(int startTicks, int startDelay, int tickDelay, GameInstance gameInstance)
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
        
        List<Player> players = gameInstance.getWinningTeam().getPlayers();
        for (Player player : players)
        {
            player.sendRawMessage("§c§lC§lo§6§ln§lg§e§lr§la§a§lt§lu§9§ll§la§b§lt§li§5§lo§c§ln§ls§6§l! §r§dYour team wins!");
            for (int i = 0; i < 5; i++)
            {
                FireworkUtils.spawnRandomFirework(player.getLocation(), gameInstance.getWinningTeam().color);
            }
        }
        
        ticksLeft = ticksLeft - tickDelay;
    }
    
    public void finishTimer()
    {
        this.gameInstance.taskCountdownGameEnd = null;
        this.gameInstance.endGame();
        this.cancel();
    }
}
