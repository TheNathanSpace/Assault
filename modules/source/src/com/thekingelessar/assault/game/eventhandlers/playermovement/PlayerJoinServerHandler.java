package com.thekingelessar.assault.game.eventhandlers.playermovement;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.database.AssaultTableManager;
import com.thekingelessar.assault.lobby.LobbyUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerJoinServerHandler implements Listener
{
    @EventHandler
    public void onPlayerJoinServer(PlayerJoinEvent playerJoinEvent)
    {
        Player player = playerJoinEvent.getPlayer();
        LobbyUtil.joinLobby(player);
        AssaultTableManager.getInstance().insertPlayer(player);
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent)
    {
        if (playerRespawnEvent.getRespawnLocation().getWorld().equals(Assault.lobbyWorld))
        {
            playerRespawnEvent.setRespawnLocation(Assault.lobbySpawn);
            LobbyUtil.joinLobby(playerRespawnEvent.getPlayer());
        }
    }
    
}
