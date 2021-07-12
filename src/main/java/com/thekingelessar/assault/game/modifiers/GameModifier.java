package com.thekingelessar.assault.game.modifiers;

import com.thekingelessar.assault.game.GameInstance;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameModifier
{
    public GameInstance gameInstance;
    public List<Player> votedPlayers = new ArrayList<>();
    public boolean enabled = false;
    
    public String name;
    
    public GameModifier(GameInstance gameInstance, String name)
    {
        this.gameInstance = gameInstance;
        this.name = name;
    }
    
    public boolean setEnabled()
    {
        if (votedPlayers.size() > (gameInstance.getPlayers().size()) / 2)
        {
            enabled = true;
        }
        
        return enabled;
    }
}
