package com.thekingelessar.assault.game.timertasks;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.util.Util;
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
    
    public TaskCountdownRespawn(int startTicks, int startDelay, int tickDelay, GameInstance gameInstance, Player player)
    {
        this.startTicks = startTicks;
        this.startDelay = startDelay;
        this.tickDelay = tickDelay;
        this.ticksLeft = this.startTicks;
        this.gameInstance = gameInstance;
        this.player = player;
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
        
        String initialTitle = "You have " + ChatColor.DARK_RED + "died" + Util.RESET_CHAT + "!";
        
        player.sendTitle(initialTitle, "Respawning in " + ChatColor.LIGHT_PURPLE + (ticksLeft / 20) + ChatColor.WHITE + " seconds");
        
        ticksLeft = ticksLeft - tickDelay;
    }
    
    public void finishTimer()
    {
        player.resetTitle();
        
        GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
        
        System.out.println("--- RESPAWNING ---");
        System.out.println("Player name: " + gamePlayer.player.getName());
        System.out.println("Player team: " + gamePlayer.gameTeam.color);
        gamePlayer.spawn(PlayerMode.ATTACKING, false);  // todo: nullpointer
        
        gamePlayer.taskCountdownRespawn = null;
        
        this.cancel();
    }
}
