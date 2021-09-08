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
    public String description;
    
    public GameModifier(GameInstance gameInstance, String name, String description)
    {
        this.gameInstance = gameInstance;
        this.name = name;
        this.description = description;
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
