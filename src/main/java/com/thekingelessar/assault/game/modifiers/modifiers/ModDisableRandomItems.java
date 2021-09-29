package com.thekingelessar.assault.game.modifiers.modifiers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.modifiers.GameModifier;

public class ModDisableRandomItems extends GameModifier
{
    
    public ModDisableRandomItems(GameInstance gameInstance)
    {
        super(gameInstance, "Disable Random Items", "Random items will not appear for sale in the shop.");
    }
}
