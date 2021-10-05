package com.thekingelessar.assault.game.pregame.modifiers.modifiers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.pregame.modifiers.GameModifier;

public class ModDontUseTeamSelection extends GameModifier
{
    
    public ModDontUseTeamSelection(GameInstance gameInstance)
    {
        super(gameInstance, "Don't Use Team Selection", "Disables the Team Selector, randomizing every player's team.");
    }
}
