package com.thekingelessar.assault.game.inventory.teambuffs;

import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BuffHaste implements IBuff
{
    public GameTeam gameTeam;
    
    public BuffHaste(GameTeam gameTeam)
    {
        this.gameTeam = gameTeam;
    }
    
    @Override
    public void doEffect()
    {
        for (Player player : gameTeam.getPlayers())
        {
            for (PotionEffect potionEffect : player.getActivePotionEffects())
            {
                if (potionEffect.getType().equals(PotionEffectType.FAST_DIGGING))
                {
                    return;
                }
            }
            
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1000000, 1, true, true));
        }
    }
}
