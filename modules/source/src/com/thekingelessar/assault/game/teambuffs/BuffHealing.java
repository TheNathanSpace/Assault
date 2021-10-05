package com.thekingelessar.assault.game.teambuffs;

import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BuffHealing implements IBuff
{
    public GameTeam gameTeam;
    
    public BuffHealing(GameTeam gameTeam)
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
                if (potionEffect.getType().equals(PotionEffectType.REGENERATION))
                {
                    return;
                }
            }
            
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1000000, 1, true, true));
        }
    }
}
