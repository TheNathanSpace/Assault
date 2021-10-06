package com.thekingelessar.assault.game;

import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Objective
{
    public GameInstance gameInstance;
    public GameTeam gameTeam;
    public Item item;
    public Location intendedLocation;
    public UUID owner;
    
    public Objective(GameInstance gameInstance, GameTeam gameTeam, Item item, Location intendedLocation)
    {
        this.gameInstance = gameInstance;
        this.gameTeam = gameTeam;
        this.item = item;
        this.intendedLocation = intendedLocation;
    }
    
    public Objective(GameInstance gameInstance, GameTeam gameTeam, Item item, Location intendedLocation, UUID owner)
    {
        this.gameInstance = gameInstance;
        this.gameTeam = gameTeam;
        this.item = item;
        this.intendedLocation = intendedLocation;
        this.owner = owner;
    }
    
    public void sendToLocation()
    {
        if (this.item.getLocation() == null || this.item.getLocation().distance(this.intendedLocation) > 1)
        {
            Vector velocity = item.getVelocity();
            item.setVelocity(velocity.zero());
            
            this.item.teleport(this.intendedLocation);
        }
    }
    
    public void delete()
    {
        this.item.remove();
        this.gameInstance.attackingObjectives.remove(this);
    }
}
