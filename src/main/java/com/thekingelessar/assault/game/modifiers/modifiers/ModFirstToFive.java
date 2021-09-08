package com.thekingelessar.assault.game.modifiers.modifiers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.modifiers.GameModifier;

public class ModFirstToFive extends GameModifier
{
    
    public ModFirstToFive(GameInstance gameInstance)
    {
        super(gameInstance, "First to 5 Stars", "Attackers must get the star 5 times instead of just once.");
    }
}
