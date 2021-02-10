package com.thekingelessar.assault.util;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Coordinate
{
    public double x;
    public double y;
    public double z;
    
    public Coordinate(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Coordinate(String coordinates)
    {
        Pattern pattern = Pattern.compile("^ *(-?[0-9]+.?[0-9]*) +(-?[0-9]+.?[0-9]*) +(-?[0-9]+.?[0-9]*)");
        Matcher matchedPattern = pattern.matcher(coordinates);
        
        if (matchedPattern.find())
        {
            try
            {
                String x = matchedPattern.group(1);
                this.x = Double.parseDouble(x);
            }
            catch (IllegalStateException | IndexOutOfBoundsException | NumberFormatException exception)
            {
                exception.printStackTrace();
                return;
            }
            
            try
            {
                String y = matchedPattern.group(2);
                this.y = Double.parseDouble(y);
            }
            catch (IllegalStateException | IndexOutOfBoundsException | NumberFormatException exception)
            {
                exception.printStackTrace();
                return;
            }
            
            try
            {
                String z = matchedPattern.group(3);
                this.z = Double.parseDouble(z);
            }
            catch (IllegalStateException | IndexOutOfBoundsException | NumberFormatException exception)
            {
                exception.printStackTrace();
                return;
            }
        }
    }
    
    public Location toLocation(World world, Float yaw, Float pitch)
    {
        if (yaw != null && pitch != null)
        {
            return new Location(world, this.x, this.y, this.z, yaw, pitch);
        }
        
        if (yaw == null && pitch != null)
        {
            return new Location(world, this.x, this.y, this.z, 0, pitch);
        }
        
        if (yaw != null && pitch == null)
        {
            return new Location(world, this.x, this.y, this.z, yaw, 0);
        }
        
        return new Location(world, this.x, this.y, this.z);
    }
    
    public Location toLocation(World world)
    {
        return new Location(world, this.x, this.y, this.z);
    }
    
    
    @Override
    public String toString()
    {
        return this.x + " " + this.y + " " + this.z;
    }
    
    
}
