package com.thekingelessar.assault.game.player;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.Currency;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GamePlayer
{
    public Player player;
    public GameInstance gameInstance;
    public PlayerBank playerBank;
    public FastBoard scoreboard;
    
    public GamePlayer(Player player, GameInstance gameInstance)
    {
        this.player = player;
        this.gameInstance = gameInstance;
        this.playerBank = new PlayerBank(0, 0);
        this.scoreboard = new FastBoard(player);
        
    }
    
    public void updateScoreboard()
    {
        scoreboard.updateTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Assault");
        
        List<String> lines = new ArrayList<>();
        
        lines.add("");
        
        lines.add("Your team: " + ChatColor.BOLD + gameInstance.getPlayerTeam(player).color.getFormattedName(false) + ChatColor.RESET);
        
        lines.add("");
        
        lines.add(ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE" + ChatColor.RESET + ": "); // todo: times
        lines.add(ChatColor.RED.toString() + ChatColor.BOLD + "RED" + ChatColor.RESET + ": ");
        
        lines.add("");
        
        lines.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "Coins" + ChatColor.RESET + ": " + playerBank.coins);
        lines.add(ChatColor.AQUA.toString() + ChatColor.BOLD + "Epic gamer points" + ChatColor.RESET + ": " + playerBank.gamerPoints);
        
        lines.add("");
        
        scoreboard.updateLines(lines);
    }
    
    public boolean purchaseItem(int cost, Currency currency)
    {
        boolean hasEnough = true;
        switch (currency)
        {
            case COINS:
                if (cost <= playerBank.coins)
                {
                    playerBank.coins -= cost;
                    return true;
                }
                hasEnough = false;
            case EMERALDS:
                break;
            case GAMER_POINTS:
                if (cost <= playerBank.gamerPoints)
                {
                    playerBank.gamerPoints -= cost;
                    return true;
                }
                hasEnough = false;
            
        }
        
        if (!hasEnough)
        {
            player.playSound(player.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
        }
        return false;
    }
}
