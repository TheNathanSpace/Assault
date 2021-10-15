package com.thekingelessar.assault.game.team;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.database.AssaultTableManager;
import com.thekingelessar.assault.database.Statistic;
import com.thekingelessar.assault.game.GameEndManager;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.Objective;
import com.thekingelessar.assault.game.inventory.shops.ShopAttacking;
import com.thekingelessar.assault.game.inventory.shops.ShopBuilding;
import com.thekingelessar.assault.game.inventory.shops.ShopTeamBuffs;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.teambuffs.IBuff;
import com.thekingelessar.assault.game.world.map.MapBase;
import com.thekingelessar.assault.util.Util;
import com.thekingelessar.assault.util.version.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GameTeam
{
    public GameInstance gameInstance;
    
    public TeamColor color;
    public Team teamScoreboard;
    
    public List<GamePlayer> members = new ArrayList<>();
    public List<UUID> loggedOutPlayers = new ArrayList<>();
    
    public TeamStage teamStage;
    
    public MapBase mapBase;
    
    public ShopBuilding shopBuilding;
    public ShopTeamBuffs shopTeamBuffs;
    
    public ItemStack goldItem;
    public Inventory secretStorage;
    
    public int gamerPoints = 0;
    
    public List<IBuff> buffList = new ArrayList<>();
    
    public List<Objective> objectiveList = new ArrayList<>();
    
    public int starsPickedUp = 0;
    
    public double displaySeconds = 0;
    public long startAttackingTime = 0; // in nanoseconds
    public double finalAttackingTime = 0; // in seconds
    public double storedFinalAttackingTime = 0;
    
    public List<Player> forfeitList = new ArrayList<>();
    
    public GameTeam(TeamColor color, GameInstance instance)
    {
        this.color = color;
        this.gameInstance = instance;
        createScoreboard();
    }
    
    public void setSecretStorage()
    {
        secretStorage = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Storage");
        
        goldItem = new ItemStack(XMaterial.GOLD_INGOT.parseMaterial());
        ItemMeta goldMeta = goldItem.getItemMeta();
        goldMeta.setDisplayName(Util.RESET_CHAT + "Return to shop");
        goldMeta.setLore(Arrays.asList(Util.RESET_CHAT + "Click to return to the shop.", Util.RESET_CHAT + "This storage can be used", Util.RESET_CHAT + "by your" + this.color.chatColor + " entire team§r."));
        goldItem.setItemMeta(goldMeta);
        
        secretStorage.setItem(0, goldItem);
    }
    
    public void setTeamMapBase()
    {
        for (MapBase base : gameInstance.gameMap.bases)
        {
            if (base.teamColor.equals(this.color))
            {
                this.mapBase = base;
            }
        }
    }
    
    private void createScoreboard()
    {
        teamScoreboard = gameInstance.scoreboard.registerNewTeam(color.toString());
        teamScoreboard.setAllowFriendlyFire(false);
        teamScoreboard.setCanSeeFriendlyInvisibles(true);
        teamScoreboard.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
    }
    
    public void doBuffs()
    {
        for (IBuff buff : this.buffList)
        {
            buff.doEffect();
        }
    }
    
    public void addMember(Player player)
    {
        GamePlayer gamePlayer = new GamePlayer(player, gameInstance, this);
        
        if (gameInstance.gameStage.equals(GameStage.BUILDING) || gameInstance.gameStage.equals(GameStage.PREGAME))
        {
            if (gameInstance.buildingCoinsRemaining.containsKey(player))
            {
                gamePlayer.playerBank.coins += gameInstance.buildingCoinsRemaining.get(player);
            }
            else
            {
                gamePlayer.playerBank.coins += gameInstance.gameMap.buildingCoins;
                gameInstance.buildingCoinsRemaining.put(player, gameInstance.gameMap.buildingCoins);
            }
        }
        
        if (gameInstance.gameStage.equals(GameStage.ATTACKING))
        {
            gamePlayer.shopAttacking = new ShopAttacking(this.color, gamePlayer);
        }
        
        members.add(gamePlayer);
        teamScoreboard.addEntry(player.getName());
        
        player.setPlayerListName(color.chatColor + ChatColor.BOLD.toString() + "[" + color.toString().charAt(0) + "]" + Util.RESET_CHAT + " " + this.color.chatColor + player.getName() + Util.RESET_CHAT);
        player.setDisplayName(color.chatColor + player.getName() + Util.RESET_CHAT);
        
        gamePlayer.swapReset();
        gamePlayer.spawn(PlayerMode.getTeamMode(this), false);
        
        gameInstance.updateShopSkins(player);
        
        this.evaluateForfeit();
    }
    
    public void addMembers(List<Player> players)
    {
        for (Player player : players)
        {
            addMember(player);
        }
    }
    
    public void removeMember(Player player)
    {
        System.out.println("Player leaving. Team list before:");
        for (GamePlayer gamePlayer : this.members)
        {
            System.out.println(" - " + gamePlayer.player.getName());
        }
        members.remove(this.getGamePlayer(player));
        System.out.println("List after:");
        for (GamePlayer gamePlayer : this.members)
        {
            System.out.println(" - " + gamePlayer.player.getName());
        }
        
        teamScoreboard.removeEntry(player.getName());
        
        if (members.size() == 0)
        {
            switch (gameInstance.gameStage)
            {
                case BUILDING:
                case ATTACKING:
                    gameInstance.alertTeamleft(this.getOppositeTeam());
                    break;
            }
        }
        
        long timeNow = Instant.now().toEpochMilli();
        AssaultTableManager.getInstance().insertValue(player, Statistic.LAST_PLAYED, timeNow);
        
        this.evaluateForfeit();
    }
    
    public List<Player> getPlayers()
    {
        List<Player> playerList = new ArrayList<>();
        
        for (GamePlayer gamePlayer : this.members)
        {
            playerList.add(gamePlayer.player);
        }
        
        return playerList;
    }
    
    public void createBuildingShop()
    {
        this.shopBuilding = new ShopBuilding(this.color, null);
        this.setSecretStorage();
    }
    
    public void createAttackShop()
    {
        if (this.teamStage.equals(TeamStage.ATTACKING))
        {
            this.shopTeamBuffs = new ShopTeamBuffs(this, null);
        }
        
        for (Player player : this.getPlayers())
        {
            GamePlayer gamePlayer = this.getGamePlayer(player);
            gamePlayer.shopAttacking = new ShopAttacking(this.color, gamePlayer);
        }
        
        this.setSecretStorage();
    }
    
    
    public GamePlayer getGamePlayer(Player player)
    {
        for (GamePlayer gamePlayer : this.members)
        {
            if (player.equals(gamePlayer.player)) return gamePlayer;
        }
        
        return null;
    }
    
    public GameTeam getOppositeTeam()
    {
        for (GameTeam listTeam : gameInstance.teams)
        {
            if (!this.equals(listTeam))
            {
                return listTeam;
            }
        }
        return null;
    }
    
    public boolean canForfeit()
    {
        return !this.teamStage.equals(TeamStage.DEFENDING);
    }
    
    public void toggleForfeit(Player player)
    {
        if (this.forfeitList.contains(player))
        {
            this.forfeitList.remove(player);
            player.sendRawMessage(Assault.ASSAULT_PREFIX + "You've voted to " + ChatColor.GREEN + "not forfeit" + Util.RESET_CHAT + "!");
        }
        else
        {
            this.forfeitList.add(player);
            player.sendRawMessage(Assault.ASSAULT_PREFIX + "You've voted to " + ChatColor.DARK_RED + "forfeit" + Util.RESET_CHAT + "!");
        }
        
        this.evaluateForfeit();
    }
    
    public void evaluateForfeit()
    {
        if (this.getPlayers().size() == 0)
        {
            return;
        }
        
        if (this.forfeitList.size() >= this.getPlayers().size() * 0.66)
        {
            for (Player player : this.getPlayers())
            {
                player.sendRawMessage(Assault.ASSAULT_PREFIX + this.color.chatColor + "Your team " + ChatColor.DARK_RED + "forfeits" + Util.RESET_CHAT + "!");
            }
            for (Player player : this.getOppositeTeam().getPlayers())
            {
                player.sendRawMessage(Assault.ASSAULT_PREFIX + "The" + this.color.chatColor + " other team " + ChatColor.DARK_RED + "forfeits" + Util.RESET_CHAT + "!");
            }
            
            gameInstance.endRound(true);
            
            if (this.gameInstance.teamsGone == 1)
            {
                gameInstance.gameEndManager.declareWinners(GameEndManager.WinState.LOWEST_TIME);
            }
        }
    }
    
    public boolean didForfeit()
    {
        return this.displaySeconds == (double) -999;
    }
    
    public List<Inventory> getShopInventories()
    {
        List<Inventory> shopInventories = new ArrayList<>();
        
        if (this.shopBuilding != null)
        {
            shopInventories.add(this.shopBuilding.inventory);
        }
        
        for (Player player : this.getPlayers())
        {
            GamePlayer gamePlayer = this.getGamePlayer(player);
            if (gamePlayer.shopAttacking != null)
            {
                shopInventories.add(gamePlayer.shopAttacking.inventory);
            }
        }
        
        if (this.shopTeamBuffs != null)
        {
            shopInventories.add(this.shopTeamBuffs.inventory);
        }
        
        return shopInventories;
    }
}
