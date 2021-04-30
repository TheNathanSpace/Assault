package com.thekingelessar.assault.game;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.map.Map;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.game.timertasks.*;
import com.thekingelessar.assault.game.world.WorldManager;
import com.thekingelessar.assault.util.Coordinate;
import com.thekingelessar.assault.util.FireworkUtils;
import com.thekingelessar.assault.util.Title;
import com.thekingelessar.assault.util.Util;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    
    private final List<Player> players;
    private final List<Player> spectators;
    
    public HashMap<Player, PlayerMode> playerModes = new HashMap<>();
    
    public TaskTickTimer taskTickTimer;
    public TaskCountdownGameStart taskCountdownGameStart;
    public TaskCountdownBuilding taskCountdownBuilding;
    public TaskAttackTimer taskAttackTimer;
    public TaskCountdownSwapAttackers taskCountdownSwapAttackers;
    public TaskCountdownGameEnd taskCountdownGameEnd;
    
    public TeamColor attackingTeam;
    
    public GameTeam overrideWinners = null;
    
    public int buildingSecondsLeft = 180;
    public int teamsGone = 0;
    
    public TaskGiveCoins taskGiveCoins;
    
    public List<Coordinate> placedBlocks = new ArrayList<>();
    
    public List<Item> guidingObjectives = new ArrayList<>();
    
    public GameInstance(String mapName, List<Player> players, List<Player> spectators)
    {
        if (!mapName.startsWith("map_"))
        {
            mapName = "map_" + mapName;
        }
        
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
        
        this.gameWorld.setGameRuleValue("DO_DAYLIGHT_CYCLE", "false");
        this.gameWorld.setGameRuleValue("DO_MOB_SPAWNING", "false");
        
        Assault.INSTANCE.getLogger().info("Opened new game world: " + gameWorld.getName());
    }
    
    public void sendPlayersToWorld()
    {
        this.gameStage = GameStage.PREGAME;
        
        for (Player player : players)
        {
            player.getInventory().clear();
            player.teleport(gameMap.waitingSpawn.toLocation(this.gameWorld));
            PlayerMode.setPlayerMode(player, PlayerMode.LOBBY, this);
        }
        
        if (spectators != null)
        {
            for (Player player : spectators)
            {
                player.teleport(gameMap.waitingSpawn.toLocation(this.gameWorld));
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
        
        taskCountdownGameStart = new TaskCountdownGameStart(200, 20, 20, this);
        taskCountdownGameStart.runTaskTimer(Assault.INSTANCE, taskCountdownGameStart.startDelay, taskCountdownGameStart.tickDelay);
    }
    
    public void createTeams()
    {
        GameTeam redTeam = new GameTeam(TeamColor.RED, this);
        redTeam.setTeamMapBase();
        redTeam.teamStage = TeamStage.DEFENDING;
        
        GameTeam blueTeam = new GameTeam(TeamColor.BLUE, this);
        blueTeam.setTeamMapBase();
        blueTeam.teamStage = TeamStage.DEFENDING;
        
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
        
        if (this.players.size() == 1)
        {
            teamLists.add(players);
        }
        
        for (java.util.Map.Entry<TeamColor, GameTeam> entry : teams.entrySet())
        {
            entry.getValue().addMembers(teamLists.remove(0));
        }
    }
    
    public GameTeam getPlayerTeam(Player player)
    {
        for (java.util.Map.Entry<TeamColor, GameTeam> team : teams.entrySet())
        {
            
            for (Player teamPlayer : team.getValue().getPlayers())
            {
                if (teamPlayer.equals(player))
                {
                    return team.getValue();
                }
            }
            
        }
        
        return null;
    }
    
    public PlayerMode getPlayerMode(Player player)
    {
        return this.playerModes.get(player);
    }
    
    public void alertLastEnemyLeft(TeamStage remainingTeam)
    {
        if (remainingTeam.equals(TeamStage.ATTACKING))
        {
            for (Player player : this.getPlayers())
            {
                Title title = new Title("Nobody is left on the " + this.getDefendingTeam().color.chatColor + "defending" + ChatColor.RESET + " team!", "Someone may rejoin, so keep going!", 0, 5, 1);
                title.clearTitle(player);
                title.send(player);
                
                // todo: if this happens in the first round, end it
            }
        }
        else
        {
            // lots copied from declareWinners()
            this.overrideWinners = this.getDefendingTeam();
            
            this.finishRound(this.getDefendingTeam());
            
            for (Player player : this.getPlayers())
            {
                PlayerMode playerMode = PlayerMode.setPlayerMode(player, PlayerMode.HAM, this);
                
                String mainTitle = this.getDefendingTeam().color.getFormattedName(true, true, ChatColor.BOLD) + ChatColor.WHITE + " team wins!";
                Title title = new Title(mainTitle, "All of the " + this.getAttackingTeam().color.chatColor + "attackers" + ChatColor.RESET + " left!", 0, 6, 1);
                title.clearTitle(player);
                title.send(player);
            }
            
            long nanosecondsTaken = System.nanoTime() - this.getAttackingTeam().startAttackingTime;
            this.getAttackingTeam().finalAttackingTime = nanosecondsTaken / 1000000000.;
            this.getAttackingTeam().displaySeconds = Util.round(this.getAttackingTeam().finalAttackingTime, 2);
            
            this.updateScoreboards();
            
            this.taskCountdownGameEnd = new TaskCountdownGameEnd(240, 20, 20, this);
            this.taskCountdownGameEnd.runTaskTimer(Assault.INSTANCE, this.taskCountdownGameEnd.startDelay, this.taskCountdownGameEnd.tickDelay);
        }
    }
    
    public void endPrematurely()
    {
        GameTeam remainingTeam = null;
        GameTeam disconnectedTeam = null;
        
        for (GameTeam gameTeam : this.teams.values())
        {
            if (gameTeam.getPlayers().size() > 0)
            {
                remainingTeam = gameTeam;
                continue;
            }
            
            disconnectedTeam = gameTeam;
        }
        
        this.overrideWinners = remainingTeam;
        
        String subtitle = "The " + disconnectedTeam.color.chatColor + "enemy team" + ChatColor.RESET + " disconnected!";
        
        declareWinners(subtitle);
    }
    
    public void startBuildMode()
    {
        createTeams();
        
        this.gameStage = GameStage.BUILDING;
        
        for (GameTeam team : teams.values())
        {
            
            Location objectiveLocation = team.mapBase.objective.toLocation(this.gameWorld);
            objectiveLocation.add(0, 0.5, 0);
            ItemStack objectiveItem = new ItemStack(Material.NETHER_STAR);
            
            Item guidingItem = this.gameWorld.dropItem(objectiveLocation, objectiveItem);
            guidingItem.teleport(objectiveLocation);
            this.guidingObjectives.add(guidingItem);
            
            for (Player player : team.getPlayers())
            {
                GamePlayer gamePlayer = team.getGamePlayer(player);
                gamePlayer.playerBank.coins += 100;
                
                try
                {
                    GameTeam gameTeam = getPlayerTeam(player);
                    gamePlayer.respawn(PlayerMode.BUILDING);
                    
                    Title title = new Title(ChatColor.WHITE + "You are on the " + gameTeam.color.getFormattedName(false, false, ChatColor.BOLD) + ChatColor.WHITE + " team!", "Begin building your defenses!");
                    title.clearTitle(player);
                    
                    title.send(player);
                    
                    PlayerMode.setPlayerMode(player, PlayerMode.BUILDING, this);
                    
                }
                catch (Exception exception)
                {
                    // Player not valid
                }
            }
            
            team.createBuildingShop();
        }
        
        this.updateScoreboards();
        gameMap.clearWaitingPlatform(gameWorld);
        
        this.restoreHealth();
        
        taskCountdownBuilding = new TaskCountdownBuilding(3600, 20, 20, this);
        taskCountdownBuilding.runTaskTimer(Assault.INSTANCE, taskCountdownBuilding.startDelay, taskCountdownBuilding.tickDelay);
    }
    
    public void startAttackMode()
    {
        for (Item item : this.guidingObjectives)
        {
            item.remove();
        }
        
        taskTickTimer = new TaskTickTimer(0, 1, this);
        taskTickTimer.runTaskTimer(Assault.INSTANCE, taskTickTimer.startDelay, taskTickTimer.tickDelay);
        
        this.gameStage = GameStage.ATTACKING;
        
        List<TeamColor> randomList = new ArrayList<>(teams.keySet());
        Collections.shuffle(randomList);
        
        this.attackingTeam = randomList.get(0);
        this.teams.get(this.attackingTeam).teamStage = TeamStage.ATTACKING;
        this.teams.get(randomList.get(1)).teamStage = TeamStage.DEFENDING;
        
        for (GameTeam gameTeam : teams.values())
        {
            gameTeam.displaySeconds = 0;
        }
        
        startRound();
    }
    
    public void swapAttackingTeams()
    {
        teamsGone++;
        
        for (GameTeam gameTeam : teams.values())
        {
            gameTeam.buffList.clear();
        }
        
        TeamColor oldDefenders = this.getDefendingTeam().color;
        this.teams.get(this.attackingTeam).teamStage = TeamStage.DEFENDING;
        this.attackingTeam = oldDefenders;
        this.getAttackingTeam().teamStage = TeamStage.ATTACKING;
        
        startRound();
    }
    
    public void startRound()
    {
        taskGiveCoins = new TaskGiveCoins(0, 100, this, 8);
        taskGiveCoins.runTaskTimer(Assault.INSTANCE, taskGiveCoins.startDelay, taskGiveCoins.tickDelay);
        
        taskAttackTimer = new TaskAttackTimer(0, 20, 20, this);
        taskAttackTimer.runTaskTimer(Assault.INSTANCE, taskAttackTimer.startDelay, taskAttackTimer.tickDelay);
        
        for (GameTeam gameTeam : teams.values())
        {
            switch (gameTeam.teamStage)
            {
                case DEFENDING:
                    for (Player player : gameTeam.getPlayers())
                    {
                        Title title = new Title(gameTeam.color.chatColor + ChatColor.BOLD.toString() + "You" + ChatColor.RESET + " are DEFENDING!", "Defend the " + ChatColor.LIGHT_PURPLE + "nether star" + ChatColor.RESET + " with your life!", 0, 4, 1);
                        title.clearTitle(player);
                        title.send(player);
                    }
                    break;
                case ATTACKING:
                    for (Player player : gameTeam.getPlayers())
                    {
                        Title title = new Title(gameTeam.color.chatColor + ChatColor.BOLD.toString() + "You" + ChatColor.RESET + " are ATTACKING!", "Get to the " + ChatColor.LIGHT_PURPLE + "nether star" + ChatColor.RESET + " as fast as you can!", 0, 4, 1);
                        title.clearTitle(player);
                        title.send(player);
                    }
                    break;
            }
            
            gameTeam.createAttackShop();
        }
        
        this.restoreHealth();
        
        this.getAttackingTeam().startAttackingTime = System.nanoTime();
        
        for (GameTeam gameTeam : teams.values())
        {
            gameTeam.gamerPoints = 0;
            
            for (Player player : gameTeam.getPlayers())
            {
                GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
                gamePlayer.playerBank.coins = 0;
                
                player.getInventory().clear();
                gamePlayer.swapReset();
                
                PlayerMode mode = PlayerMode.setPlayerMode(player, PlayerMode.ATTACKING, this);
                gameTeam.getGamePlayer(player).respawn(mode);
            }
        }
        
        Location objectiveLocation = this.getDefendingTeam().mapBase.objective.toLocation(this.gameWorld);
        objectiveLocation.add(0, 0.5, 0);
        ItemStack objectiveItem = new ItemStack(Material.NETHER_STAR);
        
        Item objectiveEntity = this.gameWorld.dropItem(objectiveLocation, objectiveItem);
        objectiveEntity.teleport(objectiveLocation);
    }
    
    public void finishRound(GameTeam fireworkRecipents)
    {
        this.taskAttackTimer.stopTimer();
        
        List<Player> winners = fireworkRecipents.getPlayers();
        for (Player winPlayer : winners)
        {
            for (int i = 0; i < 3; i++)
            {
                FireworkUtils.spawnRandomFirework(winPlayer.getLocation(), this.getAttackingTeam().color);
            }
        }
        
        for (Player currentPlayer : this.getPlayers())
        {
            GameTeam theirTeam = this.getPlayerTeam(currentPlayer);
            GamePlayer gamePlayer = theirTeam.getGamePlayer(currentPlayer);
            
            if (gamePlayer.taskCountdownRespawn != null)
            {
                gamePlayer.taskCountdownRespawn.finishTimer();
            }
            
            PlayerMode playerMode = PlayerMode.setPlayerMode(currentPlayer, PlayerMode.BETWEEN, this);
        }
    }
    
    public void declareWinners(String subtitle)
    {
        this.gameStage = GameStage.FINISHED;
        
        long nanosecondsTaken = System.nanoTime() - this.getAttackingTeam().startAttackingTime;
        this.getAttackingTeam().finalAttackingTime = nanosecondsTaken / 1000000000.;
        this.getAttackingTeam().displaySeconds = Util.round(this.getAttackingTeam().finalAttackingTime, 2);
        
        this.updateScoreboards();
        
        if (subtitle == null)
        {
            subtitle = "Time: " + ChatColor.LIGHT_PURPLE + Util.secondsToMinutes(Util.round(this.getWinningTeam().finalAttackingTime, 2), false) + ChatColor.WHITE + " seconds";
        }
        
        String mainTitle = this.getWinningTeam().color.getFormattedName(true, true, ChatColor.BOLD) + ChatColor.WHITE + " team wins!";
        Title title = new Title(mainTitle, subtitle, 0, 6, 1);
        
        for (Player currentPlayer : this.gameWorld.getPlayers())
        {
            PlayerMode playerMode = PlayerMode.setPlayerMode(currentPlayer, PlayerMode.HAM, this);
            title.send(currentPlayer);
            //     currentPlayer.sendRawMessage("§c§lC§lo§6§ln§lg§e§lr§la§a§lt§lu§9§ll§la§b§lt§li§5§lo§c§ln§ls§6§l! §r§dYour team wins!");
        }
        
        this.taskCountdownGameEnd = new TaskCountdownGameEnd(240, 20, 20, this);
        this.taskCountdownGameEnd.runTaskTimer(Assault.INSTANCE, this.taskCountdownGameEnd.startDelay, this.taskCountdownGameEnd.tickDelay);
    }
    
    public void endGame()
    {
        for (GameTeam gameTeam : this.teams.values())
        {
            for (Player player : gameTeam.getPlayers())
            {
                gameTeam.teamScoreboard.removePlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
                GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
                gamePlayer.scoreboard.delete();
            }
        }
        
        WorldManager.closeWorld(this.gameWorld);
        List<GameInstance> gameInstances = Assault.gameInstances;
        for (int i = 0; i < gameInstances.size(); i++)
        {
            GameInstance gameInstance = gameInstances.get(i);
            if (gameInstance.equals(this))
            {
                GameInstance removed = Assault.gameInstances.remove(i);
            }
        }
    }
    
    public List<Player> getPlayers()
    {
        List<Player> players = new ArrayList<>();
        for (java.util.Map.Entry<TeamColor, GameTeam> entry : this.teams.entrySet())
        {
            players.addAll(entry.getValue().getPlayers());
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
    
    public void doBuffs()
    {
        for (GameTeam gameTeam : this.teams.values())
        {
            gameTeam.doBuffs();
        }
    }
    
    public void updateScoreboards()
    {
        for (GameTeam gameTeam : this.teams.values())
        {
            for (GamePlayer player : gameTeam.members)
            {
                player.updateScoreboard();
            }
        }
    }
    
    public GameTeam getWinningTeam()
    {
        if (this.overrideWinners != null)
        {
            return overrideWinners;
        }
        
        double lowestTime = Integer.MAX_VALUE;
        GameTeam lowestTeam = null;
        
        for (GameTeam gameTeam : this.teams.values())
        {
            if (gameTeam.finalAttackingTime < lowestTime)
            {
                lowestTeam = gameTeam;
                lowestTime = gameTeam.finalAttackingTime;
            }
        }
        
        return lowestTeam;
    }
    
    public GameTeam getAttackingTeam()
    {
        return teams.get(this.attackingTeam);
    }
    
    public GameTeam getDefendingTeam()
    {
        for (GameTeam gameTeam : this.teams.values())
        {
            if (!gameTeam.color.equals(this.attackingTeam))
            {
                return gameTeam;
            }
        }
        return null;
    }
    
    public void removePlayer(Player player)
    {
        for (GameTeam gameTeam : this.teams.values())
        {
            if (gameTeam.getPlayers().contains(player))
            {
                gameTeam.removeMember(player);
            }
        }
    }
    
    public void addPlayer(Player player)
    {
        if (this.gameStage.equals(GameStage.PREGAME))
        {
            player.teleport(gameMap.waitingSpawn.toLocation(this.gameWorld));
            PlayerMode.setPlayerMode(player, PlayerMode.LOBBY, this);
        }
        
        GameTeam smallestTeam = null;
        int smallestNumber = Integer.MAX_VALUE;
        
        for (GameTeam gameTeam : this.teams.values())
        {
            if (gameTeam.getPlayers().size() < smallestNumber)
            {
                smallestNumber = gameTeam.getPlayers().size();
                smallestTeam = gameTeam;
            }
            else if (gameTeam.getPlayers().size() == smallestNumber)
            {
                gameTeam.addMember(player);
                return;
            }
        }
        
        smallestTeam.addMember(player);
    }
    
    public static GameInstance getPlayerGameInstance(Player player)
    {
        for (GameInstance gameInstance : Assault.gameInstances)
        {
            if (gameInstance.getPlayerTeam(player) != null)
            {
                return gameInstance;
            }
        }
        return null;
    }
    
}
