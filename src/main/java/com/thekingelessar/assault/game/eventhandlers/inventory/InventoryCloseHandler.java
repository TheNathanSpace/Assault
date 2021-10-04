package com.thekingelessar.assault.game.eventhandlers.inventory;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.util.xsupport.XSound;
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
        
        if (inventoryOpen.equals(playerTeam.secretStorage))
        {
            player.playSound(player.getLocation(), XSound.BLOCK_CHEST_CLOSE.parseSound(), 0.5F, 1.3F);
        }
    }
}