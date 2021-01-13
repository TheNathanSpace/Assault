package com.thekingelessar.assault.game;

import com.thekingelessar.assault.util.Coordinate;
import org.bukkit.Bukkit;
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
    public Coordinate spawn;
    
    public GameTeam(Coordinate spawn, TeamColor color, GameInstance instance)
    {
        this.color = color;
        this.spawn = spawn;
        
        createScoreboard(instance);
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
        members.add(uuid);
        teamScoreboard.addPlayer(Bukkit.getOfflinePlayer(uuid));
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
        members.remove(uuid);
        teamScoreboard.removePlayer(Bukkit.getOfflinePlayer(uuid));
    }
}
