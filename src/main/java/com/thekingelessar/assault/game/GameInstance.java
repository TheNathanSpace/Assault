package com.thekingelessar.assault.game;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.config.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GameInstance
{
    public Map gameMap;
    public HashMap<String, GameTeam> teams;
    public Scoreboard teamScoreboard;
    
    public GameInstance(String map)
    {
        gameMap = Assault.maps.get(map);
        
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        teamScoreboard = manager.getNewScoreboard();
        
        createTeams();
    }
    
    public void createTeams()
    {
        teams.put(TeamColor.RED.toString(), new GameTeam(gameMap.spawns.get(TeamColor.RED.toString()), TeamColor.RED, this));
        teams.put(TeamColor.GREEN.toString(), new GameTeam(gameMap.spawns.get(TeamColor.GREEN.toString()), TeamColor.GREEN, this));
        
        List<Player> players = Bukkit.getWorld(Assault.gameWorld).getPlayers();
        Collections.shuffle(players);
        
        int numberOfTeams = 2;
        int currentPlayer = 0;
        List<List<Player>> teamLists = new ArrayList<>();
        for (int i = 0; i < numberOfTeams; i++)
        {
            List<Player> sublist = players.subList(currentPlayer, players.size() / numberOfTeams);
            currentPlayer = sublist.size();
            teamLists.add(sublist);
        }
        
        for (java.util.Map.Entry<String, GameTeam> entry : teams.entrySet())
        {
            entry.getValue().addMembers(teamLists.remove(0));
        }
    }
    
}
