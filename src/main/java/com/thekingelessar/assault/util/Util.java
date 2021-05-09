package com.thekingelessar.assault.util;

import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.game.team.TeamStage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.*;

public class Util
{
    
    public static Location getRandomNearby(Location location, double blocks)
    {
        int blocks2 = (int) blocks * 100;
        
        double newX = (new Random().nextInt(blocks2) + 1) / 100.;
        if (new Random().nextInt(2) + 1 == 2)
        {
            newX = newX * -1;
        }
        newX += location.getX();
        
        double newY = (new Random().nextInt(blocks2) + 1) / 100.;
        if (new Random().nextInt(2) + 1 == 2)
        {
            newY = newY * -1;
        }
        newY += location.getY();
        
        double newZ = (new Random().nextInt(blocks2) + 1) / 100.;
        if (new Random().nextInt(2) + 1 == 2)
        {
            newZ = newZ * -1;
        }
        newZ += location.getZ();
        
        return new Location(location.getWorld(), newX, newY, newZ);
    }
    
    // From https://bukkit.org/threads/get-blocks-between-two-locations.262499/
    public static List<Block> selectBoundingBox(Location loc1, Location loc2, World w)
    {
        
        List<Block> blocks = new ArrayList<Block>();
        
        int x1 = loc1.getBlockX();
        int y1 = loc1.getBlockY();
        int z1 = loc1.getBlockZ();
        
        int x2 = loc2.getBlockX();
        int y2 = loc2.getBlockY();
        int z2 = loc2.getBlockZ();
        
        int xMin, yMin, zMin;
        int xMax, yMax, zMax;
        int x, y, z;
        
        if (x1 > x2)
        {
            xMin = x2;
            xMax = x1;
        }
        else
        {
            xMin = x1;
            xMax = x2;
        }
        
        if (y1 > y2)
        {
            yMin = y2;
            yMax = y1;
        }
        else
        {
            yMin = y1;
            yMax = y2;
        }
        
        if (z1 > z2)
        {
            zMin = z2;
            zMax = z1;
        }
        else
        {
            zMin = z1;
            zMax = z2;
        }
        
        for (x = xMin; x <= xMax; x++)
        {
            for (y = yMin; y <= yMax; y++)
            {
                for (z = zMin; z <= zMax; z++)
                {
                    Block b = new Location(w, x, y, z).getBlock();
                    blocks.add(b);
                }
            }
        }
        
        return blocks;
    }
    
    public static String secondsToMinutes(double secondsRaw, boolean trim)
    {
        int minute = (int) secondsRaw / 60;
        double seconds = round(secondsRaw % 60, 2);
        
        String stringSeconds;
        
        if (trim)
        {
            stringSeconds = Integer.toString((int) seconds);
        }
        else
        {
            stringSeconds = Double.toString(seconds);
        }
        
        if (seconds == 0)
        {
            stringSeconds = "00";
        }
        else if (seconds < 10)
        {
            stringSeconds = "0" + stringSeconds;
        }
        
        if (minute == 0 && !trim)
        {
            return stringSeconds;
        }
        else
        {
            return String.format("%d:%s", minute, stringSeconds);
        }
    }
    
    public static double round(double value, int precision)
    {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
    
    public static boolean isOnCarpet(Player player)
    {
        Block block = player.getLocation().getBlock();
        return block.getType().equals(Material.CARPET);
    }
    
    public static boolean isOnCarpet(Location location)
    {
        Block block = location.getBlock();
        return block.getType().equals(Material.CARPET);
    }
    
    public static List<TeamColor> getCarpetColor(Player player)
    {
        Block block = player.getLocation().getBlock();
        MaterialData materialData = block.getState().getData();
        return TeamColor.findByDataValue(materialData.getData());
    }
    
    public static List<TeamColor> getCarpetColor(Location location)
    {
        Block block = location.getBlock();
        MaterialData materialData = block.getState().getData();
        return TeamColor.findByDataValue(materialData.getData());
    }
    
    /*
     * From https://bukkit.org/threads/how-to-check-if-a-player-is-between-two-areas.78028/#post-1140505
     * Check if Location testedLocation is within the cuboid with corners l1 and l2.
     * l1 and l2 can be any two (opposite) corners of the cuboid.
     */
    public static boolean isInside(Location testedLocation, Location l1, Location l2)
    {
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
        
        return testedLocation.getX() >= x1 && testedLocation.getX() <= x2 && testedLocation.getY() >= y1 && testedLocation.getY() <= y2 && testedLocation.getZ() >= z1 && testedLocation.getZ() <= z2;
    }
    
    public static boolean blockLocationsEqual(Location loc1, Location loc2)
    {
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }
}
