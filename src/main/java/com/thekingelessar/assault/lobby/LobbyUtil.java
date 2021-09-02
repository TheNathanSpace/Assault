package com.thekingelessar.assault.lobby;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.util.ItemInit;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LobbyUtil
{
    public static ItemStack joinGameStar = ItemInit.initStar();
    public static ItemStack inQueueStar = ItemInit.initInQueueStar();
    public static ItemStack leaveBarrier = ItemInit.initBarrier();
    public static ItemStack rulesBook = ItemInit.initBook();
    
    public static List<Player> queueList = new ArrayList<>();
    
    public static void joinQueue(Player player)
    {
        try
        {
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
        }
        catch (Exception e)
        {
            player.playSound(player.getLocation(), Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"), 1.0F, 1.0F);
        }
        
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
                    try
                    {
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    }
                    catch (Exception e)
                    {
                        player.playSound(player.getLocation(), Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"), 1.0F, 1.0F);
                    }
                }
            }
            
            Random random = new Random();
            List<String> mapNames = new ArrayList<>(Assault.maps.keySet());
            String randomMap = mapNames.get(random.nextInt(mapNames.size()));
            
            GameInstance newInstance = new GameInstance(randomMap, queueList, null);
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
        try
        {
            player.playSound(player.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
        }
        catch (Exception e)
        {
            player.playSound(player.getLocation(), Sound.valueOf("ENTITY_SKELETON_HURT"), 1.0F, 1.0F);
        }
        
        queueList.remove(player);
        player.getInventory().setItem(8, new ItemStack(Material.AIR));
        LobbyUtil.giveStar(player);
        player.sendRawMessage(Assault.assaultPrefix + "You've been removed from the queue. :(");
    }
    
    public static void sendRules(Player player)
    {
        LobbyUtil.sendGuideURL(player);
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
    }
    
    private static void sendGuideURL(Player player)
    {
        String command = "tellraw " + player.getName() + " [\"\",{\"text\":\"[\",\"bold\":true,\"color\":\"dark_purple\"},{\"text\":\"Assault\",\"bold\":true,\"color\":\"light_purple\"},{\"text\":\"]\",\"bold\":true,\"color\":\"dark_purple\"},{\"text\":\" \",\"color\":\"blue\"},{\"text\":\"Click here to open the guide online!\",\"underlined\":true,\"color\":\"blue\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://thenathan.space/assault/\"}}]";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
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
