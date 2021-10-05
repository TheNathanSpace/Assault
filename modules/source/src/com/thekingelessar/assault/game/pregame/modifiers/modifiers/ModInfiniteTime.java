package com.thekingelessar.assault.game.pregame.modifiers.modifiers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.pregame.modifiers.GameModifier;

public class ModInfiniteTime extends GameModifier
{
    
    public ModInfiniteTime(GameInstance gameInstance)
    {
        super(gameInstance, "Infinite Time", "Removes the attacking round time limit of 8 minutes.");
    }
}
