package com.thekingelessar.assault.game.player;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.util.Util;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GamePlayer
{
    public Player player;
    public GameInstance gameInstance;
    public PlayerBank playerBank;
    public FastBoard scoreboard;
    
    public List<ItemStack> spawnItems = new ArrayList<>();
    public List<ItemStack> spawnArmor = new ArrayList<>();
    
    public GamePlayer(Player player, GameInstance gameInstance)
    {
        this.player = player;
        this.gameInstance = gameInstance;
        this.playerBank = new PlayerBank(0, 0);
        this.scoreboard = new FastBoard(player);
        
        spawnArmor.add(new ItemStack(Material.LEATHER_HELMET));
        spawnArmor.add(new ItemStack(Material.LEATHER_CHESTPLATE));
        spawnArmor.add(new ItemStack(Material.LEATHER_LEGGINGS));
        spawnArmor.add(new ItemStack(Material.LEATHER_BOOTS));
        spawnItems.add(new ItemStack(Material.WOOD_SWORD));
    }
    
    public void addSpawnItem(ItemStack spawnItem)
    {
        spawnItems.add(spawnItem);
    }
    
    public void respawn()
    {
        GameTeam playerTeam = gameInstance.getPlayerTeam(player);
        PlayerMode mode = PlayerMode.setPlayerMode(player, PlayerMode.ATTACKING, gameInstance);
        
        player.teleport(gameInstance.gameMap.getSpawn(playerTeam, null).toLocation(gameInstance.gameWorld));
        player.setHealth(player.getMaxHealth());
        
        player.getInventory().clear();
        
        for (ItemStack itemStack : spawnArmor)
        {
            player.getInventory().setArmorContents(spawnArmor.toArray(new ItemStack[0]));
        }
        
        for (ItemStack itemStack : spawnItems)
        {
            player.getInventory().addItem(itemStack);
        }
    }
    
    public void updateScoreboard()
    {
        scoreboard.updateTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Assault");
        
        List<String> lines = new ArrayList<>();
        
        lines.add("");
        
        lines.add("Your team: " + ChatColor.BOLD + gameInstance.getPlayerTeam(player).color.getFormattedName(false) + ChatColor.RESET);
        
        lines.add("");
        
        if (gameInstance.gameStage.equals(GameStage.BUILDING_BASE))
        {
            lines.add(ChatColor.WHITE.toString() + ChatColor.BOLD + "Building time: " + ChatColor.RESET + Util.secondsToMinutes(gameInstance.buildingSecondsLeft));
        }
        else
        {
            lines.add(ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE" + ChatColor.RESET + ": " + Util.secondsToMinutes(gameInstance.teams.get(TeamColor.BLUE).displaySeconds));
            lines.add(ChatColor.RED.toString() + ChatColor.BOLD + "RED" + ChatColor.RESET + ": " + Util.secondsToMinutes(gameInstance.teams.get(TeamColor.RED).displaySeconds));
        }
        
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
                // todo: purchase with emeralds
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
