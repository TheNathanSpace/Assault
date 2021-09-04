package com.thekingelessar.assault.game.map;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.util.Coordinate;
import com.thekingelessar.assault.util.Util;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class Map
{
    
    public String mapName;
    
    public Coordinate waitingSpawn;
    public List<Coordinate> waitingPlatformBoundingBox = new ArrayList<>();
    
    public boolean voidEnabled;
    public double voidLevel;
    
    public int buildingTime;
    public int attackTimeLimit;
    
    public double maxZ;
    public double minZ;
    
    public double maxY;
    
    public double attackerBaseProtMaxZ;
    
    public List<MapBase> bases = new ArrayList<>();
    
    public List<Material> placeableBlocks = new ArrayList<>();
    public List<Material> breakableBlocks = new ArrayList<>();
    
    public Map(YamlConfiguration config)
    {
        mapName = config.getString("world_name");
        waitingSpawn = new Coordinate(config.getString("waiting_spawn"));
        
        for (Object boundingBox : config.getList("waiting_platform_bounding_box"))
        {
            waitingPlatformBoundingBox.add(new Coordinate((String) boundingBox));
        }
        
        voidEnabled = config.getBoolean("void_enabled");
        voidLevel = config.getDouble("void_level");
        
        buildingTime = config.getInt("building_time");
        attackTimeLimit = config.getInt("attack_time_limit");
    
        maxZ = config.getDouble("max_z");
        minZ = config.getDouble("min_z");
        attackerBaseProtMaxZ = config.getDouble("attacker_base_prot_max_z");
        
        maxY = config.getDouble("max_y");
        
        List<?> baseList = config.getList("bases");
        
        for (Object base : baseList)
        {
            HashMap<String, HashMap<String, Object>> mappedBase = (HashMap<String, HashMap<String, Object>>) base;
            Set<String> baseTeamSet = mappedBase.keySet();
            String baseTeamString = baseTeamSet.iterator().next();
            TeamColor teamColor = TeamColor.valueOf(baseTeamString);
            
            HashMap<String, Object> baseSubMap = mappedBase.get(baseTeamString);
            
            List<Object> defenderSpawnsObject = (List<Object>) baseSubMap.get("defender_spawns");
            List<Coordinate> defenderSpawns = new ArrayList<>();
            for (Object spawn : defenderSpawnsObject)
            {
                Coordinate spawnCoord = new Coordinate((String) spawn);
                defenderSpawns.add(spawnCoord);
            }
            
            List<Object> attackerSpawnsObject = (List<Object>) baseSubMap.get("attacker_spawns");
            List<Coordinate> attackerSpawns = new ArrayList<>();
            for (Object spawn : attackerSpawnsObject)
            {
                Coordinate spawnCoord = new Coordinate((String) spawn);
                attackerSpawns.add(spawnCoord);
            }
            
            List<Object> defenderBoundingBoxesParent = (List<Object>) baseSubMap.get("defender_bounding_boxes");
            List<List<Object>> defenderBoundingBoxesGeneric = new ArrayList<>();
            List<List<Coordinate>> defenderBoundingBoxes = new ArrayList<>();
            for (Object boundingBox : defenderBoundingBoxesParent)
            {
                defenderBoundingBoxesGeneric.add((List<Object>) boundingBox);
            }
            for (Object boundingBox : defenderBoundingBoxesGeneric)
            {
                defenderBoundingBoxes.add((List<Coordinate>) boundingBox);
            }
            
            List<Object> emeraldSpawnsObject = (List<Object>) baseSubMap.get("emerald_spawns");
            List<Coordinate> emeraldSpawns = new ArrayList<>();
            for (Object spawn : emeraldSpawnsObject)
            {
                Coordinate spawnCoord = new Coordinate((String) spawn);
                emeraldSpawns.add(spawnCoord);
            }
            
            Coordinate objective = new Coordinate((String) baseSubMap.get("objective"));
            
            List<Object> buffShopsObject = (List<Object>) baseSubMap.get("attacker_buff_shops");
            List<Coordinate> buffShops = new ArrayList<>();
            for (Object spawn : buffShopsObject)
            {
                Coordinate spawnCoord = new Coordinate((String) spawn);
                buffShops.add(spawnCoord);
            }
            
            List<Object> itemShopsObject = (List<Object>) baseSubMap.get("item_shops");
            List<Coordinate> itemShops = new ArrayList<>();
            for (Object spawn : itemShopsObject)
            {
                Coordinate spawnCoord = new Coordinate((String) spawn);
                itemShops.add(spawnCoord);
            }
            
            MapBase mapBase = new MapBase(teamColor, defenderSpawns, defenderBoundingBoxes, attackerSpawns, emeraldSpawns, objective, itemShops, buffShops);
            bases.add(mapBase);
        }
        
        getBlocks(config);
        
    }
    
    public void getBlocks(YamlConfiguration config)
    {
        List<?> breakableList = config.getList("breakable_blocks");
        
        for (Object object : breakableList)
        {
            try
            {
                Material material = Material.valueOf(object.toString());
                breakableBlocks.add(material);
            }
            catch (IllegalArgumentException exception)
            {
                Assault.INSTANCE.getLogger().warning("Invalid material in map configuration: " + object.toString());
            }
        }
        
        List<?> placeableList = config.getList("placeable_blocks");
        
        for (Object object : placeableList)
        {
            try
            {
                Material material = Material.valueOf(object.toString());
                placeableBlocks.add(material);
            }
            catch (IllegalArgumentException exception)
            {
                Assault.INSTANCE.getLogger().warning("Invalid material in map configuration: " + object.toString());
            }
        }
    }
    
    public Coordinate getSpawn(GameTeam playerTeam, TeamStage playerTeamStage)
    {
        if (playerTeamStage == null)
        {
            playerTeamStage = playerTeam.teamStage;
        }
        
        if (playerTeamStage.equals(TeamStage.ATTACKING))
        {
            Random rand = new Random();
            MapBase defendingBase = playerTeam.gameInstance.getDefendingTeam().mapBase;
            return defendingBase.attackerSpawns.get(rand.nextInt(defendingBase.attackerSpawns.size()));
        }
        
        if (playerTeamStage.equals(TeamStage.DEFENDING))
        {
            Random rand = new Random();
            return playerTeam.mapBase.defenderSpawns.get(rand.nextInt(playerTeam.mapBase.defenderSpawns.size()));
        }
        
        return null;
    }
    
    public void clearWaitingPlatform(World world)
    {
        List<Block> blocks = Util.selectBoundingBox(waitingPlatformBoundingBox.get(0).toLocation(world), waitingPlatformBoundingBox.get(1).toLocation(world), world);
        
        for (Block block : blocks)
        {
            block.setType(Material.AIR);
        }
    }
    
}
