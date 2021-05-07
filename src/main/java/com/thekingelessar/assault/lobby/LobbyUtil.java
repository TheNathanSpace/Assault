package com.thekingelessar.assault.lobby;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import org.bukkit.*;
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
    public static ItemStack inQueueStar = initInQueueStar();
    public static ItemStack leaveBarrier = initBarrier();
    public static ItemStack rulesBook = initBook();
    
    public static List<Player> queueList = new ArrayList<>();
    
    public static void joinQueue(Player player)
    {
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
        
        if (Assault.gameInstances.size() > 0)
        {
            for (GameInstance gameInstance : Assault.gameInstances)
            {
                if (!gameInstance.gameStage.equals(GameStage.FINISHED))
                {
                    player.sendRawMessage(Assault.assaultPrefix + "Joining game!");
                    
                    for (Player otherPlayer : player.getWorld().getPlayers())
                    {
                        if (!otherPlayer.equals(player))
                        {
                            otherPlayer.sendRawMessage(Assault.assaultPrefix + player.getDisplayName() + " has joined a game!");
                        }
                    }
                    player.getInventory().clear();
                    Assault.gameInstances.get(0).addPlayer(player);
                    return;
                }
            }
        }
        
        if (queueList.contains(player))
        {
            return;
        }
        
        queueList.add(player);
        
        if (queueList.size() >= 2)
        {
            for (Player worldPlayer : Assault.lobbyWorld.getPlayers())
            {
                if (queueList.contains(worldPlayer))
                {
                    worldPlayer.sendRawMessage(Assault.assaultPrefix + "Game starting!");
                    worldPlayer.getInventory().clear();
                }
                else
                {
                    worldPlayer.sendRawMessage(Assault.assaultPrefix + "A game is starting! Click the §dnether star§r to join!");
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                }
            }
            
            GameInstance newInstance = new GameInstance("saloon", queueList, null);
            newInstance.startWorld();
            newInstance.sendPlayersToWorld();
            Assault.gameInstances.add(newInstance);
            
            queueList.clear();
        }
        else
        {
            LobbyUtil.giveQueueStar(player);
            player.sendRawMessage(Assault.assaultPrefix + "You've been added to the queue! The game will start when there are enough players.");
            
            for (Player otherPlayer : player.getWorld().getPlayers())
            {
                if (!otherPlayer.equals(player))
                {
                    otherPlayer.sendRawMessage(Assault.assaultPrefix + player.getDisplayName() + " has joined the queue!");
                }
            }
            
            giveBarrier(player);
        }
    }
    
    public static void leaveQueue(Player player)
    {
        player.playSound(player.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
        
        queueList.remove(player);
        player.getInventory().setItem(8, new ItemStack(Material.AIR));
        LobbyUtil.giveStar(player);
        player.sendRawMessage(Assault.assaultPrefix + "You've been removed from the queue. :(");
    }
    
    public static void sendRules(Player player)
    {
        String message = Assault.assaultPrefix + ChatColor.BLUE + ChatColor.UNDERLINE + "Click here to open the tutorial!";
        String url = "https://thenathan.space/assault/";
        LobbyUtil.sendMessage(player, message, url);
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
    }
    
    private static void sendMessage(Player player, String message, String url)
    {
        String command = "tellraw " + player.getName() + " [\"\",{\"text\":\"[\",\"bold\":true,\"color\":\"dark_purple\"},{\"text\":\"Assault\",\"bold\":true,\"color\":\"light_purple\"},{\"text\":\"]\",\"bold\":true,\"color\":\"dark_purple\"},{\"text\":\" \",\"color\":\"blue\"},{\"text\":\"Click here to open the rules!\",\"underlined\":true,\"color\":\"blue\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://thenathan.space/assault/\"}}]";
        Bukkit.getServer().dispatchCommand(
                Bukkit.getConsoleSender(),
                command);
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
    
    private static ItemStack initInQueueStar()
    {
        ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "You are in the queue!");
        itemMeta.setLore(Arrays.asList(ChatColor.RESET + "You have been added to the queue!", ChatColor.RESET + "", ChatColor.RESET + "When there are enough players,", ChatColor.RESET + "the game will start."));
        
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
    
    private static ItemStack initBook()
    {
        ItemStack itemStack = new ItemStack(Material.BOOK);
        
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.BLUE + ChatColor.BOLD.toString() + "Tutorial");
        itemMeta.setLore(Collections.singletonList(ChatColor.RESET + "Click this to open up the explanation!"));
        
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public static void giveStar(Player player)
    {
        player.getInventory().setItem(1, LobbyUtil.joinGameStar.clone());
    }
    
    public static void giveQueueStar(Player player)
    {
        player.getInventory().setItem(1, LobbyUtil.inQueueStar.clone());
    }
    
    public static void giveBarrier(Player player)
    {
        player.getInventory().setItem(8, LobbyUtil.leaveBarrier.clone());
    }
    
    public static void giveBook(Player player)
    {
        player.getInventory().setItem(0, LobbyUtil.rulesBook.clone());
    }
    
    public static void joinLobby(Player player)
    {
        player.teleport(Assault.lobbySpawn);
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
        player.setCustomName(player.getName());
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
        LobbyUtil.giveStar(player);
        LobbyUtil.giveBook(player);
        player.getInventory().setHeldItemSlot(0);
    }
    
    private static void giveGlass(Player player)
    {
        // spectator?
    }
}
