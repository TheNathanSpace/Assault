package com.thekingelessar.assault.game.pregame.teamselection;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.team.TeamColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SelectedTeam
{
    public GameInstance gameInstance;
    public TeamColor teamColor;
    public List<Player> members = new ArrayList<>();
    
    public SelectedTeam(GameInstance gameInstance, TeamColor teamColor)
    {
        this.gameInstance = gameInstance;
        this.teamColor = teamColor;
    }
    
    public boolean addPlayer(Player player)
    {
        if (this.members.contains(player))
        {
            this.removePlayer(player);
            return false;
        }
        
        this.members.add(player);
        this.removePlayerFromOtherTeams(player);
        return true;
    }
    
    public void removePlayer(Player player)
    {
        this.members.remove(player);
    }
    
    public void removePlayerFromOtherTeams(Player player)
    {
        for (SelectedTeam selectedTeam : gameInstance.selectedTeamList)
        {
            if (!selectedTeam.equals(this))
            {
                selectedTeam.removePlayer(player);
            }
        }
    }
}
