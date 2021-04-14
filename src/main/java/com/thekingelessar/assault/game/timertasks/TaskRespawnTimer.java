package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.util.Title;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskRespawnTimer extends BukkitRunnable
{
    public int startTicks;
    public int startDelay;
    
    public int tickDelay;
    public int ticksLeft;
    
    public GameInstance gameInstance;
    public Player player;
    
    Title title;
    
    public TaskRespawnTimer(int startTicks, int startDelay, int tickDelay, GameInstance gameInstance, Player player)
    {
        this.startTicks = startTicks;
        this.startDelay = startDelay;
        this.tickDelay = tickDelay;
        this.ticksLeft = this.startTicks;
        this.gameInstance = gameInstance;
        this.player = player;
        
        this.title = new Title();
    }
    
    @Override
    public void run()
    {
        advanceTimer();
    }
    
    public void advanceTimer()
    {
        if (ticksLeft < 21)
        {
            finishTimer();
            return;
        }
        
        title.clearTitle(player);
        
        String initialTitle = "You have " + ChatColor.RED + "died" + ChatColor.RESET + "!";
        
        title = new Title(initialTitle, "Respawning in " + ChatColor.LIGHT_PURPLE + (ticksLeft / 20) + ChatColor.WHITE + " seconds");
        
        title.send(player);
        
        ticksLeft = ticksLeft - tickDelay;
    }
    
    public void finishTimer()
    {
        title.clearTitle(player);
        
        this.cancel();
        
        GameTeam playerTeam = gameInstance.getPlayerTeam(player);
        PlayerMode mode = PlayerMode.setPlayerMode(player.getUniqueId(), PlayerMode.PLAYER);
        
        player.teleport(gameInstance.gameMap.getSpawn(playerTeam, null).toLocation(gameInstance.gameWorld));
        player.setHealth(player.getMaxHealth());
    }
}
