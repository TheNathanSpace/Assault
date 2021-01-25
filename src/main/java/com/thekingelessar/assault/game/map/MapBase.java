package com.thekingelessar.assault.game.map;

import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.util.Coordinate;

import java.util.List;

public class MapBase
{
    
    TeamColor defendingColor;
    Coordinate defenderSpawn;
    Coordinate attackerSpawn;
    List<Coordinate> emeraldSpawn;
    
    public MapBase(TeamColor defendingColor, Coordinate defenderSpawn, Coordinate attackerSpawn, List<Coordinate> emeraldSpawn)
    {
        this.defendingColor = defendingColor;
        this.defenderSpawn = defenderSpawn;
        this.attackerSpawn = attackerSpawn;
        this.emeraldSpawn = emeraldSpawn;
    }
}
