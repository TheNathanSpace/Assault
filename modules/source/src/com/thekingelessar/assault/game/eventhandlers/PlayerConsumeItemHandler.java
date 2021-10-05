package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.shops.ShopAttacking;
import com.thekingelessar.assault.util.version.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class PlayerConsumeItemHandler implements Listener
{
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent playerItemConsumeEvent)
    {
        Player player = playerItemConsumeEvent.getPlayer();
        ItemStack itemStack = playerItemConsumeEvent.getItem();
        
        if (itemStack != null)
        {
            GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
            if (gameInstance != null)
            {
                if (itemStack.getType().equals(XMaterial.POTION.parseMaterial()))
                {
                    Potion potion = Potion.fromItemStack(itemStack);
                    PotionType potionEffect = potion.getType();
                    switch (potionEffect)
                    {
                        case JUMP:
                            player.addPotionEffect(ShopAttacking.JUMP_30_5, true);
                            break;
                        case INVISIBILITY:
                            player.addPotionEffect(ShopAttacking.INVIS_30_1);
                            break;
                    }
                    playerItemConsumeEvent.setCancelled(true);
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(XMaterial.AIR.parseMaterial()));
                }
            }
        }
    }
}
