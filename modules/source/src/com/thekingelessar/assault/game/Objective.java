package com.thekingelessar.assault.game;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.world.map.MapBase;
import com.thekingelessar.assault.util.version.XMaterial;
import com.thekingelessar.assault.util.version.XSound;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
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
    
    public void spawnItem()
    {
        if (this.item != null)
        {
            this.despawnItem();
        }
        this.item = this.gameInstance.gameWorld.dropItem(this.intendedLocation, new ItemStack(XMaterial.NETHER_STAR.parseMaterial()));
        this.sendToLocation();
    }
    
    public void despawnItem()
    {
        if (this.item != null)
        {
            this.item.remove();
            this.item = null;
        }
    }
    
    public void sendToLocation()
    {
        Vector velocity = item.getVelocity();
        item.setVelocity(velocity.zero());
        
        this.item.teleport(this.intendedLocation);
    }
    
    public void deleteObjective()
    {
        this.despawnItem();
        this.gameTeam.objectiveList.remove(this);
    }
    
    public static boolean dropObjective(GamePlayer gamePlayer)
    {
        GameInstance gameInstance = gamePlayer.gameInstance;
        Player player = gamePlayer.player;
        
        Block objectiveBlock = player.getLocation().getBlock();
        if (!objectiveBlock.getType().equals(XMaterial.AIR.parseMaterial()) && !XMaterial.isWater(objectiveBlock))
        {
            return false;
        }
        
        int tested = 0;
        while (objectiveBlock.getRelative(BlockFace.DOWN).getType().equals(XMaterial.AIR.parseMaterial()) || XMaterial.isWater(objectiveBlock.getRelative(BlockFace.DOWN)))
        {
            objectiveBlock = objectiveBlock.getRelative(BlockFace.DOWN);
            tested++;
            
            if (tested >= 25)
            {
                return false;
            }
        }
        
        Location objectiveLocation = player.getLocation().clone();
        objectiveLocation.setY(objectiveBlock.getY());
        
        for (GameTeam gameTeam : gameInstance.teams)
        {
            MapBase mapBase = gameTeam.mapBase;
            
            if (mapBase.isInDefenderBoundingBox(objectiveLocation))
            {
                return false;
            }
        }
        
        if (objectiveLocation.getZ() < gameInstance.gameMap.attackerBaseProtMaxZ)
        {
            return false;
        }
        
        Objective objective = new Objective(gameInstance, gamePlayer.gameTeam, null, objectiveLocation, player.getUniqueId());
        objective.spawnItem();
        gamePlayer.gameTeam.objectiveList.add(objective);
        
        player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
        player.sendMessage(Assault.ASSAULT_PREFIX + "Objective placed!");
        
        return true;
    }
    
    public static void recallObjective(GamePlayer gamePlayer)
    {
        Player player = gamePlayer.player;
        for (Objective objective : new ArrayList<>(gamePlayer.gameTeam.objectiveList))
        {
            if (objective.owner != null && objective.owner.equals(player.getUniqueId()))
            {
                objective.deleteObjective();
                player.playSound(player.getLocation(), XSound.ENTITY_SHEEP_SHEAR.parseSound(), 1.0F, 1.0F);
                player.sendMessage(Assault.ASSAULT_PREFIX + "Objective retrieved!");
            }
        }
    }
}
