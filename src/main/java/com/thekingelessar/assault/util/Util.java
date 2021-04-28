package com.thekingelessar.assault.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    
    public static String secondsToMinutes(int secondsRaw)
    {
        int minute = (int) secondsRaw / 60;
        int seconds = secondsRaw % 60;
        
        String stringSeconds = Integer.toString(seconds);
        if (seconds == 0)
        {
            stringSeconds = "00";
        }
        else if (seconds < 10)
        {
            stringSeconds = "0" + stringSeconds;
        }
        
        return String.format("%d:%s", minute, stringSeconds);
    }
    
    public static double round(double value, int precision)
    {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
