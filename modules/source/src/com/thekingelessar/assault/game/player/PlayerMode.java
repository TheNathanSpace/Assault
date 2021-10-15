package com.thekingelessar.assault.game.player;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public enum PlayerMode
{
    LOBBY(GameMode.ADVENTURE, false, false, false, false, false, false, false),
    SPECTATOR(GameMode.SPECTATOR, false, false, false, false, true, true, false),
    ATTACKING(GameMode.SURVIVAL, true, true, true, true, false, false, true),
    BUILDING(GameMode.SURVIVAL, true, true, true, false, false, false, true),
    BETWEEN(GameMode.SURVIVAL, false, false, false, false, true, false, false),
    HAM(GameMode.CREATIVE, true, true, false, false, true, false, true);
    
    public GameMode gameMode;
    public boolean canBreakBlocks;
    public boolean canPlaceBlocks;
    public boolean canBeDamaged;
    public boolean canDamage;
    
    public boolean canFly;
    public boolean isInvisible;
    
    public boolean canDropItems;
    
    PlayerMode(GameMode gameMode, boolean canBreakBlocks, boolean canPlaceBlocks, boolean canBeDamaged, boolean canDamage, boolean canFly, boolean isInvisible, boolean canDropItems)
    {
        this.gameMode = gameMode;
        this.canBreakBlocks = canBreakBlocks;
        this.canPlaceBlocks = canPlaceBlocks;
        this.canBeDamaged = canBeDamaged;
        this.canDamage = canDamage;
        
        this.canFly = canFly;
        this.isInvisible = isInvisible;
        
        this.canDropItems = canDropItems;
    }
    
    public static PlayerMode setPlayerMode(Player player, PlayerMode playerMode, GameInstance gameInstance)
    {
        player.setGameMode(playerMode.gameMode);
        
        player.setAllowFlight(playerMode.canFly);
        
        if (playerMode.isInvisible)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false));
        }
        else
        {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
        
        gameInstance.playerModes.put(player, playerMode);
        
        return playerMode;
    }
    
    public static PlayerMode getPlayerMode(Player player)
    {
        for (GameInstance gameInstance : Assault.gameInstances)
        {
            for (HashMap.Entry<Player, PlayerMode> playerEntry : gameInstance.playerModes.entrySet())
            {
                if (player.equals(playerEntry.getKey()))
                {
                    return playerEntry.getValue();
                }
            }
        }
        
        return null;
    }
    
    public static PlayerMode getTeamMode(GameTeam gameTeam)
    {
        switch (gameTeam.gameInstance.gameStage)
        {
            case PREGAME:
            case BUILDING:
                return PlayerMode.BUILDING;
            
            case ATTACKING:
                return PlayerMode.ATTACKING;
            
            case FINISHED:
                return PlayerMode.HAM;
            
            default:
                return null;
        }
    }
}
