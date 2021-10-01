package com.thekingelessar.assault.game.pregame.modifiers.modifiers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.pregame.modifiers.GameModifier;

public class ModUseTeamSelection extends GameModifier
{
    
    public ModUseTeamSelection(GameInstance gameInstance)
    {
        super(gameInstance, "Use Team Selection", "Enables the Team Selector, allowing players to choose their team");
    }
}
