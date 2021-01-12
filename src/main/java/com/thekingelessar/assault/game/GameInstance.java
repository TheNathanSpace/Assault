package com.thekingelessar.assault.game;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.config.MapMaker;

import java.util.HashMap;

public class GameInstance
{
    public MapMaker gameMap;
    public HashMap<String, Team> teams;
    
    public GameInstance(String map) {
        gameMap = Assault.maps.get(map);
    }
    
    public void balanceTeams() {
    
    }
}
