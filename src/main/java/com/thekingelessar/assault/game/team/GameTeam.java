package com.thekingelessar.assault.game.team;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.inventory.ShopBuilding;
import com.thekingelessar.assault.game.map.MapBase;
import com.thekingelessar.assault.game.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameTeam
{
    public GameInstance gameInstance;
    
    public TeamColor color;
    public Team teamScoreboard;
    
    public List<GamePlayer> members = new ArrayList<>();
    
    public TeamStage teamStage;
    
    public MapBase mapBase;
    
    public ShopBuilding shopBuilding;
    
    public double secondsTaken;
    
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
    
    public void addMember(Player player)
    {
        GamePlayer gamePlayer = new GamePlayer(player, gameInstance);
        members.add(gamePlayer);
        teamScoreboard.addPlayer(player);
        
        player.setDisplayName(this.color.chatColor + player.getName() + ChatColor.RESET);
        player.setPlayerListName(player.getDisplayName() + ChatColor.RESET);
    }
    
    public void addMembers(List<Player> players)
    {
        for (Player player : players)
        {
            addMember(player);
        }
    }
    
    public void removeMember(UUID uuid)
    {
        Player member = Bukkit.getPlayer(uuid);
        
        members.remove(uuid);
        teamScoreboard.removePlayer(member);
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
    
    public GamePlayer getGamePlayer(Player player)
    {
        for (GamePlayer gamePlayer : this.members)
        {
            if (player.equals(gamePlayer.player)) return gamePlayer;
        }
        
        return null;
    }
    
}
