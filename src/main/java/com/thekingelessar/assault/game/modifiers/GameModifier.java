package com.thekingelessar.assault.game.modifiers;

import com.thekingelessar.assault.game.GameInstance;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameModifier
{
    public GameInstance gameInstance;
    public List<Player> votedPlayers = new ArrayList<>();
    
    public GameModifier(GameInstance gameInstance)
    {
        this.gameInstance = gameInstance;
    }
}
