package com.thekingelessar.assault.game.team;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameEndManager;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.inventory.shops.ShopAttack;
import com.thekingelessar.assault.game.inventory.shops.ShopBuilding;
import com.thekingelessar.assault.game.inventory.shops.ShopTeamBuffs;
import com.thekingelessar.assault.game.inventory.teambuffs.IBuff;
import com.thekingelessar.assault.game.map.MapBase;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameTeam
{
    public GameInstance gameInstance;
    
    public TeamColor color;
    public Team teamScoreboard;
    
    public List<GamePlayer> members = new ArrayList<>();
    
    public TeamStage teamStage;
    
    public MapBase mapBase;
    
    public ShopBuilding shopBuilding;
    public ShopTeamBuffs shopTeamBuffs;
    
    public ItemStack goldItem;
    public Inventory secretStorage;
    
    public int gamerPoints = 0;
    
    public List<IBuff> buffList = new ArrayList<>();
    
    public double displaySeconds = 0;
    public long startAttackingTime = 0; // in nanoseconds
    public double finalAttackingTime = 0; // in seconds
    
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
        
        goldItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta goldMeta = goldItem.getItemMeta();
        goldMeta.setDisplayName(ChatColor.RESET + "Return to shop");
        goldMeta.setLore(Arrays.asList(ChatColor.RESET + "Click to return to the shop.", ChatColor.RESET + "This storage can be used", ChatColor.RESET + "by your" + this.color.chatColor + " entire team§r."));
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
        teamScoreboard.setPrefix(color.chatColor + ChatColor.BOLD.toString() + " " + color.toString().charAt(0));
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
        
        if (gameInstance.gameStage.equals(GameStage.BUILDING))
        {
            if (gameInstance.buildingCoinsRemaining.containsKey(player))
            {
                gamePlayer.playerBank.coins += gameInstance.buildingCoinsRemaining.get(player);
            }
            else
            {
                gamePlayer.playerBank.coins += 100;
                gameInstance.buildingCoinsRemaining.put(player, 100);
            }
        }
        
        members.add(gamePlayer);
        teamScoreboard.addPlayer(player);
        
        player.setDisplayName(this.color.chatColor + player.getName() + ChatColor.RESET);
        player.setCustomName(player.getDisplayName() + ChatColor.RESET);
        player.setPlayerListName(player.getDisplayName() + ChatColor.RESET);
        
        gamePlayer.swapReset();
        player.getInventory().clear();
        gamePlayer.spawn(PlayerMode.getTeamMode(this));
        
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
        members.remove(this.getGamePlayer(player));
        teamScoreboard.removePlayer(player);
        
        if (members.size() == 0)
        {
            switch (gameInstance.gameStage)
            {
                case BUILDING:
                case ATTACK_ROUNDS:
                    gameInstance.alertTeamleft(this.getOppositeTeam());
                    break;
            }
        }
        
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
            gamePlayer.shopAttacking = new ShopAttack(this.color, gamePlayer);
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
        for (GameTeam listTeam : gameInstance.teams.values())
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
            player.sendRawMessage(Assault.ASSAULT_PREFIX + "You've voted to " + ChatColor.GREEN + "not forfeit" + ChatColor.RESET + "!");
        }
        else
        {
            this.forfeitList.add(player);
            player.sendRawMessage(Assault.ASSAULT_PREFIX + "You've voted to " + ChatColor.DARK_RED + "forfeit" + ChatColor.RESET + "!");
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
                player.sendRawMessage(Assault.ASSAULT_PREFIX + this.color.chatColor + "Your team " + ChatColor.DARK_RED + "forfeits" + ChatColor.RESET + "!");
            }
            for (Player player : this.getOppositeTeam().getPlayers())
            {
                player.sendRawMessage(Assault.ASSAULT_PREFIX + "The" + this.color.chatColor + " other team " + ChatColor.DARK_RED + "forfeits" + ChatColor.RESET + "!");
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
