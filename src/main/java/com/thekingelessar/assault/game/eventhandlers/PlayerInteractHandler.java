package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.lobby.LobbyUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractHandler implements Listener
{
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent)
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
            
            if (itemStack.equals(LobbyUtil.leaveBarrier))
            {
                LobbyUtil.leaveQueue(player);
                return;
            }
            
            if (itemStack.equals(LobbyUtil.rulesBook))
            {
                LobbyUtil.sendRules(player);
                return;
            }
            
            GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
            if (gameInstance != null)
            {
                if (itemStack.equals(GameInstance.gameModifierItemStack))
                {
                    player.openInventory(gameInstance.modifierShopMap.get(player).inventory);
                }
            }
        }
    }
}
