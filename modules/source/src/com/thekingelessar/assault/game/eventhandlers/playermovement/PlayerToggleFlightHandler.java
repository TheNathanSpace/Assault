package com.thekingelessar.assault.game.eventhandlers.playermovement;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.teambuffs.BuffDoubleJump;
import com.thekingelessar.assault.game.teambuffs.IBuff;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class PlayerToggleFlightHandler implements Listener
{
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent playerToggleFlightEvent)
    {
        Player player = playerToggleFlightEvent.getPlayer();
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        
        if (playerToggleFlightEvent.isFlying())
        {
            
            if (gameInstance != null)
            {
                GameTeam gameTeam = gameInstance.getPlayerTeam(player);
                if (gameTeam != null)
                {
                    for (IBuff buff : gameTeam.buffList)
                    {
                        if (buff instanceof BuffDoubleJump)
                        {
                            GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
                            if (gamePlayer.flightReset)
                            {
                                Vector yVector = new Vector(0, 0.5, 0);
                                player.setVelocity(player.getLocation().getDirection().multiply(1.3D).add(yVector));
                                player.setAllowFlight(false);
                                
                                gamePlayer.flightReset = false;
                                
                                playerToggleFlightEvent.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}