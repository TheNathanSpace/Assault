package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.lobby.LobbyUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerRightClickHandler implements Listener
{
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent playerInteractEvent)
    {
        Player player = playerInteractEvent.getPlayer();
        ItemStack itemStack = playerInteractEvent.getItem();
        
        if (itemStack != null)
        {
            if (itemStack.equals(LobbyUtil.joinGameStar))
            {
                LobbyUtil.joinQueue(player);
                return;
            }
            
            if (itemStack.getType().equals(Material.BARRIER))
            {
                LobbyUtil.leaveQueue(player);
                return;
            }
            
            if (itemStack.getType().equals(Material.BOOK))
            {
                LobbyUtil.sendRules(player);
                return;
            }
        }
    }
}
