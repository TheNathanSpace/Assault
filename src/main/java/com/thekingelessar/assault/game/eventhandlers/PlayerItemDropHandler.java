package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.lobby.LobbyUtil;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerItemDropHandler implements Listener
{
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent playerDropItemEvent)
    {
        Player player = playerDropItemEvent.getPlayer();
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        
        ItemStack itemStack = playerDropItemEvent.getItemDrop().getItemStack();
        
        if (itemStack != null)
        {
            if (itemStack.equals(LobbyUtil.joinGameStar))
            {
                playerDropItemEvent.setCancelled(true);
                return;
            }
        }
        
        if (gameInstance != null)
        {
            PlayerMode playerMode = gameInstance.getPlayerMode(player);
            if (!playerMode.canDropItems)
            {
                playerDropItemEvent.setCancelled(true);
            }
        }
    }
}