package com.thekingelessar.assault.game;

import com.thekingelessar.assault.Assault;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public enum PlayerMode
{
    LOBBY(GameMode.ADVENTURE, false, false, false, false),
    SPECTATOR(GameMode.SPECTATOR, false, false, false, false);
    
    public GameMode gameMode;
    public boolean canBreakBlocks;
    public boolean canPlaceBlocks;
    public boolean canBeDamaged;
    public boolean canDamage;
    
    PlayerMode(GameMode gameMode, boolean canBreakBlocks, boolean canPlaceBlocks, boolean canBeDamaged, boolean canDamage)
    {
        this.gameMode = gameMode;
        this.canBreakBlocks = canBreakBlocks;
        this.canPlaceBlocks = canPlaceBlocks;
        this.canBeDamaged = canBeDamaged;
        this.canDamage = canDamage;
    }
    
    public static PlayerMode setPlayerMode(UUID playerUUID, PlayerMode playerMode)
    {
        Player player = Bukkit.getPlayer(playerUUID);
        player.setGameMode(playerMode.gameMode);
        return playerMode;
    }
    
    public static PlayerMode getPlayerMode(Player player)
    {
        for (GameInstance gameInstance : Assault.gameInstances)
        {
            for (HashMap.Entry<UUID, PlayerMode> playerEntry : gameInstance.playerModes.entrySet())
            {
                if (player.getUniqueId().equals(playerEntry.getKey()))
                {
                    return playerEntry.getValue();
                }
            }
        }
        
        return null;
    }
}
