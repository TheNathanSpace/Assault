package com.thekingelessar.assault.game.eventhandlers.combat;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.shops.ShopAttack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PotionThrowHandler implements Listener
{
    @EventHandler
    public void onPotionThrown(ProjectileLaunchEvent projectileLaunchEvent)
    {
        ProjectileSource thrower = projectileLaunchEvent.getEntity().getShooter();
        if (!(thrower instanceof Player)) return;
        Player throwerPlayer = (Player) thrower;
        
        if (!(projectileLaunchEvent.getEntity() instanceof ThrownPotion)) return;
        ThrownPotion thrownPotion = (ThrownPotion) projectileLaunchEvent.getEntity();
        
        if (thrownPotion != null)
        {
            GameInstance gameInstance = GameInstance.getWorldGameInstance(thrownPotion.getWorld());
            if (gameInstance != null)
            {
                if (gameInstance.fixedPotions.contains(thrownPotion)) return;
                
                Collection<PotionEffect> potionEffects = thrownPotion.getEffects();
                List<PotionEffect> potionEffectList = new ArrayList<>(potionEffects);
                
                switch (PotionType.getByEffect(potionEffectList.get(0).getType())) // todo: nothing
                {
                    case POISON:
                        projectileLaunchEvent.setCancelled(true);
                        
                        Potion potion = new Potion(PotionType.POISON, 1);
                        potion.setSplash(true);
                        potion.setType(PotionType.POISON);
                        
                        ItemStack newPotionItemStack = new ItemStack(Material.POTION);
                        potion.apply(newPotionItemStack);
                        
                        ItemMeta newPotionItemMeta = newPotionItemStack.getItemMeta();
                        PotionMeta potionMeta = (PotionMeta) newPotionItemMeta;
                        potionMeta.setMainEffect(PotionEffectType.POISON);
                        
                        potionMeta.addCustomEffect(ShopAttack.POISON_6_2, true);
                        newPotionItemStack.setItemMeta(potionMeta);
                        
                        ThrownPotion newPotion = throwerPlayer.launchProjectile(ThrownPotion.class);
                        newPotion.setItem(newPotionItemStack);
                        gameInstance.fixedPotions.add(newPotion);
                        break;
                }
            }
        }
    }
    
    @EventHandler
    public void onPotionSplash(PotionSplashEvent potionSplashEvent)
    {
        ThrownPotion thrownPotion = potionSplashEvent.getEntity();
        
        if (thrownPotion != null)
        {
            GameInstance gameInstance = GameInstance.getWorldGameInstance(thrownPotion.getWorld());
            if (gameInstance != null)
            {
                gameInstance.fixedPotions.remove(thrownPotion);
            }
        }
    }
    
}
