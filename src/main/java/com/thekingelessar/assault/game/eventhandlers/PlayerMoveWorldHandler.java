package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
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
//        World worldFrom = playerChangedWorldEvent.getFrom();
//
//        for (GameInstance gameInstance : Assault.gameInstances)
//        {
//            if (worldFrom.equals(gameInstance.gameWorld))
//            {
//                if (gameInstance.gameStage.equals(GameStage.PREGAME))
//                {
//                    gameInstance.removePlayerPreGame(player);
//                }
//            }
//        }
        
        if (world.equals(Assault.lobbyWorld))
        {
            LobbyUtil.joinLobby(player);
        }
    }
}
