package com.thekingelessar.assault.lobby;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.util.ItemInit;
import com.thekingelessar.assault.util.version.XMaterial;
import com.thekingelessar.assault.util.version.XSound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LobbyUtil
{
    public static ItemStack joinGameStar = ItemInit.initStar();
    public static ItemStack inQueueStar = ItemInit.initInQueueStar();
    public static ItemStack leaveBarrier = ItemInit.initBarrier();
    public static ItemStack rulesBook = ItemInit.initBook();
    
    public static List<ItemStack> undroppable = Arrays.asList(LobbyUtil.joinGameStar, LobbyUtil.rulesBook, LobbyUtil.inQueueStar, LobbyUtil.leaveBarrier, GameInstance.gameModifierItemStack);
    
    public static List<Player> queueList = new ArrayList<>();
    
    public static void joinQueue(Player player)
    {
        player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
        
        if (Assault.gameInstances.size() > 0)
        {
            for (GameInstance gameInstance : Assault.gameInstances)
            {
                if (!gameInstance.gameStage.equals(GameStage.FINISHED))
                {
                    player.sendRawMessage(Assault.ASSAULT_PREFIX + "Joining game!");
                    
                    for (Player otherPlayer : player.getWorld().getPlayers())
                    {
                        if (!otherPlayer.equals(player))
                        {
                            otherPlayer.sendRawMessage(Assault.ASSAULT_PREFIX + player.getDisplayName() + " has joined a game!");
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
                    worldPlayer.sendRawMessage(Assault.ASSAULT_PREFIX + "Game starting!");
                    worldPlayer.getInventory().clear();
                }
                else
                {
                    worldPlayer.sendRawMessage(Assault.ASSAULT_PREFIX + "A game is starting! Click the §dnether star§r to join!");
                    player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
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
            player.sendRawMessage(Assault.ASSAULT_PREFIX + "You've been added to the queue! The game will start when there are enough players.");
            
            for (Player otherPlayer : player.getWorld().getPlayers())
            {
                if (!otherPlayer.equals(player))
                {
                    otherPlayer.sendRawMessage(Assault.ASSAULT_PREFIX + player.getDisplayName() + " has joined the queue!");
                }
            }
            
            giveBarrier(player);
        }
    }
    
    public static void leaveQueue(Player player)
    {
        player.playSound(player.getLocation(), XSound.ENTITY_SKELETON_HURT.parseSound(), 1.0F, 1.0F);
        
        queueList.remove(player);
        player.getInventory().setItem(8, new ItemStack(XMaterial.AIR.parseMaterial()));
        LobbyUtil.giveStar(player);
        player.sendRawMessage(Assault.ASSAULT_PREFIX + "You've been removed from the queue. :(");
    }
    
    public static void sendRules(Player player)
    {
        LobbyUtil.sendGuideURL(player);
        player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_HARP.parseSound(), 1.0F, 1.0F);
    }
    
    private static void sendGuideURL(Player player)
    {
        String command = "tellraw " + player.getName() + " [\"\",{\"text\":\"[\",\"bold\":true,\"color\":\"dark_purple\"},{\"text\":\"Assault\",\"bold\":true,\"color\":\"light_purple\"},{\"text\":\"]\",\"bold\":true,\"color\":\"dark_purple\"},{\"text\":\" \",\"color\":\"blue\"},{\"text\":\"Click here to open the guide online!\",\"underlined\":true,\"color\":\"blue\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://github.com/TheKingElessar/Assault/wiki/How-to-Play\"}}]";
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
        Entity vehicle = player.getVehicle();
        player.leaveVehicle();
        if (vehicle != null)
        {
            vehicle.remove();
        }
        player.teleport(Assault.lobbySpawn);
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
        player.setCustomName(player.getName());
        player.setGameMode(Assault.lobbyGamemode);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(XMaterial.AIR.parseMaterial()), new ItemStack(XMaterial.AIR.parseMaterial()), new ItemStack(XMaterial.AIR.parseMaterial()), new ItemStack(XMaterial.AIR.parseMaterial())});
        LobbyUtil.giveStar(player);
        LobbyUtil.giveBook(player);
        player.getInventory().setHeldItemSlot(0);
        for (PotionEffect effect : player.getActivePotionEffects())
        {
            player.removePotionEffect(effect.getType());
        }
    }
    
    private static void giveGlass(Player player)
    {
        // spectator?
    }
}
