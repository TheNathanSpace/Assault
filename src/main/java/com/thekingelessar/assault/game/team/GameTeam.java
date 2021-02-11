package com.thekingelessar.assault.game.team;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.map.MapBase;
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
    
    public ArrayList<UUID> members = new ArrayList<>();
    
    public TeamStage teamStage;
    
    public MapBase mapBase;
    
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
    
    public void addMember(UUID uuid)
    {
        Player member = Bukkit.getPlayer(uuid);
        
        if (member == null)
        {
            return;
        }
        
        members.add(uuid);
        teamScoreboard.addPlayer(member);
        
        member.setDisplayName(this.color.chatColor + member.getName() + ChatColor.RESET);
        member.setPlayerListName(member.getDisplayName() + ChatColor.RESET);
    }
    
    public void addMember(String string)
    {
        addMember(Bukkit.getPlayer(string).getUniqueId());
    }
    
    public void addMembers(List<Player> players)
    {
        for (Player player : players)
        {
            addMember(player.getUniqueId());
        }
    }
    
    public void removeMember(UUID uuid)
    {
        Player member = Bukkit.getPlayer(uuid);
        
        members.remove(uuid);
        teamScoreboard.removePlayer(member);
    }
    
}
