package com.thekingelessar.assault.game.eventhandlers.world;

import com.thekingelessar.assault.database.AssaultTableManager;
import com.thekingelessar.assault.database.Statistic;
import com.thekingelessar.assault.game.GameEndManager;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.player.GamePlayer;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.util.FireworkUtils;
import com.thekingelessar.assault.util.Title;
import com.thekingelessar.assault.util.version.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerPickupItemHandler implements Listener
{
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent playerPickupItemEvent)
    {
        Material pickedUp = playerPickupItemEvent.getItem().getItemStack().getType();
        Player player = playerPickupItemEvent.getPlayer();
        
        List<Material> swordList = Arrays.asList(XMaterial.STONE_SWORD.parseMaterial(), XMaterial.IRON_SWORD.parseMaterial(), XMaterial.GOLDEN_SWORD.parseMaterial(), XMaterial.DIAMOND_SWORD.parseMaterial());
        if (swordList.contains(pickedUp))
        {
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length; i++)
            {
                ItemStack inventoryItem = contents[i];
                if (inventoryItem != null && inventoryItem.getType().equals(XMaterial.WOODEN_SWORD.parseMaterial()))
                {
                    player.getInventory().setItem(i, playerPickupItemEvent.getItem().getItemStack());
                    playerPickupItemEvent.setCancelled(true);
                    playerPickupItemEvent.getItem().remove();
                    break;
                }
            }
        }
        
        if (pickedUp.equals(XMaterial.NETHER_STAR.parseMaterial()))
        {
            GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
            
            if (gameInstance != null)
            {
                GameTeam gameTeam = gameInstance.getPlayerTeam(player);
                
                if (gameInstance.gameStage.equals(GameStage.BUILDING) || !gameInstance.getAttackingTeam().equals(gameTeam))
                {
                    playerPickupItemEvent.setCancelled(true);
                    return;
                }
                
                if (gameInstance.taskAttackTimer == null)
                {
                    playerPickupItemEvent.setCancelled(true);
                    return;
                }
                
                AssaultTableManager.getInstance().incrementValue(player, Statistic.STARS);
                UUID uuid = player.getUniqueId();
                if (gameInstance.starsInGame.containsKey(uuid))
                {
                    int oldStars = gameInstance.starsInGame.get(uuid);
                    gameInstance.starsInGame.put(uuid, oldStars + 1);
                }
                else
                {
                    gameInstance.starsInGame.put(uuid, 1);
                }
                
                int mostStarsOld = (int) AssaultTableManager.getInstance().getValue(uuid, Statistic.MOST_STARS_IN_SINGLE_GAME);
                if (gameInstance.starsInGame.containsKey(uuid))
                {
                    int mostStarsNew = gameInstance.starsInGame.get(uuid);
                    if (mostStarsNew > mostStarsOld)
                    {
                        AssaultTableManager.getInstance().insertValue(player, Statistic.MOST_STARS_IN_SINGLE_GAME, mostStarsNew);
                    }
                }
                
                gameTeam.starsPickedUp += 1;
                if (gameInstance.modFirstTo5Stars.enabled && gameTeam.starsPickedUp != 5)
                {
                    for (int i = 0; i < 2; i++)
                    {
                        FireworkUtils.spawnRandomFirework(player.getLocation(), gameInstance.getAttackingTeam().color);
                    }
                    
                    GamePlayer gamePlayer = gameInstance.getGamePlayer(player);
                    gamePlayer.spawn(gameInstance.getPlayerMode(player), true);
                    
                    for (int i = 0; i < 2; i++)
                    {
                        FireworkUtils.spawnRandomFirework(player.getLocation(), gameInstance.getAttackingTeam().color);
                    }
                    
                    String mainTitle = gameInstance.getAttackingTeam().color.getFormattedName(true, true, ChatColor.BOLD) + ChatColor.WHITE + " reached a star!";
                    Title title = new Title(mainTitle, String.format("%s%s✬%s remaining!", ChatColor.BOLD, 5 - gameTeam.starsPickedUp, ChatColor.RESET));
                    
                    List<Player> players = gameInstance.gameWorld.getPlayers();
                    for (Player player1 : players)
                    {
                        title.clearTitle(player1);
                        title.send(player1);
                    }
                    
                    playerPickupItemEvent.setCancelled(true);
                    
                    return;
                }
                
                gameInstance.endRound(false);
                
                if (gameInstance.isOneTeamRemaining())
                {
                    gameInstance.winningTeam = gameInstance.getRemainingTeam();
                    gameInstance.gameEndManager.declareWinners(GameEndManager.WinState.DEFENDERS_LEFT);
                    return;
                }
                
                if (gameInstance.teamsGone == 1)
                {
                    if (gameInstance.isTie())
                    {
                        gameInstance.gameEndManager.declareWinners(GameEndManager.WinState.TIE);
                    }
                    gameInstance.gameEndManager.declareWinners(GameEndManager.WinState.LOWEST_TIME);
                }
            }
        }
    }
}