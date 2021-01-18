package com.thekingelessar.assault.game;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.map.Map;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.game.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class GameInstance
{
    public UUID gameUUID = UUID.randomUUID();
    public String gameID;
    
    public Map gameMap;
    public World gameWorld;
    
    public HashMap<TeamColor, GameTeam> teams = new HashMap<>();
    public Scoreboard teamScoreboard;
    
    public GameInstance(String mapName)
    {
        this.gameMap = Assault.maps.get(mapName);
        this.gameID = mapName + "_" + gameUUID.toString();
        
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        teamScoreboard = manager.getNewScoreboard();
    }
    
    public void createWorld()
    {
        this.gameWorld = WorldManager.createWorldFromMap(this.gameMap.name, false, this.gameID);
    }
    
    public void createTeams()
    {
        
        teams.put(TeamColor.RED, new GameTeam(gameMap.spawns.get(TeamColor.RED), ChatColor.RED, this));
        teams.put(TeamColor.GREEN, new GameTeam(gameMap.spawns.get(TeamColor.GREEN), ChatColor.GREEN, this));
        
        List<Player> players = gameWorld.getPlayers();
        Collections.shuffle(players);
        
        int numberOfTeams = 2;
        int firstMember = 0;
        List<List<Player>> teamLists = new ArrayList<>();
        for (int i = 0; i < numberOfTeams; i++)
        {
            List<Player> sublist = players.subList(firstMember, (players.size() / numberOfTeams) * (i + 1));
            firstMember = (players.size() / numberOfTeams) * (i + 1);
            teamLists.add(sublist);
        }
        
        int iter = 1;
        for (List<Player> team : teamLists)
        {
            System.out.println("TEAM " + iter);
            
            for (Player player : team)
            {
                System.out.println(player.getDisplayName());
            }
            
            iter++;
        }
        
        for (java.util.Map.Entry<TeamColor, GameTeam> entry : teams.entrySet())
        {
            entry.getValue().addMembers(teamLists.remove(0));
        }
    }
    
    public GameTeam getPlayerTeam(UUID uuid)
    {
        for (java.util.Map.Entry<TeamColor, GameTeam> team : teams.entrySet())
        {
            
            for (UUID teamUUID : team.getValue().members)
            {
                if (teamUUID.equals(uuid))
                {
                    return team.getValue();
                }
            }
            
        }
        
        return null;
    }
    
}
