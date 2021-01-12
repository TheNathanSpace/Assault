package com.thekingelessar.assault.util;

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
        Pattern pattern = Pattern.compile("^ *(-?[0-9]+) *(-?[0-9]+) *(-?[0-9]+)");
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
}
