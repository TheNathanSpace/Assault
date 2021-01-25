package com.thekingelessar.assault.game;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.map.Map;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.game.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class GameInstance
{
    public UUID gameUUID = UUID.randomUUID();
    public String gameID;
    
    public GameStage gameStage;
    
    public Map gameMap;
    public World gameWorld;
    
    public HashMap<TeamColor, GameTeam> teams = new HashMap<>();
    public Scoreboard teamScoreboard;
    
    private List<Player> players;
    private List<Player> spectators;
    
    public GameInstance(String mapName, List<Player> players, List<Player> spectators)
    {
        this.gameMap = Assault.maps.get(mapName);
        this.gameID = mapName + "_" + gameUUID.toString();
        
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        teamScoreboard = manager.getNewScoreboard();
        
        this.players = players;
        this.spectators = spectators;
    }
    
    public void startWorld()
    {
        this.gameWorld = WorldManager.createWorldFromMap(this.gameMap.mapName, false, this.gameID);
    }
    
    public void createTeams()
    {
        GameTeam redTeam = new GameTeam(TeamColor.RED, this);
        redTeam.setSpawns(gameMap.getSpawn(redTeam, TeamStage.ATTACKING), gameMap.getSpawn(redTeam, TeamStage.DEFENDING));
        GameTeam greenTeam = new GameTeam(TeamColor.GREEN, this);
        greenTeam.setSpawns(gameMap.getSpawn(greenTeam, TeamStage.ATTACKING), gameMap.getSpawn(greenTeam, TeamStage.DEFENDING));
        
        teams.put(TeamColor.RED, redTeam);
        teams.put(TeamColor.GREEN, greenTeam);
        
        Collections.shuffle(this.players);
        
        int numberOfTeams = 2;
        int firstMember = 0;
        List<List<Player>> teamLists = new ArrayList<>();
        for (int i = 0; i < numberOfTeams; i++)
        {
            List<Player> sublist = players.subList(firstMember, (players.size() / numberOfTeams) * (i + 1));
            firstMember = (players.size() / numberOfTeams) * (i + 1);
            teamLists.add(sublist);
        }
        
        for (List<Player> team : teamLists)
        {
            for (Player player : team)
            {
                System.out.println(player.getDisplayName());
            }
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
    
    public void startGame()
    {
        this.gameStage = GameStage.SPLITTING_TEAMS;
        
        createTeams();
        
        for (java.util.Map.Entry<TeamColor, GameTeam> team : teams.entrySet())
        {
            
            for (UUID teamMember : team.getValue().members)
            {
                try
                {
                    Player player = Bukkit.getPlayer(teamMember);
                    GameTeam gameTeam = getPlayerTeam(teamMember);
                    player.teleport(gameTeam.defenderSpawn.toLocation(this.gameWorld));
                }
                catch (Exception exception)
                {
                    // Player not valid
                }
            }
            
        }
    }
    
}
