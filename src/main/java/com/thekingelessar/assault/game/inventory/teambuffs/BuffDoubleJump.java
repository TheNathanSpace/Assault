package com.thekingelessar.assault.game.inventory.teambuffs;

import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.entity.Player;

public class BuffDoubleJump implements IBuff
{
    public GameTeam gameTeam;
    
    public BuffDoubleJump(GameTeam gameTeam)
    {
        this.gameTeam = gameTeam;
    }
    
    @Override
    public void doEffect()
    {
        for (Player player : gameTeam.getPlayers())
        {
            GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
            if (gamePlayer.flightReset && !player.getAllowFlight())
            {
                player.setAllowFlight(true);
            }
        }
    }
}
