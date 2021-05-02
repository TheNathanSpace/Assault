package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatHandler implements Listener
{
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent playerChatEvent)
    {
        Player player = playerChatEvent.getPlayer();
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        if (gameInstance != null)
        {
            GameTeam gameTeam = gameInstance.getPlayerTeam(player);
            if (gameTeam != null)
            {
                String format = ChatColor.GREEN + "[TEAM] " + gameTeam.color.chatColor + "[" + gameTeam.color.getFormattedName(false, false, null) + gameTeam.color.chatColor + "]§r %1$s§r: %2$s";
                String message = String.format(format, player.getName(), playerChatEvent.getMessage());
                
                for (Player teamPlayer : gameTeam.getPlayers())
                {
                    teamPlayer.sendMessage(message);
                }
                
                playerChatEvent.setCancelled(true);
            }
        }
    }
}