package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.lobby.LobbyUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerMoveWorldHandler implements Listener
{
    @EventHandler
    public void onPlayerJoinWorld(PlayerChangedWorldEvent playerChangedWorldEvent)
    {
        Player player = playerChangedWorldEvent.getPlayer();
        World world = playerChangedWorldEvent.getPlayer().getWorld();
        
        if (world.equals(Assault.lobbyWorld))
        {
            LobbyUtil.joinLobby(player);
        }
    }
}
