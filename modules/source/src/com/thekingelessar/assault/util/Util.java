package com.thekingelessar.assault.util;

import com.thekingelessar.assault.game.inventory.shopitems.ShopItemArmor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Util
{
    
    public static final String RESET_CHAT = ChatColor.RESET + ChatColor.WHITE.toString();
    
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
    
    public static Material getRandomMaterial()
    {
        Material[] materials = Material.values();
        Random generator = new Random();
        Material randomMaterial = materials[generator.nextInt(materials.length)];
        while (randomMaterial.isEdible() || randomMaterial.isRecord())
        {
            randomMaterial = materials[generator.nextInt(materials.length)];
        }
        
        return randomMaterial;
    }
    
    public static ItemStack getRandomItemStack()
    {
        Material randomMaterial = Util.getRandomMaterial();
        int count = new Random().nextInt(64) + 1;
        ItemStack randomItemStack = new ItemStack(randomMaterial, count);
        
        double stdDeviation = 4.5;
        double mean = 24;
        
        randomItemStack.setAmount(Util.weightedInt(stdDeviation, mean, 1, 64));
        
        return randomItemStack;
    }
    
    // Inclusive bounds
    public static int weightedInt(double stdDeviation, double mean, int lower, int upper)
    {
        RandomCollection<Object> randomCollection = new RandomCollection<>();
        for (int i = lower; i <= upper; i++)
        {
            randomCollection.add(Util.gaussianCurve(i, stdDeviation, mean), i);
        }
        return (int) randomCollection.next();
    }
    
    public static double gaussianCurve(double x, double stdDeviation, double mean)
    {
        double variance = stdDeviation * stdDeviation;
        return Math.pow(Math.exp(-(((x - mean) * (x - mean)) / ((2 * variance)))), 1 / (stdDeviation * Math.sqrt(2 * Math.PI)));
    }
    
    public static class RandomCollection<E>
    {
        private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
        private final Random random;
        private double total = 0;
        
        public RandomCollection()
        {
            this(new Random());
        }
        
        public RandomCollection(Random random)
        {
            this.random = random;
        }
        
        public RandomCollection<E> add(double weight, E result)
        {
            if (weight <= 0) return this;
            total += weight;
            map.put(total, result);
            return this;
        }
        
        public E next()
        {
            double value = random.nextDouble() * total;
            return map.higherEntry(value).getValue();
        }
    }
    
    public static List<ItemStack> sortArmor(List<ItemStack> armorList)
    {
        ItemStack helmet = null;
        ItemStack chestplate = null;
        ItemStack leggings = null;
        ItemStack boots = null;
        
        for (ItemStack armorPiece : armorList)
        {
            if (ShopItemArmor.HELMETS.contains(armorPiece.getType()))
            {
                helmet = armorPiece;
            }
            if (ShopItemArmor.CHESTPLATES.contains(armorPiece.getType()))
            {
                chestplate = armorPiece;
            }
            if (ShopItemArmor.LEGGINGS.contains(armorPiece.getType()))
            {
                leggings = armorPiece;
            }
            if (ShopItemArmor.BOOTS.contains(armorPiece.getType()))
            {
                boots = armorPiece;
            }
        }
        
        return Arrays.asList(boots, leggings, chestplate, helmet);
    }
    
    public static List<Block> getTouchingBlocks(Player player)
    {
        Location playerLocation = player.getLocation();
        List<Block> touchingBlocks = new ArrayList<>();
        touchingBlocks.add(playerLocation.getBlock());
        
        Location headLocation = playerLocation.clone();
        headLocation.setY(playerLocation.getY() + 1);
        touchingBlocks.add(headLocation.getBlock());
        
        if (playerLocation.getX() - (long) playerLocation.getX() != 0)
        {
            double difference = playerLocation.getX() - (long) playerLocation.getX();
            if (Math.abs(difference) > 0.6)
            {
                Location extendedX = playerLocation.clone();
                if (difference < 0)
                {
                    extendedX.setX(Math.floor(playerLocation.getX() - 1));
                }
                else
                {
                    extendedX.setX(Math.ceil(playerLocation.getX() + 1));
                }
                Block otherX = playerLocation.getWorld().getBlockAt(extendedX);
                if (!Util.isBlockInList(touchingBlocks, otherX))
                {
                    touchingBlocks.add(otherX);
                }
            }
            else if (Math.abs(difference) < 0.4)
            {
                Location extendedX = playerLocation.clone();
                if (difference < 0)
                {
                    extendedX.setX(Math.ceil(playerLocation.getX()));
                }
                else
                {
                    extendedX.setX(Math.floor(playerLocation.getX()));
                }
                Block otherX = playerLocation.getWorld().getBlockAt(extendedX);
                if (!Util.isBlockInList(touchingBlocks, otherX))
                {
                    touchingBlocks.add(otherX);
                }
            }
        }
        
        if (playerLocation.getZ() - (long) playerLocation.getZ() != 0)
        {
            double difference = playerLocation.getZ() - (long) playerLocation.getZ();
            if (Math.abs(difference) > 0.6)
            {
                Location extendedZ = playerLocation.clone();
                if (difference < 0)
                {
                    extendedZ.setZ(Math.floor(playerLocation.getZ() - 1));
                }
                else
                {
                    extendedZ.setZ(Math.ceil(playerLocation.getZ() + 1));
                }
                Block otherZ = playerLocation.getWorld().getBlockAt(extendedZ);
                if (!Util.isBlockInList(touchingBlocks, otherZ))
                {
                    touchingBlocks.add(otherZ);
                }
            }
            else if (Math.abs(difference) < 0.4)
            {
                Location extendedZ = playerLocation.clone();
                if (difference < 0)
                {
                    extendedZ.setZ(Math.ceil(playerLocation.getZ() + 1));
                }
                else
                {
                    extendedZ.setZ(Math.floor(playerLocation.getZ() - 1));
                }
                Block otherZ = playerLocation.getWorld().getBlockAt(extendedZ);
                if (!Util.isBlockInList(touchingBlocks, otherZ))
                {
                    touchingBlocks.add(otherZ);
                }
            }
        }
        
        if (playerLocation.getX() - (long) playerLocation.getX() != 0)
        {
            double difference = playerLocation.getX() - (long) playerLocation.getX();
            if (Math.abs(difference) > 0.6)
            {
                Location extendedX = playerLocation.clone();
                if (difference < 0)
                {
                    extendedX.setX(Math.floor(playerLocation.getX() - 1));
                }
                else
                {
                    extendedX.setX(Math.ceil(playerLocation.getX() + 1));
                }
                Block otherX = playerLocation.getWorld().getBlockAt(extendedX);
                if (!Util.isBlockInList(touchingBlocks, otherX))
                {
                    touchingBlocks.add(otherX);
                }
            }
            else if (Math.abs(difference) < 0.4)
            {
                Location extendedX = playerLocation.clone();
                if (difference < 0)
                {
                    extendedX.setX(Math.ceil(playerLocation.getX() + 1));
                }
                else
                {
                    extendedX.setX(Math.floor(playerLocation.getX() - 1));
                }
                Block otherX = playerLocation.getWorld().getBlockAt(extendedX);
                if (!Util.isBlockInList(touchingBlocks, otherX))
                {
                    touchingBlocks.add(otherX);
                }
            }
        }
        
        headLocation.setY(headLocation.getY() + 1);
        
        if (headLocation.getZ() - (long) headLocation.getZ() != 0)
        {
            double difference = headLocation.getZ() - (long) headLocation.getZ();
            if (Math.abs(difference) > 0.6)
            {
                Location extendedZ = headLocation.clone();
                if (difference < 0)
                {
                    extendedZ.setZ(Math.floor(headLocation.getZ() - 1));
                }
                else
                {
                    extendedZ.setZ(Math.ceil(headLocation.getZ() + 1));
                }
                Block otherZ = headLocation.getWorld().getBlockAt(extendedZ);
                if (!Util.isBlockInList(touchingBlocks, otherZ))
                {
                    touchingBlocks.add(otherZ);
                }
            }
            else if (Math.abs(difference) < 0.4)
            {
                Location extendedZ = headLocation.clone();
                if (difference < 0)
                {
                    extendedZ.setZ(Math.ceil(headLocation.getZ() + 1));
                }
                else
                {
                    extendedZ.setZ(Math.floor(headLocation.getZ() - 1));
                }
                Block otherZ = headLocation.getWorld().getBlockAt(extendedZ);
                if (!Util.isBlockInList(touchingBlocks, otherZ))
                {
                    touchingBlocks.add(otherZ);
                }
            }
        }
        
        //        // 1.8 doesn't work with z height
        //        if (headLocation.getZ() - (long) headLocation.getZ() != 0)
        //        {
        //            double difference = headLocation.getZ() - (long) headLocation.getZ();
        //            if (Math.abs(difference) > 0.6)
        //            {
        //                Location extendedZ = headLocation.clone();
        //                if (difference < 0)
        //                {
        //                    extendedZ.setY(Math.floor(headLocation.getZ() - 1));
        //                }
        //                else
        //                {
        //                    extendedZ.setZ(Math.ceil(headLocation.getZ() + 1));
        //                }
        //                Block otherZ = headLocation.getWorld().getBlockAt(extendedZ);
        //                touchingBlocks.add(otherZ);
        //            }
        //            else if (Math.abs(difference) < 0.4)
        //            {
        //                Location extendedZ = headLocation.clone();
        //                if (difference < 0)
        //                {
        //                    extendedZ.setZ(Math.ceil(headLocation.getZ() + 1));
        //                }
        //                else
        //                {
        //                    extendedZ.setZ(Math.floor(headLocation.getZ() - 1));
        //                }
        //                Block otherZ = headLocation.getWorld().getBlockAt(extendedZ);
        //                touchingBlocks.add(otherZ);
        //            }
        //        }
        
        return touchingBlocks;
    }
    
    public static boolean blockLocationsEqual(Block block1, Block block2)
    {
        return block1.getWorld().equals(block2.getWorld()) && block1.getX() == block2.getX() && block1.getY() == block2.getY() && block1.getZ() == block2.getZ();
    }
    
    public static boolean isBlockInList(List<Block> blockList, Block block)
    {
        for (Block block1 : blockList)
        {
            if (Util.blockLocationsEqual(block, block1))
            {
                return true;
            }
        }
        return false;
    }
}
