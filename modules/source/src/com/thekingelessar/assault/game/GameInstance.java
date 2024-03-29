package com.thekingelessar.assault.game;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.ShopUtil;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItem;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.pregame.modifiers.GameModifier;
import com.thekingelessar.assault.game.pregame.modifiers.PlayerShopModifiers;
import com.thekingelessar.assault.game.pregame.modifiers.modifiers.*;
import com.thekingelessar.assault.game.pregame.teamselection.PlayerShopTeamSelection;
import com.thekingelessar.assault.game.pregame.teamselection.SelectedTeam;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.game.timertasks.*;
import com.thekingelessar.assault.game.world.WorldManager;
import com.thekingelessar.assault.game.world.map.MapBase;
import com.thekingelessar.assault.util.Coordinate;
import com.thekingelessar.assault.util.FireworkUtils;
import com.thekingelessar.assault.util.ItemInit;
import com.thekingelessar.assault.util.Util;
import com.thekingelessar.assault.util.version.XMaterial;
import com.thekingelessar.assault.util.version.XSound;
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
    
    public com.thekingelessar.assault.game.world.map.Map gameMap;
    public World gameWorld;
    
    public List<GameTeam> teams = new ArrayList<>();
    public Scoreboard scoreboard;
    
    private final List<Player> players;
    private final List<Player> spectators;
    
    public ModInfiniteTime modInfiniteTime = new ModInfiniteTime(this);
    public ModFirstTo5Stars modFirstTo5Stars = new ModFirstTo5Stars(this);
    public ModDisableWildcardItems modDisableWildcardItems = new ModDisableWildcardItems(this);
    public ModDontUseTeamSelection modDontUseTeamSelector = new ModDontUseTeamSelection(this);
    public ModManualStar modManualStar = new ModManualStar(this);
    
    public static ItemStack gameModifierItemStack = ItemInit.initGameModifierItemStack();
    public HashMap<Player, PlayerShopModifiers> modifierShopMap = new HashMap<>();
    public List<GameModifier> modifierList = Arrays.asList(modInfiniteTime, modFirstTo5Stars, modDisableWildcardItems, modDontUseTeamSelector, modManualStar);
    
    public static ItemStack teamSelectionItemStack = ItemInit.initTeamSelectionItemStack();
    public HashMap<Player, PlayerShopTeamSelection> teamSelectionShopMap = new HashMap<>();
    public List<SelectedTeam> selectedTeamList = new ArrayList<>();
    
    public static ItemStack manualPlacementStar = ItemInit.initManualStar();
    public static ItemStack retrieveObjectiveItem = ItemInit.initRetrieveStar();
    
    public HashMap<Player, PlayerMode> playerModes = new HashMap<>();
    
    public TaskTickTimer taskTickTimer;
    public TaskCountdownGameStart taskCountdownGameStart;
    public TaskCountdownBuilding taskCountdownBuilding;
    public TaskAttackTimer taskAttackTimer;
    public TaskCountdownSwapAttackers taskCountdownSwapAttackers;
    public TaskCountdownGameEnd taskCountdownGameEnd;
    public TaskCountdownAttackEnd taskCountdownAttackEnd;
    
    public TaskEmeraldSpawnTimer emeraldSpawnTimer;
    public List<Hologram> emeraldSpawnHologram = new ArrayList<>();
    public TextLine line2;
    
    public GameTeam attackingTeam;
    
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
    
    public HashMap<Player, Player> lastDamagedBy = new HashMap<>();
    
    public HashMap<UUID, Integer> killsInGame = new HashMap<>();
    public HashMap<UUID, Integer> deathsInGame = new HashMap<>();
    public HashMap<UUID, Integer> starsInGame = new HashMap<>();
    
    public List<ShopItem> randomShopItems = new ArrayList<>();
    
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
        for (MapBase mapBase : this.gameMap.bases)
        {
            TeamColor teamColor = mapBase.teamColor;
            
            SelectedTeam selectedTeam = new SelectedTeam(this, teamColor);
            this.selectedTeamList.add(selectedTeam);
        }
        
        this.gameStage = GameStage.PREGAME;
        
        for (Player player : players)
        {
            player.getInventory().clear();
            player.teleport(gameMap.waitingSpawn.toLocation(this.gameWorld));
            PlayerMode.setPlayerMode(player, PlayerMode.LOBBY, this);
            
            player.getInventory().setItem(7, GameInstance.teamSelectionItemStack.clone());
            this.teamSelectionShopMap.put(player, new PlayerShopTeamSelection(this, player));
            
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
    
    public void endPreGame(String message)
    {
        for (Player player : this.gameWorld.getPlayers())
        {
            player.resetTitle();
            player.playSound(player.getLocation(), XSound.ENTITY_SKELETON_HURT.parseSound(), 1.0F, 1.0F);
            player.sendRawMessage(message);
        }
        this.taskCountdownGameStart.cancelTimer();
        WorldManager.closeWorld(this.gameWorld);
        Assault.gameInstances.remove(this);
    }
    
    public void updateModShops()
    {
        for (PlayerShopModifiers playerShopModifiers : this.modifierShopMap.values())
        {
            playerShopModifiers.updateCounts();
        }
    }
    
    public void updateTeamSelectionShops()
    {
        for (PlayerShopTeamSelection playerShopTeamSelection : this.teamSelectionShopMap.values())
        {
            playerShopTeamSelection.updateCounts();
        }
    }
    
    public boolean createTeams()
    {
        GameTeam teamOne = new GameTeam(this.gameMap.getTeamOne().teamColor, this);
        teamOne.setTeamMapBase();
        teamOne.teamStage = TeamStage.DEFENDING;
        
        GameTeam teamTwo = new GameTeam(this.gameMap.getTeamTwo().teamColor, this);
        teamTwo.setTeamMapBase();
        teamTwo.teamStage = TeamStage.DEFENDING;
        
        teams.add(teamOne);
        teams.add(teamTwo);
        
        this.modDontUseTeamSelector.setEnabled();
        if (!this.modDontUseTeamSelector.enabled)
        {
            for (SelectedTeam selectedTeam : this.selectedTeamList)
            {
                for (GameTeam gameTeam : this.teams)
                {
                    if (gameTeam.color.equals(selectedTeam.teamColor))
                    {
                        gameTeam.addMembers(selectedTeam.members);
                        this.players.removeAll(selectedTeam.members);
                        for (Player player : selectedTeam.members)
                        {
                            System.out.println(String.format("Forced: Added %s to %s team", player.getName(), gameTeam.color));
                        }
                    }
                }
            }
        }
        
        GameTeam lowerTeam = null;
        int lowestMembers = Integer.MAX_VALUE;
        Random generator = new Random();
        while (this.players.size() != 0)
        {
            for (GameTeam gameTeam : this.teams)
            {
                if (gameTeam.members.size() <= lowestMembers)
                {
                    lowerTeam = gameTeam;
                    lowestMembers = gameTeam.members.size();
                }
            }
            
            Player player = this.players.get(generator.nextInt(this.players.size()));
            this.players.remove(player);
            lowerTeam.addMember(player);
            System.out.println(String.format("Random: Added %s to %s team", player.getName(), lowerTeam.color));
        }
        
        for (GameTeam gameTeam : this.teams)
        {
            if (gameTeam.members.size() == 0)
            {
                this.endPreGame(Assault.ASSAULT_PREFIX + "Ending game—there's nobody on " + gameTeam.color.getFormattedName(false, false, ChatColor.BOLD) + Util.RESET_CHAT + "!");
                return false;
            }
        }
        
        //        Collections.shuffle(this.players);
        //
        //        List<List<Player>> teamLists = new ArrayList<>();
        //
        //        int numberOfTeams = 2;
        //        int halfwayPoint = players.size() / numberOfTeams;
        //        int start = 0;
        //        int end = halfwayPoint;
        //        for (int i = 0; i < numberOfTeams; i++)
        //        {
        //            List<Player> sublist = players.subList(start, end);
        //            end = players.size();
        //            start = halfwayPoint;
        //
        //            teamLists.add(sublist);
        //        }
        //
        //        for (GameTeam gameTeam : teams)
        //        {
        //            gameTeam.addMembers(teamLists.remove(0));
        //        }
        
        this.modifierShopMap.clear();
        this.modifierShopMap = null;
        
        this.teamSelectionShopMap.clear();
        this.teamSelectionShopMap = null;
        
        return true;
    }
    
    public GameTeam getPlayerTeam(Player player)
    {
        for (GameTeam gameTeam : teams)
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
            return;
        }
        
        if (this.gameStage.equals(GameStage.BUILDING))
        {
            this.winningTeam = this.getRemainingTeam();
            this.gameEndManager.declareWinners(GameEndManager.WinState.BUILDING_LEFT);
            return;
        }
        
        TeamStage remainingTeamStage = remainingTeam.teamStage;
        
        if (remainingTeamStage.equals(TeamStage.ATTACKING))
        {
            for (Player player : this.getAttackingTeam().getPlayers())
            {
                player.sendTitle(this.getDefendingTeam().color.chatColor + "Defending" + Util.RESET_CHAT + " team left!", "Someone may rejoin, so keep going!", 0, 5, 1);
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
        for (GameTeam gameTeam : this.teams)
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
        
        for (GameTeam gameTeam : this.teams)
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
        boolean success = createTeams();
        if (!success)
        {
            return;
        }
        
        this.gameStage = GameStage.BUILDING;
        
        for (Player player : this.getPlayers())
        {
            player.getInventory().clear();
            GamePlayer gamePlayer = this.getGamePlayer(player);
            gamePlayer.swapReset();
        }
        
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
                        player.sendMessage("§5§l[§d§lGame Modifiers§5§l]" + Util.RESET_CHAT);
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
        
        ItemStack shopItemBoughtStack = new ItemStack(Util.getRandomItemStack());
        int coinCost = Util.weightedInt(6, 16, 1, 64);
        ItemStack shopItemStack = ShopUtil.constructShopItemStack(shopItemBoughtStack.clone(), ChatColor.BLUE + ChatColor.BOLD.toString() + "Wildcard: " + Util.RESET_CHAT + XMaterial.matchXMaterial(shopItemBoughtStack).toString(), coinCost, Currency.COINS);
        ShopItem shopItem = new ShopItem(coinCost, Currency.COINS, shopItemStack, shopItemBoughtStack);
        this.randomShopItems.add(shopItem);
        
        for (GameTeam team : this.teams)
        {
            if (!this.modManualStar.enabled)
            {
                for (Coordinate coordinate : team.mapBase.objectives)
                {
                    Location objectiveLocation = coordinate.toLocation(this.gameWorld);
                    
                    ItemStack objectiveItem = new ItemStack(XMaterial.NETHER_STAR.parseMaterial());
                    
                    Item guidingItem = this.gameWorld.dropItem(objectiveLocation, objectiveItem);
                    
                    Vector velocity = guidingItem.getVelocity();
                    guidingItem.setVelocity(velocity.zero());
                    
                    guidingItem.teleport(objectiveLocation);
                    
                    Objective objective = new Objective(this, this.getDefendingTeam(), guidingItem, objectiveLocation);
                    team.objectiveList.add(objective);
                }
            }
            
            for (Player player : team.getPlayers())
            {
                GamePlayer gamePlayer = team.getGamePlayer(player);
                
                buildingCoinsRemaining.put(player, 100);
                
                GameTeam gameTeam = getPlayerTeam(player);
                gamePlayer.spawn(PlayerMode.BUILDING, false);
                if (this.modManualStar.enabled)
                {
                    player.getInventory().setItem(8, manualPlacementStar.clone());
                }
                
                player.sendTitle(ChatColor.WHITE + "You are on the " + gameTeam.color.getFormattedName(false, false, ChatColor.BOLD) + ChatColor.WHITE + " team!", "Begin building your defenses!");
                
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
        for (GameTeam gameTeam : this.teams)
        {
            for (Objective objective : gameTeam.objectiveList)
            {
                objective.despawnItem();
            }
        }
        
        taskTickTimer = new TaskTickTimer(0, 1, this);
        taskTickTimer.runTaskTimer(Assault.INSTANCE, taskTickTimer.startDelay, taskTickTimer.tickDelay);
        
        this.gameStage = GameStage.ATTACKING;
        
        List<GameTeam> randomList = new ArrayList<>(teams);
        Collections.shuffle(randomList);
        
        this.attackingTeam = randomList.get(0);
        this.attackingTeam.teamStage = TeamStage.ATTACKING;
        randomList.get(1).teamStage = TeamStage.DEFENDING;
        
        startRound();
    }
    
    public void swapAttackingTeams()
    {
        teamsGone++;
        
        for (GameTeam gameTeam : teams)
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
        
        GameTeam oldDefenders = this.getDefendingTeam();
        this.attackingTeam.teamStage = TeamStage.DEFENDING;
        this.attackingTeam = oldDefenders;
        this.getAttackingTeam().teamStage = TeamStage.ATTACKING;
        
        startRound();
    }
    
    public void startRound()
    {
        taskGiveCoins = new TaskGiveCoins(0, (int) (this.gameMap.giveCoinDelay * 20), this, this.gameMap.giveCoinAmount);
        taskGiveCoins.runTaskTimer(Assault.INSTANCE, taskGiveCoins.startDelay, taskGiveCoins.tickDelay);
        
        taskAttackTimer = new TaskAttackTimer(0, 20, 20, this);
        taskAttackTimer.runTaskTimer(Assault.INSTANCE, taskAttackTimer.startDelay, taskAttackTimer.tickDelay);
        
        emeraldSpawnTimer = new TaskEmeraldSpawnTimer(0, 0, 1, this, this.gameMap.emeraldSpawnDelay);
        emeraldSpawnTimer.runTaskTimer(Assault.INSTANCE, emeraldSpawnTimer.startDelay, emeraldSpawnTimer.tickDelay);
        
        taskTickTimer = new TaskTickTimer(0, 1, this);
        taskTickTimer.runTaskTimer(Assault.INSTANCE, taskTickTimer.startDelay, taskTickTimer.tickDelay);
        
        if (Assault.useHolographicDisplays)
        {
            this.emeraldSpawnHologram.clear();
            for (Coordinate coordinate : this.getDefendingTeam().mapBase.emeraldSpawns)
            {
                Location hologramLocation = coordinate.toLocation(this.gameWorld);
                hologramLocation.setY(hologramLocation.getY() + 3.5);
                
                Hologram hologram = HologramsAPI.createHologram(Assault.INSTANCE, hologramLocation);
                hologram.getVisibilityManager().setVisibleByDefault(true);
                
                this.emeraldSpawnHologram.add(hologram);
                TextLine line1 = hologram.appendTextLine("§a§lEmerald Spawn");
                line2 = hologram.appendTextLine("§rSpawning in §d%s§r seconds!");
            }
        }
        
        for (GameTeam gameTeam : teams)
        {
            switch (gameTeam.teamStage)
            {
                case DEFENDING:
                    for (Player player : gameTeam.getPlayers())
                    {
                        player.sendTitle(gameTeam.color.chatColor + ChatColor.BOLD.toString() + "You" + Util.RESET_CHAT + " are DEFENDING!", "Defend the " + ChatColor.LIGHT_PURPLE + "nether star" + Util.RESET_CHAT + " with your life!", 0, 4, 1);
                    }
                    break;
                case ATTACKING:
                    for (Player player : gameTeam.getPlayers())
                    {
                        player.sendTitle(gameTeam.color.chatColor + ChatColor.BOLD.toString() + "You" + Util.RESET_CHAT + " are ATTACKING!", "Get to the " + ChatColor.LIGHT_PURPLE + "nether star" + Util.RESET_CHAT + " as fast as you can!", 0, 4, 1);
                    }
                    break;
            }
            
            gameTeam.createAttackShop();
        }
        
        this.restoreHealth();
        
        this.getAttackingTeam().startAttackingTime = System.nanoTime();
        
        for (GameTeam gameTeam : teams)
        {
            gameTeam.gamerPoints = 0;
            
            for (Player player : gameTeam.getPlayers())
            {
                GamePlayer gamePlayer = gameTeam.getGamePlayer(player);
                gamePlayer.playerBank.coins = 0;
                
                player.getInventory().clear();
                player.setItemOnCursor(null);
                player.getOpenInventory().getTopInventory().clear();
                gamePlayer.swapReset();
                
                PlayerMode mode = PlayerMode.setPlayerMode(player, PlayerMode.ATTACKING, this);
                gameTeam.getGamePlayer(player).spawn(mode, false);
            }
        }
        
        for (GameTeam gameTeam : teams)
        {
            gameTeam.mapBase.destroyShops();
        }
        
        this.getDefendingTeam().mapBase.spawnShops(this);
        this.getAttackingTeam().displaySeconds = 0;
        
        if (!this.modManualStar.enabled || this.getDefendingTeam().objectiveList.isEmpty())
        {
            for (Coordinate coordinate : this.getDefendingTeam().mapBase.objectives)
            {
                Location objectiveLocation = coordinate.toLocation(this.gameWorld);
                
                ItemStack objectiveItem = new ItemStack(XMaterial.NETHER_STAR.parseMaterial());
                
                Item objectiveEntity = this.gameWorld.dropItem(objectiveLocation, objectiveItem);
                
                Vector velocity = objectiveEntity.getVelocity();
                objectiveEntity.setVelocity(velocity.zero());
                
                objectiveEntity.teleport(objectiveLocation);
                Objective objective = new Objective(this, this.getDefendingTeam(), objectiveEntity, objectiveLocation);
                this.getDefendingTeam().objectiveList.add(objective);
            }
        }
        else
        {
            for (Objective objective : this.getDefendingTeam().objectiveList)
            {
                objective.spawnItem();
            }
        }
    }
    
    public boolean isTie()
    {
        return isTimeTie() && isStarTie();
    }
    
    public boolean isTimeTie()
    {
        boolean isTimeTie = false;
        
        int triedTeam = 0;
        double firstTime = 0;
        
        for (GameTeam gameTeam : this.teams)
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
    
    public boolean isStarTie()
    {
        if (modFirstTo5Stars.enabled)
        {
            boolean isStarTie = false;
            int triedTeam = 0;
            int firstStars = 0;
            
            for (GameTeam gameTeam : this.teams)
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
            
            return isStarTie;
        }
        
        return true;
    }
    
    public void endRound(boolean attackersForfeit)
    {
        for (Objective objective : new ArrayList<>(this.getDefendingTeam().objectiveList))
        {
            objective.despawnItem();
        }
        
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
                this.getAttackingTeam().storedFinalAttackingTime = this.getAttackingTeam().finalAttackingTime;
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
            
            for (GameTeam gameTeam : this.teams)
            {
                if (gameTeam.finalAttackingTime < lowestTime)
                {
                    lowestTeam = gameTeam;
                    lowestTime = gameTeam.finalAttackingTime;
                }
            }
            
            this.winningTeam = lowestTeam;
            
            if (this.modFirstTo5Stars.enabled && this.isTie()) // todo: here
            {
                int mostStars = Integer.MIN_VALUE;
                GameTeam mostStarsTeam = null;
                
                for (GameTeam gameTeam : this.teams)
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
        if (this.gameStage.equals(GameStage.PREGAME))
        {
            return players;
        }
        
        List<Player> players = new ArrayList<>();
        for (GameTeam gameTeam : this.teams)
        {
            players.addAll(gameTeam.getPlayers());
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
        for (GameTeam gameTeam : this.teams)
        {
            gameTeam.doBuffs();
        }
    }
    
    public void updateScoreboards()
    {
        for (GameTeam gameTeam : this.teams)
        {
            for (GamePlayer player : gameTeam.members)
            {
                player.updateScoreboard();
            }
        }
    }
    
    public GameTeam getAttackingTeam()
    {
        return this.attackingTeam;
    }
    
    public GameTeam getDefendingTeam()
    {
        for (GameTeam gameTeam : this.teams)
        {
            if (!gameTeam.equals(this.attackingTeam))
            {
                return gameTeam;
            }
        }
        return null;
    }
    
    public GameTeam getTeamOne()
    {
        return this.teams.get(0);
    }
    
    public GameTeam getTeamTwo()
    {
        return this.teams.get(1);
    }
    
    public GameTeam getOppositeTeam(Player player)
    {
        for (GameTeam gameTeam : this.teams)
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
        for (GameTeam overTeam : this.teams)
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
        for (GameTeam gameTeam : this.teams)
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
        
        for (SelectedTeam selectedTeam : this.selectedTeamList)
        {
            if (selectedTeam.members.contains(player))
            {
                selectedTeam.removePlayer(player);
            }
        }
        
        if (this.players.size() == 1)
        {
            this.endPreGame(Assault.ASSAULT_PREFIX + "Ending game—not enough players! :(");
        }
    }
    
    public void addPlayer(Player player)
    {
        if (this.gameStage.equals(GameStage.PREGAME))
        {
            player.teleport(gameMap.waitingSpawn.toLocation(this.gameWorld));
            PlayerMode.setPlayerMode(player, PlayerMode.LOBBY, this);
            this.players.add(player);
            
            player.getInventory().setItem(7, GameInstance.teamSelectionItemStack.clone());
            this.teamSelectionShopMap.put(player, new PlayerShopTeamSelection(this, player));
            
            player.getInventory().setItem(8, GameInstance.gameModifierItemStack.clone());
            this.modifierShopMap.put(player, new PlayerShopModifiers(this, player));
            return;
        }
        
        GameTeam smallestTeam = null;
        int smallestNumber = Integer.MAX_VALUE;
        
        for (GameTeam gameTeam : this.teams)
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
    
    public List<Objective> getCompassObjectives(GamePlayer gamePlayer)
    {
        if (this.gameStage.equals(GameStage.BUILDING))
        {
            return gamePlayer.gameTeam.objectiveList;
        }
        else if (this.gameStage.equals(GameStage.ATTACKING))
        {
            if (gamePlayer.gameTeam.teamStage.equals(TeamStage.DEFENDING))
            {
                return gamePlayer.gameTeam.objectiveList;
            }
            else
            {
                return gamePlayer.gameTeam.getOppositeTeam().objectiveList;
            }
        }
        
        return null;
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
