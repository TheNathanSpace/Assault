package com.thekingelessar.assault.game;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.modifiers.GameModifier;
import com.thekingelessar.assault.game.modifiers.PlayerShopModifiers;
import com.thekingelessar.assault.game.modifiers.modifiers.ModFirstToFive;
import com.thekingelessar.assault.game.modifiers.modifiers.ModInfiniteTime;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.game.timertasks.*;
import com.thekingelessar.assault.game.world.WorldManager;
import com.thekingelessar.assault.game.world.map.Map;
import com.thekingelessar.assault.game.world.map.MapBase;
import com.thekingelessar.assault.util.*;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameInstance
{
    public UUID gameUUID = UUID.randomUUID();
    public String gameID;
    
    public GameStage gameStage;
    
    public Map gameMap;
    public World gameWorld;
    
    public HashMap<TeamColor, GameTeam> teams = new HashMap<>();
    public Scoreboard scoreboard;
    
    private final List<Player> players;
    private final List<Player> spectators;
    
    public ModInfiniteTime modInfiniteTime = new ModInfiniteTime(this);
    public ModFirstToFive modFirstToFive = new ModFirstToFive(this);
    
    public static ItemStack gameModifierItemStack = ItemInit.initGameModifierItemStack();
    public HashMap<Player, PlayerShopModifiers> modifierShopMap = new HashMap<>();
    public List<GameModifier> modifierList = Arrays.asList(modInfiniteTime, modFirstToFive);
    
    public HashMap<Player, PlayerMode> playerModes = new HashMap<>();
    
    public TaskTickTimer taskTickTimer;
    public TaskCountdownGameStart taskCountdownGameStart;
    public TaskCountdownBuilding taskCountdownBuilding;
    public TaskAttackTimer taskAttackTimer;
    public TaskCountdownSwapAttackers taskCountdownSwapAttackers;
    public TaskCountdownGameEnd taskCountdownGameEnd;
    public TaskCountdownAttackEnd taskCountdownAttackEnd;
    
    public TaskEmeraldSpawnTimer emeraldSpawnTimer;
    public Hologram emeraldSpawnHologram;
    public TextLine line2;
    
    public TeamColor attackingTeam;
    
    public GameTeam winningTeam = null;
    public GameEndManager gameEndManager;
    
    public HashMap<Player, Integer> buildingCoinsRemaining = new HashMap<>();
    public int buildingSecondsLeft;
    
    public int teamsGone = 0;
    
    public TaskGiveCoins taskGiveCoins;
    
    public List<BukkitRunnable> allTimers = Arrays.asList(taskTickTimer, taskCountdownGameStart, taskCountdownBuilding, taskAttackTimer, taskCountdownSwapAttackers, taskCountdownGameEnd, taskCountdownAttackEnd, emeraldSpawnTimer, taskGiveCoins);
    
    public List<Coordinate> placedBlocks = new ArrayList<>();
    public HashMap<FallingBlock, Player> fallingBlockPlayerMap = new HashMap<>();
    public HashMap<String, Player> fallingBlockCoordinateMap = new HashMap<>();
    
    public List<ThrownPotion> fixedPotions = new ArrayList<>();
    
    public HashMap<GameTeam, Item> guidingObjectives = new HashMap<>();
    public HashMap<GameTeam, Item> currentObjective = new HashMap<>();
    
    public HashMap<Player, Player> lastDamagedBy = new HashMap<>();
    
    public GameInstance(String mapName, List<Player> players, List<Player> spectators)
    {
        if (!mapName.startsWith("map_"))
        {
            mapName = "map_" + mapName;
        }
        
        this.gameMap = Assault.maps.get(mapName);
        this.gameID = mapName + "_" + gameUUID.toString();
        
        this.buildingSecondsLeft = this.gameMap.buildingTime;
        
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        
        this.players = new ArrayList<>(players);
        this.spectators = spectators;
        
        this.gameEndManager = new GameEndManager(this);
    }
    
    public void startWorld()
    {
        this.gameWorld = WorldManager.createWorldFromMap(this.gameMap.mapName, false, this.gameID);
        
        this.gameWorld.setGameRuleValue("DO_DAYLIGHT_CYCLE", "false");
        this.gameWorld.setGameRuleValue("DO_MOB_SPAWNING", "false");
        
        int randomTime = ThreadLocalRandom.current().nextInt(0, 24000 + 1);
        this.gameWorld.setTime(randomTime);
        
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
            
            player.getInventory().setItem(8, GameInstance.gameModifierItemStack.clone());
            this.modifierShopMap.put(player, new PlayerShopModifiers(this, player));
        }
        
        if (spectators != null)
        {
            for (Player player : spectators)
            {
                player.teleport(gameMap.waitingSpawn.toLocation(this.gameWorld));
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
        
        taskCountdownGameStart = new TaskCountdownGameStart(Assault.gameStartCountdown * 20, 20, 20, this);
        taskCountdownGameStart.runTaskTimer(Assault.INSTANCE, taskCountdownGameStart.startDelay, taskCountdownGameStart.tickDelay);
    }
    
    public void endPreGame()
    {
        for (Player player : this.gameWorld.getPlayers())
        {
            Title title = new Title();
            title.clearTitle(player);
            try
            {
                player.playSound(player.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
            }
            catch (Throwable throwable)
            {
                player.playSound(player.getLocation(), Sound.valueOf("ENTITY_SKELETON_HURT"), 1.0F, 1.0F);
            }
            player.sendRawMessage(Assault.ASSAULT_PREFIX + "Not enough players! :(");
        }
        
        WorldManager.closeWorld(this.gameWorld);
        List<GameInstance> gameInstances = Assault.gameInstances;
        Assault.gameInstances.remove(this);
    }
    
    public void updateModShops()
    {
        for (PlayerShopModifiers playerShopModifiers : this.modifierShopMap.values())
        {
            playerShopModifiers.updateCounts();
        }
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
        
        List<List<Player>> teamLists = new ArrayList<>();
        
        int numberOfTeams = 2;
        int halfwayPoint = players.size() / numberOfTeams;
        int start = 0;
        int end = halfwayPoint;
        for (int i = 0; i < numberOfTeams; i++)
        {
            List<Player> sublist = players.subList(start, end);
            end = players.size();
            start = halfwayPoint;
            
            teamLists.add(sublist);
        }
        
        for (GameTeam gameTeam : teams.values())
        {
            gameTeam.addMembers(teamLists.remove(0));
        }
        
        this.modifierShopMap.clear();
        this.modifierShopMap = null;
        
        for (Player player : this.getPlayers())
        {
            player.getInventory().clear();
            GamePlayer gamePlayer = this.getGamePlayer(player);
            gamePlayer.swapReset();
        }
    }
    
    public GameTeam getPlayerTeam(Player player)
    {
        for (GameTeam gameTeam : teams.values())
        {
            
            for (Player teamPlayer : gameTeam.getPlayers())
            {
                if (teamPlayer.equals(player))
                {
                    return gameTeam;
                }
            }
            
        }
        
        return null;
    }
    
    public PlayerMode getPlayerMode(Player player)
    {
        return this.playerModes.get(player);
    }
    
    public GamePlayer getGamePlayer(Player player)
    {
        if (this.getPlayerTeam(player) != null)
        {
            return this.getPlayerTeam(player).getGamePlayer(player);
        }
        
        return null;
    }
    
    public void alertTeamleft(GameTeam remainingTeam)
    {
        if (this.getPlayers().size() == 0)
        {
            this.gameEndManager.cleanupGameInstance();
        }
        
        if (this.gameStage.equals(GameStage.BUILDING))
        {
            this.winningTeam = this.getRemainingTeam();
            this.gameEndManager.declareWinners(GameEndManager.WinState.BUILDING_LEFT);
        }
        
        TeamStage remainingTeamStage = remainingTeam.teamStage;
        
        if (remainingTeamStage.equals(TeamStage.ATTACKING))
        {
            for (Player player : this.getAttackingTeam().getPlayers())
            {
                Title title = new Title(this.getDefendingTeam().color.chatColor + "Defending" + ChatColor.RESET + " team left!", "Someone may rejoin, so keep going!", 0, 5, 1);
                title.clearTitle(player);
                title.send(player);
            }
        }
        else
        {
            this.winningTeam = this.getDefendingTeam();
            this.endRound(true);
            this.gameEndManager.declareWinners(GameEndManager.WinState.ATTACKERS_LEFT);
        }
    }
    
    public boolean isOneTeamRemaining()
    {
        boolean teamLeft = false;
        for (GameTeam gameTeam : this.teams.values())
        {
            if (gameTeam.getPlayers().size() == 0)
            {
                teamLeft = true;
            }
        }
        return teamLeft;
    }
    
    public GameTeam getRemainingTeam()
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
        
        return remainingTeam;
    }
    
    public void startBuildMode()
    {
        this.gameStage = GameStage.BUILDING;
        
        createTeams();
        
        boolean intro = false;
        for (GameModifier gameModifier : modifierList)
        {
            boolean enabled = gameModifier.setEnabled();
            
            if (enabled)
            {
                if (!intro)
                {
                    for (Player player : this.getPlayers())
                    {
                        player.sendMessage("§5§l[§d§lGame Modifiers§5§l]" + ChatColor.RESET);
                    }
                    intro = true;
                }
                
                for (Player player : this.getPlayers())
                {
                    player.sendMessage(" - " + gameModifier.name);
                }
            }
        }
        
        for (Player player : this.getPlayers())
        {
            for (PotionEffect effect : player.getActivePotionEffects())
            {
                player.removePotionEffect(effect.getType());
            }
        }
        
        for (GameTeam team : teams.values())
        {
            Location objectiveLocation = team.mapBase.objective.toLocation(this.gameWorld);
            objectiveLocation.add(0, 0.5, 0);
            ItemStack objectiveItem = new ItemStack(Material.NETHER_STAR);
            
            Item guidingItem = this.gameWorld.dropItem(objectiveLocation, objectiveItem);
            
            Vector velocity = guidingItem.getVelocity();
            velocity.setX(0);
            velocity.setY(0);
            velocity.setZ(0);
            guidingItem.setVelocity(velocity);
            
            guidingItem.teleport(objectiveLocation);
            this.guidingObjectives.put(team, guidingItem);
            
            for (Player player : team.getPlayers())
            {
                GamePlayer gamePlayer = team.getGamePlayer(player);
                
                buildingCoinsRemaining.put(player, 100);
                
                GameTeam gameTeam = getPlayerTeam(player);
                gamePlayer.spawn(PlayerMode.BUILDING, false);
                
                Title title = new Title(ChatColor.WHITE + "You are on the " + gameTeam.color.getFormattedName(false, false, ChatColor.BOLD) + ChatColor.WHITE + " team!", "Begin building your defenses!");
                title.clearTitle(player);
                
                title.send(player);
                
                PlayerMode.setPlayerMode(player, PlayerMode.BUILDING, this);
            }
            
            team.createBuildingShop();
            team.mapBase.spawnShops(this);
        }
        
        this.updateScoreboards();
        gameMap.clearWaitingPlatform(gameWorld);
        
        this.restoreHealth();
        
        taskCountdownBuilding = new TaskCountdownBuilding(3600, 20, 20, this);
        taskCountdownBuilding.runTaskTimer(Assault.INSTANCE, taskCountdownBuilding.startDelay, taskCountdownBuilding.tickDelay);
    }
    
    public void startAttackMode()
    {
        for (Item item : this.guidingObjectives.values())
        {
            item.remove();
        }
        
        taskTickTimer = new TaskTickTimer(0, 1, this);
        taskTickTimer.runTaskTimer(Assault.INSTANCE, taskTickTimer.startDelay, taskTickTimer.tickDelay);
        
        this.gameStage = GameStage.ATTACK_ROUNDS;
        
        List<TeamColor> randomList = new ArrayList<>(teams.keySet());
        Collections.shuffle(randomList);
        
        this.attackingTeam = randomList.get(0);
        this.teams.get(this.attackingTeam).teamStage = TeamStage.ATTACKING;
        this.teams.get(randomList.get(1)).teamStage = TeamStage.DEFENDING;
        
        for (GameTeam gameTeam : teams.values())
        {
            gameTeam.mapBase.destroyShops();
            gameTeam.mapBase.spawnShops(this);
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
        
        for (Player player : this.getPlayers())
        {
            for (PotionEffect effect : player.getActivePotionEffects())
            {
                player.removePotionEffect(effect.getType());
            }
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
        
        emeraldSpawnTimer = new TaskEmeraldSpawnTimer(0, 0, 1, this, this.gameMap.emeraldSpawnDelay);
        emeraldSpawnTimer.runTaskTimer(Assault.INSTANCE, emeraldSpawnTimer.startDelay, emeraldSpawnTimer.tickDelay);
        
        if (Assault.useHolographicDisplays)
        {
            Location hologramLocation = this.getDefendingTeam().mapBase.emeraldSpawns.get(0).toLocation(this.gameWorld);
            hologramLocation.setY(hologramLocation.getY() + 3.5);
            
            emeraldSpawnHologram = HologramsAPI.createHologram(Assault.INSTANCE, hologramLocation);
            TextLine line1 = emeraldSpawnHologram.appendTextLine("§a§lEmerald Spawn");
            line2 = emeraldSpawnHologram.appendTextLine("§rSpawning in §d%s§r seconds!");
        }
        
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
                gameTeam.getGamePlayer(player).spawn(mode, false);
            }
        }
        
        for (GameTeam gameTeam : teams.values())
        {
            gameTeam.mapBase.destroyShops();
        }
        
        this.getDefendingTeam().mapBase.spawnShops(this);
        this.getAttackingTeam().displaySeconds = 0;
        
        Location objectiveLocation = this.getDefendingTeam().mapBase.objective.toLocation(this.gameWorld);
        objectiveLocation.add(0, 0.5, 0);
        ItemStack objectiveItem = new ItemStack(Material.NETHER_STAR);
        
        Item objectiveEntity = this.gameWorld.dropItem(objectiveLocation, objectiveItem);
        
        Vector velocity = objectiveEntity.getVelocity();
        velocity.setX(0);
        velocity.setY(0);
        velocity.setZ(0);
        objectiveEntity.setVelocity(velocity);
        
        objectiveEntity.teleport(objectiveLocation);
        
        this.currentObjective.put(getDefendingTeam(), objectiveEntity);
    }
    
    public boolean isTie()
    {
        boolean isTimeTie = false;
        
        int triedTeam = 0;
        double firstTime = 0;
        
        for (GameTeam gameTeam : this.teams.values())
        {
            if (gameTeam.finalAttackingTime == 0)
            {
                isTimeTie = false;
                break;
            }
            if (triedTeam == 0)
            {
                firstTime = gameTeam.finalAttackingTime;
                triedTeam++;
            }
            else
            {
                if (firstTime == gameTeam.finalAttackingTime)
                {
                    isTimeTie = true;
                }
            }
        }
        
        boolean isStarTie = false;
        if (isTimeTie && this.modFirstToFive.enabled)
        {
            triedTeam = 0;
            int firstStars = 0;
            
            for (GameTeam gameTeam : this.teams.values())
            {
                if (triedTeam == 0)
                {
                    firstStars = gameTeam.starsPickedUp;
                    triedTeam++;
                }
                else
                {
                    if (firstStars == gameTeam.starsPickedUp)
                    {
                        isStarTie = true;
                    }
                }
            }
        }
        
        return isTimeTie && isStarTie;
    }
    
    public boolean isTimeTie()
    {
        boolean isTimeTie = false;
        
        int triedTeam = 0;
        double firstTime = 0;
        
        for (GameTeam gameTeam : this.teams.values())
        {
            if (gameTeam.finalAttackingTime == 0)
            {
                isTimeTie = false;
                break;
            }
            if (triedTeam == 0)
            {
                firstTime = gameTeam.finalAttackingTime;
                triedTeam++;
            }
            else
            {
                if (firstTime == gameTeam.finalAttackingTime)
                {
                    isTimeTie = true;
                }
            }
        }
        
        return isTimeTie;
    }
    
    public void endRound(boolean attackersForfeit)
    {
        if (this.taskAttackTimer != null)
        {
            this.taskAttackTimer.stopTimer();
        }
        
        if (this.emeraldSpawnTimer != null)
        {
            this.emeraldSpawnTimer.stopTimer();
        }
        
        if (this.taskTickTimer != null)
        {
            this.taskTickTimer.cancel();
        }
        
        if (this.getAttackingTeam() != null)
        {
            if (this.getAttackingTeam().finalAttackingTime != this.gameMap.attackTimeLimit && this.getAttackingTeam().finalAttackingTime != this.gameMap.firstToFiveStarsTimeLimit)
            {
                long nanosecondsTaken = System.nanoTime() - this.getAttackingTeam().startAttackingTime;
                this.getAttackingTeam().finalAttackingTime = nanosecondsTaken / 1000000000.;
            }
        }
        
        this.getAttackingTeam().displaySeconds = Util.round(this.getAttackingTeam().finalAttackingTime, 2);
        
        if (attackersForfeit)
        {
            this.getAttackingTeam().finalAttackingTime = Integer.MAX_VALUE;
            this.getAttackingTeam().displaySeconds = -999;
        }
        
        for (Player currentPlayer : this.getPlayers())
        {
            for (PotionEffect effect : currentPlayer.getActivePotionEffects())
            {
                currentPlayer.removePotionEffect(effect.getType());
            }
            
            GameTeam theirTeam = this.getPlayerTeam(currentPlayer);
            GamePlayer gamePlayer = theirTeam.getGamePlayer(currentPlayer);
            
            if (gamePlayer.taskCountdownRespawn != null)
            {
                gamePlayer.taskCountdownRespawn.finishTimer();
            }
            
            PlayerMode playerMode = PlayerMode.setPlayerMode(currentPlayer, PlayerMode.BETWEEN, this);
        }
        
        if (!attackersForfeit)
        {
            List<Player> winners = this.getAttackingTeam().getPlayers();
            for (Player winPlayer : winners)
            {
                for (int i = 0; i < 3; i++)
                {
                    FireworkUtils.spawnRandomFirework(winPlayer.getLocation(), this.getAttackingTeam().color);
                }
            }
        }
        
        if (this.teamsGone == 1)
        {
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
            
            this.winningTeam = lowestTeam;
            
            if (this.modFirstToFive.enabled && this.isTie())
            {
                int mostStars = Integer.MIN_VALUE;
                GameTeam mostStarsTeam = null;
                
                for (GameTeam gameTeam : this.teams.values())
                {
                    if (gameTeam.starsPickedUp > mostStars)
                    {
                        mostStarsTeam = gameTeam;
                        mostStars = gameTeam.starsPickedUp;
                    }
                }
                
                this.winningTeam = mostStarsTeam;
            }
        }
        else if (this.isOneTeamRemaining())
        {
            this.winningTeam = this.getRemainingTeam();
        }
        else
        {
            this.taskCountdownSwapAttackers = new TaskCountdownSwapAttackers(200, 0, 20, this);
            this.taskCountdownSwapAttackers.runTaskTimer(Assault.INSTANCE, this.taskCountdownSwapAttackers.startDelay, this.taskCountdownSwapAttackers.tickDelay);
        }
        
        this.updateScoreboards();
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
    
    public GameTeam getRedTeam()
    {
        return this.teams.get(TeamColor.RED);
    }
    
    public GameTeam getBlueTeam()
    {
        return this.teams.get(TeamColor.BLUE);
    }
    
    public GameTeam getOppositeTeam(Player player)
    {
        for (GameTeam gameTeam : this.teams.values())
        {
            if (!gameTeam.equals(this.getPlayerTeam(player)))
            {
                return gameTeam;
            }
        }
        return null;
    }
    
    public GameTeam getOppositeTeam(GameTeam gameTeam)
    {
        for (GameTeam overTeam : this.teams.values())
        {
            if (!overTeam.equals(gameTeam))
            {
                return overTeam;
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
    
    public void removePlayerPreGame(Player player)
    {
        this.players.remove(player);
        
        if (this.players.size() == 1)
        {
            this.endPreGame();
        }
    }
    
    public void addPlayer(Player player)
    {
        if (this.gameStage.equals(GameStage.PREGAME))
        {
            player.teleport(gameMap.waitingSpawn.toLocation(this.gameWorld));
            PlayerMode.setPlayerMode(player, PlayerMode.LOBBY, this);
            this.players.add(player);
            return;
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
    
    public void updateShopSkins(Player player)
    {
        for (MapBase mapBase : this.gameMap.bases)
        {
            for (NPC npc : mapBase.itemShopNPCs)
            {
                Entity npcEntity = npc.getEntity();
                if (npcEntity instanceof SkinnableEntity)
                {
                    ((SkinnableEntity) npcEntity).getSkinTracker().updateViewer(player);
                }
            }
            
            if (mapBase.teamBuffShopNPCs.size() != 0)
            {
                for (NPC npc : mapBase.teamBuffShopNPCs)
                {
                    Entity npcEntity = npc.getEntity();
                    if (npcEntity instanceof SkinnableEntity)
                    {
                        ((SkinnableEntity) npcEntity).getSkinTracker().updateViewer(player);
                    }
                }
            }
        }
    }
    
    public static GameInstance getPlayerGameInstance(Player player)
    {
        for (GameInstance gameInstance : Assault.gameInstances)
        {
            if (gameInstance.getPlayerTeam(player) != null)
            {
                return gameInstance;
            }
            
            if (gameInstance.players.contains(player))
            {
                return gameInstance;
            }
        }
        return null;
    }
    
    public static GameInstance getWorldGameInstance(World world)
    {
        for (GameInstance gameInstance : Assault.gameInstances)
        {
            if (gameInstance.gameWorld.equals(world))
            {
                return gameInstance;
            }
        }
        
        return null;
    }
    
}
