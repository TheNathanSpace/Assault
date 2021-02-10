package com.thekingelessar.assault.game;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.countdown.TaskGameStartDelay;
import com.thekingelessar.assault.game.map.Map;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.game.world.WorldManager;
import com.thekingelessar.assault.util.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
    
    public HashMap<UUID, PlayerMode> playerModes = new HashMap<>();
    
    public TaskGameStartDelay taskGameStartDelay;
    
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
        Assault.INSTANCE.getLogger().info("Opened new game world: " + gameWorld.getName());
    }
    
    public void sendPlayersToWorld()
    {
        for (Player player : players)
        {
            player.teleport(gameMap.waitingSpawn.toLocation(this.gameWorld));
            UUID playerUUID = player.getUniqueId();
            playerModes.put(playerUUID, PlayerMode.setPlayerMode(playerUUID, PlayerMode.LOBBY));
        }
        
        if (spectators != null)
        {
            for (Player player : spectators)
            {
                player.teleport(gameMap.waitingSpawn.toLocation(this.gameWorld));
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
        
        taskGameStartDelay = new TaskGameStartDelay(200, 20, 20, this);
        taskGameStartDelay.runTaskTimer(Assault.INSTANCE, taskGameStartDelay.startDelay, taskGameStartDelay.tickDelay);
    }
    
    public void createTeams()
    {
        GameTeam redTeam = new GameTeam(TeamColor.RED, this);
        redTeam.setTeamMapBase();
        GameTeam blueTeam = new GameTeam(TeamColor.BLUE, this);
        blueTeam.setTeamMapBase();
        
        teams.put(TeamColor.RED, redTeam);
        teams.put(TeamColor.BLUE, blueTeam);
        
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
        createTeams();
        
        this.gameStage = GameStage.BUILDING_BASE;
        
        for (java.util.Map.Entry<TeamColor, GameTeam> team : teams.entrySet())
        {
            
            for (UUID teamMember : team.getValue().members)
            {
                try
                {
                    Player player = Bukkit.getPlayer(teamMember);
                    GameTeam gameTeam = getPlayerTeam(teamMember);
                    player.teleport(gameTeam.mapBase.defenderSpawn.toLocation(this.gameWorld, 180f, 0f));
                    // todo: add facing rotation for spawns so you can customize them
                    
                    Title title = new Title(ChatColor.WHITE + "You are on the " + gameTeam.color.getFormattedName() + ChatColor.WHITE + " team!", "Begin building your defenses!");
                    title.clearTitle(player);
                    
                    title.send(player);
                    
                }
                catch (Exception exception)
                {
                    // Player not valid
                }
            }
            
        }
        
        this.restoreHealth();
    }
    
    public List<Player> getPlayers()
    {
        List<Player> players = new ArrayList<>();
        for (java.util.Map.Entry<TeamColor, GameTeam> entry : this.teams.entrySet())
        {
            for (UUID uuid : entry.getValue().members)
            {
                players.add(Bukkit.getPlayer(uuid));
            }
        }
        
        return players;
    }
    
    public void restoreHealth()
    {
        for (Player player : this.getPlayers())
        {
            player.setHealth(player.getMaxHealth());
        }
    }
    
    public static GameInstance getPlayerGameInstance(Player player)
    {
        for (GameInstance gameInstance : Assault.gameInstances)
        {
            if (gameInstance.getPlayerTeam(player.getUniqueId()) != null)
            {
                return gameInstance;
            }
        }
        
        return null;
    }
    
    
}
