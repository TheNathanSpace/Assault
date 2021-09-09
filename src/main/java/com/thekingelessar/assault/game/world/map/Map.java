package com.thekingelessar.assault.game.world.map;

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
import java.util.logging.Level;

public class Map
{
    
    public String mapName;
    
    public Coordinate waitingSpawn;
    public List<Coordinate> waitingPlatformBoundingBox = new ArrayList<>();
    
    public boolean voidEnabled = true;
    public double voidLevel = 70;
    
    public int buildingTime = 180;
    public int buildingCoins = 100;
    
    public int attackTimeLimit = 480;
    public int firstToFiveStarsTimeLimit = 480;
    
    public double borderX = 0;
    
    public double maxZ = 86;
    public double minZ = 2;
    
    public double maxY = 122;
    
    public double attackerBaseProtMaxZ = 20;
    
    public List<MapBase> bases = new ArrayList<>();
    
    public List<Material> placeableBlocks = new ArrayList<>();
    public List<Material> breakableBlocks = new ArrayList<>();
    
    public Map(YamlConfiguration config)
    {
        try
        {
            mapName = config.getString("world_name");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "world_name invalid");
            throw exception;
        }
        
        try
        {
            waitingSpawn = new Coordinate(config.getString("waiting_spawn"));
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "waiting_spawn invalid");
            throw exception;
        }
        
        try
        {
            for (Object boundingBox : config.getList("waiting_platform_bounding_box"))
            {
                waitingPlatformBoundingBox.add(new Coordinate((String) boundingBox));
            }
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "waiting_platform_bounding_box invalid");
            throw exception;
        }
        
        try
        {
            voidEnabled = config.getBoolean("void_enabled");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "void_enabled invalid; defaulting to true");
        }
        
        try
        {
            voidLevel = config.getDouble("void_level");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "void_level invalid; defaulting to 70");
        }
        
        try
        {
            buildingTime = config.getInt("building_time");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "building_time invalid; defaulting to 180");
        }
        
        try
        {
            buildingCoins = config.getInt("building_coins");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "building_coins invalid; defaulting to 100");
        }
        
        try
        {
            attackTimeLimit = config.getInt("attack_time_limit");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "attack_time_limit invalid; defaulting to 480");
        }
        
        try
        {
            firstToFiveStarsTimeLimit = config.getInt("first_to_five_stars_time_limit");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "first_to_five_stars_time_limit invalid; defaulting to 480");
        }
        
        try
        {
            borderX = config.getDouble("border_x");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "border_x invalid; defaulting to 0");
        }
        
        try
        {
            maxZ = config.getDouble("max_z");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "max_z invalid; defaulting to 86");
        }
        
        try
        {
            minZ = config.getDouble("min_z");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "min_z invalid; defaulting to 2");
        }
        
        try
        {
            attackerBaseProtMaxZ = config.getDouble("attacker_base_prot_max_z");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "attacker_base_prot_max_z invalid; defaulting to 20");
        }
        
        try
        {
            maxY = config.getDouble("max_y");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "max_y invalid; defaulting to 122");
        }
        
        List<?> baseList;
        try
        {
            baseList = config.getList("bases");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "bases invalid");
            throw exception;
        }
        
        for (Object base : baseList)
        {
            HashMap<String, HashMap<String, Object>> mappedBase;
            Set<String> baseTeamSet;
            String baseTeamString;
            TeamColor teamColor;
            HashMap<String, Object> baseSubMap;
            
            try
            {
                mappedBase = (HashMap<String, HashMap<String, Object>>) base;
                baseTeamSet = mappedBase.keySet();
                baseTeamString = baseTeamSet.iterator().next();
                teamColor = TeamColor.valueOf(baseTeamString);
                baseSubMap = mappedBase.get(baseTeamString);
            }
            catch (Exception exception)
            {
                Assault.INSTANCE.getLogger().log(Level.WARNING, "Base structure invalid");
                throw exception;
            }
            
            List<Coordinate> defenderSpawns;
            try
            {
                List<Object> defenderSpawnsObject = (List<Object>) baseSubMap.get("defender_spawns");
                defenderSpawns = new ArrayList<>();
                for (Object spawn : defenderSpawnsObject)
                {
                    Coordinate spawnCoord = new Coordinate((String) spawn);
                    defenderSpawns.add(spawnCoord);
                }
            }
            catch (Exception exception)
            {
                Assault.INSTANCE.getLogger().log(Level.WARNING, "defender_spawns invalid");
                throw exception;
            }
            
            List<Coordinate> attackerSpawns;
            try
            {
                List<Object> attackerSpawnsObject = (List<Object>) baseSubMap.get("attacker_spawns");
                attackerSpawns = new ArrayList<>();
                for (Object spawn : attackerSpawnsObject)
                {
                    Coordinate spawnCoord = new Coordinate((String) spawn);
                    attackerSpawns.add(spawnCoord);
                }
            }
            catch (Exception exception)
            {
                Assault.INSTANCE.getLogger().log(Level.WARNING, "attacker_spawns invalid");
                throw exception;
            }
            
            List<List<Coordinate>> defenderBoundingBoxes;
            try
            {
                List<Object> defenderBoundingBoxesParent = (List<Object>) baseSubMap.get("defender_bounding_boxes");
                List<List<Object>> defenderBoundingBoxesGeneric = new ArrayList<>();
                defenderBoundingBoxes = new ArrayList<>();
                for (Object boundingBox : defenderBoundingBoxesParent)
                {
                    defenderBoundingBoxesGeneric.add((List<Object>) boundingBox);
                }
                for (List<Object> boundingBox : defenderBoundingBoxesGeneric)
                {
                    List<Coordinate> singleBoundCoord = new ArrayList<>();
                    for (Object singleBound : boundingBox)
                    {
                        singleBoundCoord.add(new Coordinate((String) singleBound));
                    }
                    defenderBoundingBoxes.add(singleBoundCoord);
                }
            }
            catch (Exception exception)
            {
                Assault.INSTANCE.getLogger().log(Level.WARNING, "defender_bounding_boxes invalid");
                throw exception;
            }
            
            List<Coordinate> emeraldSpawns;
            try
            {
                List<Object> emeraldSpawnsObject = (List<Object>) baseSubMap.get("emerald_spawns");
                emeraldSpawns = new ArrayList<>();
                for (Object spawn : emeraldSpawnsObject)
                {
                    Coordinate spawnCoord = new Coordinate((String) spawn);
                    emeraldSpawns.add(spawnCoord);
                }
            }
            catch (Exception exception)
            {
                Assault.INSTANCE.getLogger().log(Level.WARNING, "emerald_spawns invalid");
                throw exception;
            }
            
            Coordinate objective;
            try
            {
                objective = new Coordinate((String) baseSubMap.get("objective"));
            }
            catch (Exception exception)
            {
                Assault.INSTANCE.getLogger().log(Level.WARNING, "objective invalid");
                throw exception;
            }
            
            List<Coordinate> buffShops;
            try
            {
                List<Object> buffShopsObject = (List<Object>) baseSubMap.get("attacker_buff_shops");
                buffShops = new ArrayList<>();
                for (Object spawn : buffShopsObject)
                {
                    Coordinate spawnCoord = new Coordinate((String) spawn);
                    buffShops.add(spawnCoord);
                }
            }
            catch (Exception exception)
            {
                Assault.INSTANCE.getLogger().log(Level.WARNING, "attacker_buff_shops invalid");
                throw exception;
            }
            
            List<Coordinate> itemShops;
            try
            {
                List<Object> itemShopsObject = (List<Object>) baseSubMap.get("item_shops");
                itemShops = new ArrayList<>();
                for (Object spawn : itemShopsObject)
                {
                    Coordinate spawnCoord = new Coordinate((String) spawn);
                    itemShops.add(spawnCoord);
                }
            }
            catch (Exception exception)
            {
                Assault.INSTANCE.getLogger().log(Level.WARNING, "item_shops invalid");
                throw exception;
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
                Assault.INSTANCE.getLogger().warning("Invalid material in breakable_blocks: " + object.toString());
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
                Assault.INSTANCE.getLogger().warning("Invalid material in placeable_blocks: " + object.toString());
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
