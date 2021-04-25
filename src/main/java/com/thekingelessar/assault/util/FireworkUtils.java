package com.thekingelessar.assault.util;

import com.thekingelessar.assault.game.team.TeamColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

// From https://gist.github.com/JorelAli/8e60a30ca133769c4ca1
public class FireworkUtils
{
    
    public static void spawnRandomFirework(final Location oldLocation, TeamColor teamColor)
    {
        Location randomLocation = Util.getRandomNearby(oldLocation, 3.5);
        
        final Firework firework = (Firework) randomLocation.getWorld().spawnEntity(randomLocation, EntityType.FIREWORK);
        final FireworkMeta fireworkMeta = firework.getFireworkMeta();
        
        final Random random = new Random();
        
        final FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(getColor(random.nextInt(3) + 1, teamColor)).withFade(getColor(random.nextInt(3) + 1, teamColor)).with(Type.values()[random.nextInt(Type.values().length)]).trail(random.nextBoolean()).build();
        
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(random.nextInt(2) + 1);
        firework.setFireworkMeta(fireworkMeta);
    }
    
    private static Color getColor(final int i, TeamColor teamColor)
    {
        switch (teamColor)
        {
            case RED:
                switch (i)
                {
                    case 1:
                        return Color.RED;
                    case 2:
                        return Color.MAROON;
                    case 3:
                        return Color.FUCHSIA;
                }
            case BLUE:
                switch (i)
                {
                    case 1:
                        return Color.BLUE;
                    case 2:
                        return Color.NAVY;
                    case 3:
                        return Color.AQUA;
                }
        }
        
        return null;
    }
}