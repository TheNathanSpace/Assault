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
    
    public Double yaw;
    public Double pitch;
    
    public Coordinate(Location location)
    {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = (double) location.getYaw();
        this.pitch = (double) location.getPitch();
    }
    
    public Coordinate(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Coordinate(String coordinates)
    {
        Pattern pattern = Pattern.compile("^ *(-?[0-9]+.?[0-9]*) +(-?[0-9]+.?[0-9]*) +(-?[0-9]+.?[0-9]*) *(-?[0-9]+.?[0-9]*)? ?(-?[0-9]+.?[0-9]*)?");
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
            
            try
            {
                String yaw = matchedPattern.group(4);
                this.yaw = Double.parseDouble(yaw);
            }
            catch (IllegalStateException | IndexOutOfBoundsException | NumberFormatException exception)
            {
                exception.printStackTrace();
                return;
            }
            
            try
            {
                String pitch = matchedPattern.group(5);
                this.pitch = Double.parseDouble(pitch);
            }
            catch (IllegalStateException | IndexOutOfBoundsException | NumberFormatException exception)
            {
                exception.printStackTrace();
                return;
            }
        }
    }
    
    public Location toLocation(World world)
    {
        if (yaw == null || pitch == null)
        {
            return new Location(world, this.x, this.y, this.z);
        }
        
        return new Location(world, this.x, this.y, this.z, this.yaw.floatValue(), this.pitch.floatValue());
    }
    
    
    @Override
    public String toString()
    {
        return this.x + " " + this.y + " " + this.z;
    }
    
}
