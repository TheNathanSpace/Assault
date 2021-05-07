package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class InventoryCloseHandler implements Listener
{
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent)
    {
        Player player = (Player) inventoryCloseEvent.getPlayer();
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        
        if (gameInstance == null)
        {
            return;
        }
        
        if (gameInstance.gameStage.equals(GameStage.PREGAME))
        {
            return;
        }
        
        GameTeam playerTeam = gameInstance.getPlayerTeam(player);
        GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
        
        Inventory inventoryOpen = inventoryCloseEvent.getInventory();
        
        if (inventoryOpen.equals(playerTeam.shopAttacking.secretStorage))
        {
            player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 0.5F, 1.3F);
        }
    }
}