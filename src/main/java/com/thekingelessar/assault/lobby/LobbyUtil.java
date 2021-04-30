package com.thekingelessar.assault.lobby;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LobbyUtil
{
    public static ItemStack joinGameStar = initStar();
    public static ItemStack leaveBarrier = initBarrier();
    
    public static List<Player> queueList = new ArrayList<>();
    
    public static void joinQueue(Player player)
    {
        if (Assault.gameInstances.size() > 0)
        {
            for (GameInstance gameInstance : Assault.gameInstances)
            {
                if (!gameInstance.gameStage.equals(GameStage.FINISHED))
                {
                    player.sendRawMessage(Assault.assaultPrefix + "Joining game!");
                    Assault.gameInstances.get(0).addPlayer(player);
                    return;
                }
            }
        }
        
        queueList.add(player);
        
        if (queueList.size() >= 2)
        {
            for (Player worldPlayer : Assault.lobbyWorld.getPlayers())
            {
                if (queueList.contains(worldPlayer))
                {
                    worldPlayer.sendRawMessage(Assault.assaultPrefix + "Game starting!");
                }
                else
                {
                    worldPlayer.sendRawMessage(Assault.assaultPrefix + "A game is starting! Click the §dnether star§r to join!");
                }
                
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
            }
            
            
            GameInstance newInstance = new GameInstance("saloon", queueList, null);
            newInstance.startWorld();
            newInstance.sendPlayersToWorld();
            Assault.gameInstances.add(newInstance);
            
            queueList.clear();
        }
        else
        {
            player.sendRawMessage(Assault.assaultPrefix + "You've been added to the queue! The game will start when there are enough players.");
            giveBarrier(player);
        }
    }
    
    public static void leaveQueue(Player player)
    {
        queueList.remove(player);
        player.getInventory().setItem(8, new ItemStack(Material.AIR));
        player.sendRawMessage(Assault.assaultPrefix + "You've been removed from the queue. :(");
    }
    
    private static ItemStack initStar()
    {
        ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Join Game");
        itemMeta.setLore(Arrays.asList(ChatColor.RESET + "Click this to join a game of Assault!", ChatColor.RESET + "", ChatColor.RESET + "If there's no game available,", ChatColor.RESET + "you'll be added to the queue."));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    private static ItemStack initBarrier()
    {
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Leave Queue");
        itemMeta.setLore(Collections.singletonList(ChatColor.RESET + "Click this to leave the queue."));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static void giveStar(Player player)
    {
        player.getInventory().setItem(0, LobbyUtil.joinGameStar.clone());
    }
    
    public static void giveBarrier(Player player)
    {
        player.getInventory().setItem(8, LobbyUtil.leaveBarrier.clone());
    }
    
    public static void joinLobby(Player player)
    {
        player.teleport(Assault.lobbySpawn);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
        LobbyUtil.giveStar(player);
    }
    
    private static void giveGlass(Player player)
    {
        // spectator?
    }
}
