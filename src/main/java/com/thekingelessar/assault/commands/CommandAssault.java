package com.thekingelessar.assault.commands;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class CommandAssault implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args == null || args.length == 0) return false;
        switch (args[0])
        {
            case "start":
                if (sender instanceof Player)
                {
                    Player senderPlayer = (Player) sender;
                    GameInstance testInstance = new GameInstance(args[1], senderPlayer.getWorld().getPlayers(), null);
                    testInstance.startWorld();
                    testInstance.sendPlayersToWorld();
                    Assault.gameInstances.add(testInstance);
                }
                break;
            
            case "attack":
                if (sender instanceof Player)
                {
                    for (GameInstance gameInstance : Assault.gameInstances)
                    {
                        gameInstance.startAttackMode();
                    }
                }
                break;
            
            case "close":
                WorldManager.closeWorld(Bukkit.getWorld(args[1]));
                break;
            
            case "create":
                WorldManager.createWorldFromMap(args[1], Boolean.parseBoolean(args[2]), args[3]);
                break;
            
            case "join":
                if (sender instanceof Player)
                {
                    ((Player) sender).teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
                }
                break;
            
            case "shop_building":
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;
                    GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
                    
                    if (gameInstance != null)
                    {
                        GameTeam team = gameInstance.getPlayerTeam(player);
                        InventoryView inventoryView = player.openInventory(team.shopBuilding.inventory);
                    }
                }
                break;
        }
        return true;
    }
}