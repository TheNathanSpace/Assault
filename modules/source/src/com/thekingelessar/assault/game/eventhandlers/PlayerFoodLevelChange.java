package com.thekingelessar.assault.game.eventhandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerFoodLevelChange implements Listener
{
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent foodLevelChangeEvent)
    {
        foodLevelChangeEvent.setCancelled(true);
    }
}