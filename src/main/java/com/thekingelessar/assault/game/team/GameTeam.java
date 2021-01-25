package com.thekingelessar.assault.game.team;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.util.Coordinate;
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
    public TeamColor color;
    public Team teamScoreboard;
    
    public ArrayList<UUID> members = new ArrayList<>();
    public Coordinate defenderSpawn;
    public Coordinate attackerSpawn;
    
    public TeamStage teamStage;
    
    public GameTeam(TeamColor color, GameInstance instance)
    {
        this.color = color;
        createScoreboard(instance);
    }
    
    public void setSpawns(Coordinate defenderSpawn, Coordinate attackerSpawn)
    {
        this.defenderSpawn = defenderSpawn;
        this.attackerSpawn = attackerSpawn;
    }
    
    private void createScoreboard(GameInstance instance)
    {
        teamScoreboard = instance.teamScoreboard.registerNewTeam(color.toString());
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
        
        member.setDisplayName(this.color + member.getName() + ChatColor.RESET);
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
