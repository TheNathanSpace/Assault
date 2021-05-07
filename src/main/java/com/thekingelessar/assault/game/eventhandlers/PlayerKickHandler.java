package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.DeathType;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKickHandler implements Listener
{
    @EventHandler
    public void onPlayerKick(PlayerKickEvent playerKickEvent)
    {
        Player player = playerKickEvent.getPlayer();
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        
        if (gameInstance != null)
        {
            GameTeam gameTeam = gameInstance.getPlayerTeam(player);
            GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
            gamePlayer.respawn(null, true, DeathType.DEATH);
            playerKickEvent.setCancelled(true);
        }
    }
}
