package com.thekingelessar.assault.game.map;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.util.Coordinate;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Map
{
    
    public String mapName;
    
    public Coordinate waitingSpawn;
    public List<Coordinate> waitingPlatformBoundingBox = new ArrayList<>();
    
    public boolean voidEnabled;
    public double voidLevel;
    
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
        
        List<?> baseList = config.getList("bases");
        
        for (Object base : baseList)
        {
            HashMap<String, HashMap<String, Object>> mappedBase = (HashMap<String, HashMap<String, Object>>) base;
            Set<String> baseTeamSet = mappedBase.keySet();
            String baseTeamString = baseTeamSet.iterator().next();
            TeamColor teamColor = TeamColor.valueOf(baseTeamString);
            
            HashMap<String, Object> baseSubMap = mappedBase.get(baseTeamString);
            Coordinate defenderSpawn = new Coordinate((String) baseSubMap.get("defender_spawn"));
            Coordinate attackerSpawn = new Coordinate((String) baseSubMap.get("attacker_spawn"));
            List<Object> emeraldSpawnsObject = (List<Object>) baseSubMap.get("emerald_spawns");
            
            List<Coordinate> emeraldSpawns = new ArrayList<>();
            for (Object spawn : emeraldSpawnsObject)
            {
                Coordinate spawnCoord = new Coordinate((String) spawn);
                emeraldSpawns.add(spawnCoord);
            }
            
            MapBase mapBase = new MapBase(teamColor, defenderSpawn, attackerSpawn, emeraldSpawns);
            System.out.println("Team " + teamColor + ", spawn " + defenderSpawn.toString());
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
    
    public Coordinate getSpawn(GameTeam team, TeamStage teamStage)
    {
        TeamColor teamColor = team.color;
        
        if (teamStage == null)
        {
            teamStage = team.teamStage;
        }
        
        for (MapBase base : bases)
        {
            if (teamStage.equals(TeamStage.DEFENDING))
            {
                if (base.teamColor.equals(teamColor)) return base.defenderSpawn;
            }
            
            return base.attackerSpawn;
        }
        
        return null;
        
    }
    
}
