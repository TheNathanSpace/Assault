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
    
        final FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(getColor(teamColor)).withFade(getColor(teamColor)).with(Type.values()[random.nextInt(Type.values().length)]).trail(random.nextBoolean()).build();
        
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(random.nextInt(2) + 1);
        firework.setFireworkMeta(fireworkMeta);
    }
    
    private static Color getColor(TeamColor teamColor)
    {
        final Random random = new Random();
        int randomInt;
        
        switch (teamColor)
        {
            case RED:
                randomInt = random.nextInt(3) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.RED;
                    case 2:
                        return Color.MAROON;
                    case 3:
                        return Color.FUCHSIA;
                }
            case BLACK:
                randomInt = random.nextInt(2) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.BLACK;
                    case 2:
                        return Color.GRAY;
                }
                break;
            case DARK_BLUE:
                randomInt = random.nextInt(3) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.BLUE;
                    case 2:
                        return Color.NAVY;
                    case 3:
                        return Color.AQUA;
                }
                break;
            case DARK_GREEN:
                randomInt = random.nextInt(4) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.GREEN;
                    case 2:
                        return Color.LIME;
                    case 3:
                        return Color.TEAL;
                    case 4:
                        return Color.OLIVE;
                }
                break;
            case DARK_AQUA:
                randomInt = random.nextInt(3) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.TEAL;
                    case 2:
                        return Color.AQUA;
                    case 3:
                        return Color.BLUE;
                }
                break;
            case DARK_RED:
                randomInt = random.nextInt(3) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.RED;
                    case 2:
                        return Color.MAROON;
                    case 3:
                        return Color.FUCHSIA;
                }
            case DARK_PURPLE:
                randomInt = random.nextInt(4) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.RED;
                    case 2:
                        return Color.MAROON;
                    case 3:
                        return Color.PURPLE;
                    case 4:
                        return Color.FUCHSIA;
                }
            case GOLD:
                randomInt = random.nextInt(2) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.ORANGE;
                    case 2:
                        return Color.YELLOW;
                }
            case GRAY:
                randomInt = random.nextInt(3) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.SILVER;
                    case 2:
                        return Color.GRAY;
                    case 3:
                        return Color.WHITE;
                }
            case DARK_GRAY:
                randomInt = random.nextInt(3) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.SILVER;
                    case 2:
                        return Color.GRAY;
                    case 3:
                        return Color.BLACK;
                }
            case BLUE:
                randomInt = random.nextInt(3) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.BLUE;
                    case 2:
                        return Color.NAVY;
                    case 3:
                        return Color.AQUA;
                }
            case GREEN:
                randomInt = random.nextInt(2) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.GREEN;
                    case 2:
                        return Color.LIME;
                }
            case AQUA:
                randomInt = random.nextInt(2) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.AQUA;
                    case 2:
                        return Color.TEAL;
                }
            case LIGHT_PURPLE:
                randomInt = random.nextInt(3) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.FUCHSIA;
                    case 2:
                        return Color.PURPLE;
                    case 3:
                        return Color.RED;
                }
            case YELLOW:
                randomInt = random.nextInt(3) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.YELLOW;
                    case 2:
                        return Color.ORANGE;
                    case 3:
                        return Color.WHITE;
                }
            case WHITE:
                randomInt = random.nextInt(2) + 1;
                switch (randomInt)
                {
                    case 1:
                        return Color.WHITE;
                    case 2:
                        return Color.SILVER;
                }
                
                return null;
        }
    }