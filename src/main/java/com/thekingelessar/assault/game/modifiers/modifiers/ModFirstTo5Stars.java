package com.thekingelessar.assault.game.modifiers.modifiers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.modifiers.GameModifier;

public class ModFirstTo5Stars extends GameModifier
{
    
    public ModFirstTo5Stars(GameInstance gameInstance)
    {
        super(gameInstance, "First to 5 Stars", "Attackers must reach the star 5 times instead of just once.");
    }
}
