package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.util.Title;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskCountdownRespawn extends BukkitRunnable
{
    public int startTicks;
    public int startDelay;
    
    public int tickDelay;
    public int ticksLeft;
    
    public GameInstance gameInstance;
    public Player player;
    
    Title title;
    
    public TaskCountdownRespawn(int startTicks, int startDelay, int tickDelay, GameInstance gameInstance, Player player)
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
        if (ticksLeft < 20)
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
        
        GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
        
        System.out.println("--- RESPAWNING ---");
        System.out.println("Player name: " + gamePlayer.player.getName());
        System.out.println("Player team: " + gamePlayer.gameTeam.color);
        gamePlayer.spawn(PlayerMode.ATTACKING);  // todo: nullpointer
        
        gamePlayer.taskCountdownRespawn = null;
        
        this.cancel();
    }
}
