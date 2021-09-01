package com.thekingelessar.assault.game;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.map.Map;
import com.thekingelessar.assault.game.map.MapBase;
import com.thekingelessar.assault.game.modifiers.GameModifier;
import com.thekingelessar.assault.game.modifiers.PlayerShopModifiers;
import com.thekingelessar.assault.game.modifiers.modifiers.ModInfiniteTime;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.game.timertasks.*;
import com.thekingelessar.assault.game.world.WorldManager;
import com.thekingelessar.assault.util.*;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
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
    
    public static ItemStack gameModifierItemStack = ItemInit.initGameModifierItemStack();
    public HashMap<Player, PlayerShopModifiers> modifierShopMap = new HashMap<>();
    public ModInfiniteTime modInfiniteTime = new ModInfiniteTime(this);
    public List<GameModifier> modifierList = Arrays.asList(modInfiniteTime);
    
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
    
    public GameTeam overrideWinners = null;
    
    public HashMap<Player, Integer> buildingCoinsRemaining = new HashMap<>();
    public int buildingSecondsLeft = 180;
    
    public int teamsGone = 0;
    
    public TaskGiveCoins taskGiveCoins;
    
    public List<Coordinate> placedBlocks = new ArrayList<>();
    public HashMap<FallingBlock, Player> fallingBlockPlayerMap = new HashMap<>();
    public HashMap<String, Player> fallingBlockCoordinateMap = new HashMap<>();
    
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
        
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        
        this.players = new ArrayList<>(players);
        this.spectators = spectators;
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
            
            System.out.println("Player " + player.getName() + " has formatted name: " + player.getDisplayName());
        }
        
        if (spectators != null)
        {
            for (Player player : spectators)
            {
                player.teleport(gameMap.waitingSpawn.toLocation(this.gameWorld));
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
        
        taskCountdownGameStart = new TaskCountdownGameStart(400, 20, 20, this);
        taskCountdownGameStart.runTaskTimer(Assault.INSTANCE, taskCountdownGameStart.startDelay, taskCountdownGameStart.tickDelay);
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
            System.out.println("Player " + player.getName() + " has formatted name: " + player.getDisplayName());
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
    
    public void alertLastEnemyLeft(GameTeam remainingTeam)
    {
        if (this.getPlayers().size() == 0)
        {
            this.endGame();
        }
        
        if (this.gameStage.equals(GameStage.BUILDING))
        {
            // lots copied from declareWinners()
            this.overrideWinners = remainingTeam;
            
            for (Player player : this.getPlayers())
            {
                PlayerMode playerMode = PlayerMode.setPlayerMode(player, PlayerMode.HAM, this);
                
                String mainTitle = remainingTeam.color.getFormattedName(true, true, ChatColor.BOLD) + ChatColor.WHITE + " team wins!";
                Title title = new Title(mainTitle, "All of the " + remainingTeam.getOppositeTeam().color.chatColor + "enemy" + ChatColor.RESET + " left!", 0, 6, 1);
                title.clearTitle(player);
                title.send(player);
            }
            
            this.updateScoreboards();
            
            List<Player> players = this.getWinningTeam().getPlayers();
            for (Player player : players)
            {
                player.sendRawMessage(Assault.assaultPrefix + "Congratulations! " + this.getPlayerTeam(player).color.chatColor + ChatColor.BOLD + "Your team" + ChatColor.RESET + " wins!");
                
                for (int i = 0; i < 5; i++)
                {
                    FireworkUtils.spawnRandomFirework(player.getLocation(), this.getWinningTeam().color);
                }
            }
            
            this.taskCountdownGameEnd = new TaskCountdownGameEnd(240, 20, 20, this);
            this.taskCountdownGameEnd.runTaskTimer(Assault.INSTANCE, this.taskCountdownGameEnd.startDelay, this.taskCountdownGameEnd.tickDelay);
            return;
        }
        
        TeamStage remainingTeamStage = remainingTeam.teamStage;
        
        if (remainingTeamStage.equals(TeamStage.ATTACKING))
        {
            for (Player player : this.getPlayers())
            {
                Title title = new Title(this.getDefendingTeam().color.chatColor + "Defending" + ChatColor.RESET + " team left!", "Someone may rejoin, so keep going!", 0, 5, 1);
                title.clearTitle(player);
                title.send(player);
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
            
            List<Player> players = this.getWinningTeam().getPlayers();
            for (Player player : players)
            {
                player.sendRawMessage(Assault.assaultPrefix + "Congratulations! " + this.getPlayerTeam(player).color.chatColor + ChatColor.BOLD + "Your team" + ChatColor.RESET + " wins!");
                
                for (int i = 0; i < 5; i++)
                {
                    FireworkUtils.spawnRandomFirework(player.getLocation(), this.getWinningTeam().color);
                }
            }
            
            this.taskCountdownGameEnd = new TaskCountdownGameEnd(240, 20, 20, this);
            this.taskCountdownGameEnd.runTaskTimer(Assault.INSTANCE, this.taskCountdownGameEnd.startDelay, this.taskCountdownGameEnd.tickDelay);
        }
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
    
    public void endPrematurely()
    {
        GameTeam remainingTeam = getRemainingTeam();
        GameTeam disconnectedTeam = this.getOppositeTeam(remainingTeam);
        
        this.overrideWinners = remainingTeam;
        
        String subtitle = "The " + disconnectedTeam.color.chatColor + "enemy team" + ChatColor.RESET + " disconnected!";
        
        declareWinners(subtitle, false);
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
                gamePlayer.spawn(PlayerMode.BUILDING);
                
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
        
        this.gameStage = GameStage.ATTACKING;
        
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
        
        emeraldSpawnTimer = new TaskEmeraldSpawnTimer(0, 0, 1, this, 20);
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
                gameTeam.getGamePlayer(player).spawn(mode);
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
    
    public void finishRound(GameTeam fireworkRecipents)
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
        
        List<Player> winners = fireworkRecipents.getPlayers();
        for (Player winPlayer : winners)
        {
            for (int i = 0; i < 3; i++)
            {
                FireworkUtils.spawnRandomFirework(winPlayer.getLocation(), fireworkRecipents.color);
            }
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
    }
    
    public void declareWinners(String subtitle, boolean forfeit)
    {
        this.gameStage = GameStage.FINISHED;
        
        if (this.getAttackingTeam() == null)
        {
            alertLastEnemyLeft(this.getRemainingTeam());
        }
        
        long nanosecondsTaken = System.nanoTime() - this.getAttackingTeam().startAttackingTime;
        this.getAttackingTeam().finalAttackingTime = nanosecondsTaken / 1000000000.;
        this.getAttackingTeam().displaySeconds = Util.round(this.getAttackingTeam().finalAttackingTime, 2);
        
        if (forfeit)
        {
            this.getAttackingTeam().finalAttackingTime = 481;
            this.getAttackingTeam().displaySeconds = Util.round(this.getAttackingTeam().finalAttackingTime, 2);
        }
        
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
        }
        
        List<Player> players = this.getWinningTeam().getPlayers();
        for (Player player : players)
        {
            player.sendRawMessage(Assault.assaultPrefix + "Congratulations! " + this.getPlayerTeam(player).color.chatColor + ChatColor.BOLD + "Your team" + ChatColor.RESET + " wins!");
            
            for (int i = 0; i < 5; i++)
            {
                FireworkUtils.spawnRandomFirework(player.getLocation(), this.getWinningTeam().color);
            }
        }
        
        this.taskCountdownGameEnd = new TaskCountdownGameEnd(240, 20, 20, this);
        this.taskCountdownGameEnd.runTaskTimer(Assault.INSTANCE, this.taskCountdownGameEnd.startDelay, this.taskCountdownGameEnd.tickDelay);
    }
    
    public void endGame()
    {
        for (GameTeam gameTeam : teams.values())
        {
            gameTeam.mapBase.destroyShops();
        }
        
        if (this.taskTickTimer != null)
        {
            this.taskTickTimer.cancel();
        }
        
        if (taskCountdownBuilding != null)
        {
            taskCountdownBuilding.cancel();
        }
        
        for (GameTeam gameTeam : this.teams.values())
        {
            gameTeam.buffList.clear();
            
            for (Player player : gameTeam.getPlayers())
            {
                gameTeam.teamScoreboard.removePlayer(player);
                GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
                gamePlayer.scoreboard.delete();
            }
            
            gameTeam.teamScoreboard.unregister();
        }
        
        WorldManager.closeWorld(this.gameWorld);
        Assault.gameInstances.remove(this);
    }
    
    public void endPreGame()
    {
        for (Player player : this.gameWorld.getPlayers())
        {
            Title title = new Title();
            title.clearTitle(player);
            player.playSound(player.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
            player.sendRawMessage(Assault.assaultPrefix + "Not enough players! :(");
        }
        
        WorldManager.closeWorld(this.gameWorld);
        List<GameInstance> gameInstances = Assault.gameInstances;
        Assault.gameInstances.remove(this);
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
            
            if (mapBase.teamBuffShopNPC != null)
            {
                Entity npcEntity = mapBase.teamBuffShopNPC.getEntity();
                if (npcEntity instanceof SkinnableEntity)
                {
                    ((SkinnableEntity) npcEntity).getSkinTracker().updateViewer(player);
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
