package com.thekingelessar.assault.game.pregame.modifiers.modifiers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.pregame.modifiers.GameModifier;

public class ModDisableWildcardItems extends GameModifier
{
    
    public ModDisableWildcardItems(GameInstance gameInstance)
    {
        super(gameInstance, "Disable Wildcard Items", "Random Wildcard Items will not appear for sale in the shop.");
    }
}
