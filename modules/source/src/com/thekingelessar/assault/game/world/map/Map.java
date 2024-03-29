package com.thekingelessar.assault.game.world.map;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.util.Coordinate;
import com.thekingelessar.assault.util.Util;
import com.thekingelessar.assault.util.version.XMaterial;
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
    public boolean deleteWaitingPlatform = true;
    public List<Coordinate> waitingPlatformBoundingBox = new ArrayList<>();
    
    public boolean voidEnabled = true;
    public double voidLevel = 70;
    
    public int buildingTime = 180;
    public int buildingCoins = 100;
    
    public double giveCoinDelay = 5.0;
    public int giveCoinAmount = 8;
    
    public boolean flatCoinsOnKill = false;
    public int coinsOnKill = 0;
    public boolean percentCoinsOnKill = true;
    public double rateOnKill = 0.2;
    
    public int coinCap = 0;
    
    public int attackTimeLimit = 480;
    public int firstToFiveStarsTimeLimit = 480;
    
    public double borderX = 0;
    
    public double maxZ = 86;
    public double minZ = 2;
    
    public double maxY = 122;
    
    public double attackerBaseProtMaxZ = 20;
    
    public int emeraldSpawnDelay = 20;
    
    public List<MapBase> bases = new ArrayList<>();
    
    public boolean enablePlacableBlocks = false;
    public boolean enableBreakableBlocks = true;
    
    public List<Material> placeableBlocks = new ArrayList<>();
    public List<Material> breakableBlocks = new ArrayList<>();
    
    public Map(YamlConfiguration config) throws Exception
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
            deleteWaitingPlatform = config.getBoolean("delete_waiting_platform");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "delete_waiting_platform invalid; defaulting to true");
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
            giveCoinDelay = config.getDouble("give_coin_delay");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "give_coin_delay invalid; defaulting to 5.0");
        }
        
        try
        {
            giveCoinAmount = config.getInt("give_coin_amount");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "give_coin_amount invalid; defaulting to 8");
        }
        
        try
        {
            flatCoinsOnKill = config.getBoolean("flat_coins_on_kill");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "flat_coins_on_kill invalid; defaulting to false");
        }
        
        try
        {
            coinsOnKill = config.getInt("coins_on_kill");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "coins_on_kill invalid; defaulting to 0");
        }
        
        try
        {
            percentCoinsOnKill = config.getBoolean("percent_coins_on_kill");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "percent_coins_on_kill invalid; defaulting to true");
        }
        
        try
        {
            rateOnKill = config.getDouble("rate_on_kill");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "rate_on_kill invalid; defaulting to 0.2");
        }
        
        try
        {
            coinCap = config.getInt("coin_cap");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "coin_cap invalid; defaulting to 0");
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
            emeraldSpawnDelay = config.getInt("emerald_spawn_delay");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "emerald_spawn_delay invalid; defaulting to 20");
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
                teamColor = TeamColor.valueOfCaseInsensitive(baseTeamString);
                if (teamColor == null)
                {
                    Assault.INSTANCE.getLogger().log(Level.WARNING, "Team color invalid");
                    throw new Exception();
                }
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
            
            List<Coordinate> objectives = new ArrayList<>();
            try
            {
                List<Object> objectiveListObject = (List<Object>) baseSubMap.get("objectives");
                for (Object object : objectiveListObject)
                {
                    objectives.add(new Coordinate((String) object));
                }
            }
            catch (Exception exception)
            {
                Assault.INSTANCE.getLogger().log(Level.WARNING, "objectives invalid, trying single objective");
                
                try
                {
                    objectives.add(new Coordinate(config.getString("objective")));
                }
                catch (Exception exception2)
                {
                    Assault.INSTANCE.getLogger().log(Level.WARNING, "objective invalid");
                    throw exception;
                }
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
            
            MapBase mapBase = new MapBase(teamColor, defenderSpawns, defenderBoundingBoxes, attackerSpawns, emeraldSpawns, objectives, itemShops, buffShops);
            bases.add(mapBase);
        }
        
        getBlocks(config);
        
    }
    
    public void getBlocks(YamlConfiguration config)
    {
        List<?> breakableList = config.getList("breakable_blocks");
        
        for (Object object : breakableList)
        {
            if (object.toString().equalsIgnoreCase("NONE")) break;
            try
            {
                String materialString = object.toString().toUpperCase();
                String containedMaterial = "";
                if (materialString.contains("WOOL"))
                {
                    containedMaterial = "WOOL";
                }
                else if (materialString.contains("GLAZED_TERRACOTTA"))
                {
                    containedMaterial = "GLAZED_TERRACOTTA";
                }
                else if (materialString.contains("STAINED_CLAY") || materialString.contains("TERRACOTTA"))
                {
                    containedMaterial = "STAINED_CLAY";
                }
                else if (materialString.contains("THIN_GLASS") || materialString.contains("STAINED_GLASS_PANE"))
                {
                    containedMaterial = "THIN_GLASS";
                }
                else if (materialString.contains("STAINED_GLASS"))
                {
                    containedMaterial = "STAINED_GLASS";
                }
                else if (materialString.contains("WALL_BANNER"))
                {
                    containedMaterial = "WALL_BANNER";
                }
                else if (materialString.contains("BANNER"))
                {
                    containedMaterial = "BANNER";
                }
                else if (materialString.contains("BED"))
                {
                    containedMaterial = "BED";
                }
                else if (materialString.contains("CARPET"))
                {
                    containedMaterial = "CARPET";
                }
                else if (materialString.contains("CONCRETE_POWDER"))
                {
                    containedMaterial = "CONCRETE_POWDER";
                }
                else if (materialString.contains("CONCRETE"))
                {
                    containedMaterial = "CONCRETE";
                }
                
                if (!containedMaterial.equals(""))
                {
                    List<Material> woolList = new ArrayList<>();
                    woolList.add(XMaterial.matchXMaterial(containedMaterial).get().parseMaterial());
                    for (int i = 1; i <= 15; i++)
                    {
                        Material material = XMaterial.matchXMaterial(containedMaterial + ":" + i).get().parseMaterial();
                        woolList.add(material);
                    }
                    breakableBlocks.addAll(woolList);
                }
                else
                {
                    Material material = XMaterial.matchXMaterial(materialString).get().parseMaterial();
                    breakableBlocks.add(material);
                }
            }
            catch (IllegalArgumentException exception)
            {
                Assault.INSTANCE.getLogger().warning("Invalid material in breakable_blocks: " + object.toString());
            }
        }
        
        try
        {
            enablePlacableBlocks = config.getBoolean("enable_placeable_blocks");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "enable_placeable_blocks invalid; defaulting to false");
        }
        
        try
        {
            enableBreakableBlocks = config.getBoolean("enable_breakable_blocks");
        }
        catch (Exception exception)
        {
            Assault.INSTANCE.getLogger().log(Level.WARNING, "enable_breakable_blocks invalid; defaulting to true");
        }
        
        List<?> placeableList = config.getList("placeable_blocks");
        
        for (Object object : placeableList)
        {
            try
            {
                String materialString = object.toString().toUpperCase();
                String containedMaterial = "";
                if (materialString.contains("WOOL"))
                {
                    containedMaterial = "WOOL";
                }
                else if (materialString.contains("GLAZED_TERRACOTTA"))
                {
                    containedMaterial = "GLAZED_TERRACOTTA";
                }
                else if (materialString.contains("STAINED_CLAY") || materialString.contains("TERRACOTTA"))
                {
                    containedMaterial = "STAINED_CLAY";
                }
                else if (materialString.contains("THIN_GLASS") || materialString.contains("STAINED_GLASS_PANE"))
                {
                    containedMaterial = "THIN_GLASS";
                }
                else if (materialString.contains("STAINED_GLASS"))
                {
                    containedMaterial = "STAINED_GLASS";
                }
                else if (materialString.contains("WALL_BANNER"))
                {
                    containedMaterial = "WALL_BANNER";
                }
                else if (materialString.contains("BANNER"))
                {
                    containedMaterial = "BANNER";
                }
                else if (materialString.contains("BED"))
                {
                    containedMaterial = "BED";
                }
                else if (materialString.contains("CARPET"))
                {
                    containedMaterial = "CARPET";
                }
                else if (materialString.contains("CONCRETE_POWDER"))
                {
                    containedMaterial = "CONCRETE_POWDER";
                }
                else if (materialString.contains("CONCRETE"))
                {
                    containedMaterial = "CONCRETE";
                }
                
                if (!containedMaterial.equals(""))
                {
                    List<Material> woolList = new ArrayList<>();
                    woolList.add(XMaterial.matchXMaterial(containedMaterial).get().parseMaterial());
                    for (int i = 1; i <= 15; i++)
                    {
                        Material material = XMaterial.matchXMaterial(containedMaterial + ":" + i).get().parseMaterial();
                        woolList.add(material);
                    }
                    placeableBlocks.addAll(woolList);
                }
                else
                {
                    Material material = XMaterial.matchXMaterial(materialString).get().parseMaterial();
                    placeableBlocks.add(material);
                }
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
    
    public MapBase getTeamOne()
    {
        return this.bases.get(0);
    }
    
    public MapBase getTeamTwo()
    {
        return this.bases.get(1);
    }
    
    public void clearWaitingPlatform(World world)
    {
        if (this.deleteWaitingPlatform)
        {
            List<Block> blocks = Util.selectBoundingBox(waitingPlatformBoundingBox.get(0).toLocation(world), waitingPlatformBoundingBox.get(1).toLocation(world), world);
            
            for (Block block : blocks)
            {
                block.setType(Material.AIR);
            }
        }
    }
    
}
