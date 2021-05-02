package com.thekingelessar.assault.commands;

import com.thekingelessar.assault.game.GameInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAssault implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args == null || args.length == 0) return false;
        switch (args[0])
        {
            case "attack":
                if (sender instanceof Player)
                {
                    Player senderPlayer = (Player) sender;
                    GameInstance testInstance = GameInstance.getPlayerGameInstance(senderPlayer);
                    if (testInstance != null)
                    {
                        testInstance.taskCountdownBuilding.ticksLeft = 60;
                    }
                }
                break;
        }
        return true;
    }
}