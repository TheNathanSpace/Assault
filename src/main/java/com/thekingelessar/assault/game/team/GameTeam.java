package com.thekingelessar.assault.game.team;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.inventory.shops.ShopAttack;
import com.thekingelessar.assault.game.inventory.shops.ShopBuilding;
import com.thekingelessar.assault.game.inventory.shops.ShopTeamBuffs;
import com.thekingelessar.assault.game.inventory.teambuffs.IBuff;
import com.thekingelessar.assault.game.map.MapBase;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
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
    public ShopAttack shopAttacking;
    public ShopTeamBuffs shopTeamBuffs;
    
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
        teamScoreboard = gameInstance.teamScoreboard.registerNewTeam(color.toString());
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
        teamScoreboard.addPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
        
        player.setDisplayName(this.color.chatColor + player.getName() + ChatColor.RESET);
        player.setCustomName(player.getDisplayName() + ChatColor.RESET);
        player.setPlayerListName(player.getDisplayName() + ChatColor.RESET);
        
        gamePlayer.swapReset();
        player.getInventory().clear();
        gamePlayer.spawn(PlayerMode.getTeamMode(this));
        
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
                case ATTACKING:
                    gameInstance.alertLastEnemyLeft(this.getOppositeTeam());
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
    }
    
    public void createAttackShop()
    {
        if (this.teamStage.equals(TeamStage.ATTACKING))
        {
            this.shopTeamBuffs = new ShopTeamBuffs(this, null);
        }
        
        this.shopAttacking = new ShopAttack(this.color);
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
        if (this.teamStage.equals(TeamStage.DEFENDING))
        {
            return false;
        }
        
        GameTeam oppositeTeam = this.getOppositeTeam();
        long nanosecondsTaken = System.nanoTime() - this.startAttackingTime;
        double secondsTaken = nanosecondsTaken / 1000000000.;
        
        return oppositeTeam.finalAttackingTime > secondsTaken;
    }
    
    public void toggleForfeit(Player player)
    {
        if (this.forfeitList.contains(player))
        {
            this.forfeitList.remove(player);
            player.sendRawMessage(Assault.assaultPrefix + "You've voted to " + ChatColor.GREEN + "not forfeit" + ChatColor.RESET + "!");
        }
        else
        {
            this.forfeitList.add(player);
            player.sendRawMessage(Assault.assaultPrefix + "You've voted to " + ChatColor.RED + "forfeit" + ChatColor.RESET + "!");
        }
        
        this.evaluateForfeit();
    }
    
    public void evaluateForfeit()
    {
        if (this.forfeitList.size() >= this.getPlayers().size() * 0.66)
        {
            for (Player player : this.getPlayers())
            {
                player.sendRawMessage(Assault.assaultPrefix + "Your team " + ChatColor.RED + "forfeits" + ChatColor.RESET + "!");
            }
            
            gameInstance.finishRound(gameInstance.getDefendingTeam());
            gameInstance.declareWinners(null, true);
            
            gameInstance.getAttackingTeam().finalAttackingTime = 481;
            gameInstance.getAttackingTeam().displaySeconds = Util.round(gameInstance.getAttackingTeam().finalAttackingTime, 2);
        }
    }
    
}
