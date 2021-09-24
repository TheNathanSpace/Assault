package com.thekingelessar.assault.game.player;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.database.AssaultTableManager;
import com.thekingelessar.assault.database.Statistic;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.shops.ShopAttack;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.game.timertasks.TaskCountdownRespawn;
import com.thekingelessar.assault.util.Util;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class GamePlayer
{
    public Player player;
    public GameInstance gameInstance;
    public GameTeam gameTeam;
    public FastBoard scoreboard;
    
    public PlayerBank playerBank;
    public ShopAttack shopAttacking;
    
    public TaskCountdownRespawn taskCountdownRespawn;
    
    public long startTimeInAir = 0; // in nanoseconds
    
    public List<ItemStack> spawnItems = new ArrayList<>();
    public List<ItemStack> spawnArmor = new ArrayList<>();
    
    List<Material> itemsToDrop = Arrays.asList(Material.EMERALD, Material.TNT, Material.OBSIDIAN);
    
    public boolean flightReset = true;
    
    public GamePlayer(Player player, GameInstance gameInstance, GameTeam gameTeam)
    {
        this.player = player;
        this.gameInstance = gameInstance;
        this.playerBank = new PlayerBank(0);
        this.scoreboard = new FastBoard(player);
        this.gameTeam = gameTeam;
    }
    
    public void swapReset()
    {
        spawnItems = new ArrayList<>();
        spawnArmor = new ArrayList<>();
        
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(gameInstance.getPlayerTeam(player).color.color);
        bootsMeta.spigot().setUnbreakable(true);
        boots.setItemMeta(bootsMeta);
        spawnArmor.add(boots);
        
        ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        ItemMeta leggingsMeta = leggings.getItemMeta();
        leggingsMeta.spigot().setUnbreakable(true);
        leggings.setItemMeta(leggingsMeta);
        spawnArmor.add(leggings);
        
        ItemStack chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemMeta chestplateMeta = chestplate.getItemMeta();
        chestplateMeta.spigot().setUnbreakable(true);
        chestplate.setItemMeta(chestplateMeta);
        spawnArmor.add(chestplate);
        
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(gameInstance.getPlayerTeam(player).color.color);
        helmetMeta.spigot().setUnbreakable(true);
        helmet.setItemMeta(helmetMeta);
        spawnArmor.add(helmet);
        
        ItemStack woodSword = new ItemStack(Material.WOOD_SWORD);
        ItemMeta itemMeta = woodSword.getItemMeta();
        itemMeta.spigot().setUnbreakable(true);
        woodSword.setItemMeta(itemMeta);
        spawnItems.add(woodSword);
    }
    
    public void addSpawnItem(ItemStack spawnItem)
    {
        spawnItems.add(spawnItem);
    }
    
    public void spawn(PlayerMode playerMode, boolean keepInventory) // keepInventory and checking if GameStage is BUILDING is a little redundant
    {
        GameTeam playerTeam = gameInstance.getPlayerTeam(player);
        PlayerMode mode = PlayerMode.setPlayerMode(player, playerMode, gameInstance);
        
        Vector velocity = player.getVelocity();
        velocity.setX(0);
        velocity.setY(0);
        velocity.setZ(0);
        player.setVelocity(velocity);
        player.setFallDistance(0);
        
        player.teleport(gameInstance.gameMap.getSpawn(playerTeam, null).toLocation(gameInstance.gameWorld));
        player.setHealth(player.getMaxHealth());
        
        if (keepInventory)
        {
            player.getInventory().setArmorContents(spawnArmor.toArray(new ItemStack[0]));
            return;
        }
        
        if (!playerMode.equals(PlayerMode.BUILDING))
        {
            player.getInventory().clear();
        }
        
        player.getInventory().setArmorContents(spawnArmor.toArray(new ItemStack[0]));
        
        if (this.shopAttacking != null)
        {
            this.shopAttacking.downgradeAxe();
            this.shopAttacking.downgradePickaxe();
        }
        
        for (ItemStack itemStack : spawnItems)
        {
            if (!player.getInventory().contains(itemStack))
            {
                player.getInventory().addItem(itemStack);
            }
        }
    }
    
    public void respawn(PlayerMode playerMode, boolean delay, DeathType deathType)
    {
        try
        {
            this.player.playSound(player.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
        }
        catch (Throwable throwable)
        {
            this.player.playSound(player.getLocation(), Sound.valueOf("ENTITY_SKELETON_HURT"), 1.0F, 1.0F);
        }
        
        
        if (playerMode == null)
        {
            playerMode = this.gameInstance.getPlayerMode(this.player);
        }
        
        switch (deathType)
        {
            case SWORD:
            case BOW:
                PlayerInventory inventory = this.player.getInventory();
                for (ItemStack itemStack : inventory.getContents())
                {
                    if (itemStack != null)
                    {
                        if (itemsToDrop.contains(itemStack.getType()))
                        {
                            Location location = this.player.getLocation();
                            location.getWorld().dropItemNaturally(location, itemStack);
                        }
                    }
                }
                break;
            case VOID:
                this.indirectKill(deathType, false);
                break;
            case CONTACT:
            case FALL:
            case DEATH:
                this.indirectKill(deathType, true);
                break;
            case DROWNING:
                this.addDrownDeathFeed();
                break;
            case EXPLOSION:
                this.addExplosionDeathFeed();
                break;
        }
        
        AssaultTableManager.getInstance().incrementValue(this.player, Statistic.DEATHS);
        
        if (playerMode.equals(PlayerMode.BUILDING))
        {
            this.spawn(playerMode, false);
            return;
        }
        
        PlayerMode.setPlayerMode(player, PlayerMode.SPECTATOR, gameInstance);
        
        player.teleport(gameInstance.gameMap.waitingSpawn.toLocation(gameInstance.gameWorld));
        
        if (delay)
        {
            this.taskCountdownRespawn = new TaskCountdownRespawn(60, 0, 20, gameInstance, player);
            this.taskCountdownRespawn.runTaskTimer(Assault.INSTANCE, this.taskCountdownRespawn.startDelay, this.taskCountdownRespawn.tickDelay);
        }
        else
        {
            this.spawn(playerMode, false);
        }
    }
    
    public void indirectKill(DeathType deathType, boolean dropItems)
    {
        Player attacker = gameInstance.lastDamagedBy.get(player);
        
        String deathMessage = "";
        switch (deathType)
        {
            case VOID:
                if (attacker != null)
                {
                    deathMessage = this.addVoidDeathFeed(attacker);
                }
                else
                {
                    deathMessage = this.addVoidFallFeed();
                }
                break;
            case CONTACT:
                deathMessage = this.addContactDeathFeed();
                break;
            case FALL:
                deathMessage = this.addFallDeathFeed();
                break;
            case DROWNING:
                deathMessage = this.addDrownDeathFeed();
                break;
            case EXPLOSION:
                deathMessage = this.addExplosionDeathFeed();
                break;
            case DEATH:
                if (attacker != null)
                {
                    deathMessage = this.addRespawnDeathFeed(attacker);
                }
                else
                {
                    deathMessage = this.addDeathFeed();
                }
                break;
        }
        Assault.INSTANCE.getLogger().log(Level.INFO, ChatColor.stripColor(String.format("DEATH [%s]: %s", gameInstance.gameUUID, deathMessage)));
        
        if (attacker != null)
        {
            GamePlayer attackerPlayer = gameInstance.getGamePlayer(attacker);
            GamePlayer victimPlayer = gameInstance.getGamePlayer(player);
            
            attackerPlayer.killPlayer(victimPlayer.player, false);
            
            if (!dropItems)
            {
                int emeraldCount = 0;
                for (ItemStack itemStack : player.getInventory().getContents())
                {
                    if (itemStack != null && itemStack.getType().equals(Material.EMERALD))
                    {
                        emeraldCount += itemStack.getAmount();
                    }
                }
                
                if (emeraldCount > 0)
                {
                    attacker.getInventory().addItem(new ItemStack(Material.EMERALD, emeraldCount));
                }
            }
            
            attackerPlayer.updateScoreboard();
        }
        else if (dropItems)
        {
            PlayerInventory inventory = this.player.getInventory();
            for (ItemStack itemStack : inventory.getContents())
            {
                if (itemStack != null)
                {
                    if (itemsToDrop.contains(itemStack.getType()))
                    {
                        Location location = this.player.getLocation();
                        location.getWorld().dropItemNaturally(location, itemStack);
                    }
                }
            }
        }
        
        gameInstance.lastDamagedBy.put(player, null);
    }
    
    public void killPlayer(Player victim, boolean arrow)
    {
        AssaultTableManager.getInstance().incrementValue(this.player, Statistic.KILLS);
        if (!this.gameInstance.gameKills.containsKey(this.player.getUniqueId()))
        {
            this.gameInstance.gameKills.put(this.player.getUniqueId(), 0);
        }
        int gameKills = this.gameInstance.gameKills.get(this.player.getUniqueId());
        this.gameInstance.gameKills.put(this.player.getUniqueId(), gameKills += 1);
        
        if (this.gameTeam.teamStage.equals(TeamStage.ATTACKING))
        {
            gameTeam.gamerPoints += 1;
        }
        
        GamePlayer victimPlayer = gameInstance.getPlayerTeam(victim).getGamePlayer(victim);
        
        if (!arrow)
        {
            String deathMessage = victimPlayer.addSwordDeathFeed(this.player);
            Assault.INSTANCE.getLogger().log(Level.INFO, ChatColor.stripColor(String.format("DEATH [%s]: %s", gameInstance.gameUUID, deathMessage)));
        }
        
        this.playerBank.coins += (int) (0.2 * (victimPlayer.playerBank.coins));
        
        try
        {
            this.player.playSound(this.player.getLocation(), Sound.ORB_PICKUP, 0.8F, 1.0F);
        }
        catch (Throwable throwable)
        {
            this.player.playSound(this.player.getLocation(), Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"), 0.8F, 1.0F);
        }
        
        this.updateScoreboard();
    }
    
    public String addSwordDeathFeed(Player killer)
    {
        String message = killer.getDisplayName() + ChatColor.RESET + " stabbed " + this.player.getDisplayName() + ChatColor.RESET + " to death";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addBowDeathFeed(Player killer)
    {
        String message = killer.getDisplayName() + ChatColor.RESET + " shot " + this.player.getDisplayName() + ChatColor.RESET + " to death";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addVoidFallFeed()
    {
        String message = this.player.getDisplayName() + ChatColor.RESET + " fell into the void";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addVoidDeathFeed(Player killer)
    {
        String message = killer.getDisplayName() + ChatColor.RESET + " knocked " + this.player.getDisplayName() + ChatColor.RESET + " into the void";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addRespawnDeathFeed(Player killer)
    {
        String message = killer.getDisplayName() + ChatColor.RESET + " fought " + this.player.getDisplayName() + ChatColor.RESET + " to the edge";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addDeathFeed()
    {
        String message = this.player.getDisplayName() + ChatColor.RESET + " died";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addContactDeathFeed()
    {
        String message = this.player.getDisplayName() + ChatColor.RESET + " was pricked to death";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addFallDeathFeed()
    {
        String message = this.player.getDisplayName() + ChatColor.RESET + " fell to their death";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addDrownDeathFeed()
    {
        String message = this.player.getDisplayName() + ChatColor.RESET + " drowned";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addExplosionDeathFeed()
    {
        String message = this.player.getDisplayName() + ChatColor.RESET + " blew up";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public void updateScoreboard()
    {
        scoreboard.updateTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Assault");
        
        List<String> lines = new ArrayList<>();
        
        lines.add("");
        
        lines.add("Your team: " + gameInstance.getPlayerTeam(player).color.getFormattedName(false, false, ChatColor.BOLD) + ChatColor.RESET);
        
        lines.add("");
        
        if (gameInstance.gameStage.equals(GameStage.BUILDING))
        {
            lines.add(ChatColor.WHITE.toString() + ChatColor.BOLD + "Building time: " + ChatColor.RESET + Util.secondsToMinutes(gameInstance.buildingSecondsLeft, true));
        }
        else
        {
            String timeBlue = "";
            String timeRed = "";
            
            if (gameInstance.getTeamOne().didForfeit())
            {
                timeBlue = "Forfeit";
            }
            else
            {
                timeBlue = Util.secondsToMinutes((int) gameInstance.getTeamOne().displaySeconds, true);
            }
            
            if (gameInstance.getTeamTwo().didForfeit())
            {
                timeRed = "Forfeit";
            }
            else
            {
                timeRed = Util.secondsToMinutes((int) gameInstance.getTeamTwo().displaySeconds, true);
            }
            
            if (gameInstance.modFirstTo5Stars.enabled)
            {
                timeBlue += String.format(" %s%s✬", gameInstance.getTeamOne().starsPickedUp, ChatColor.WHITE);
                timeRed += String.format(" %s%s✬", gameInstance.getTeamTwo().starsPickedUp, ChatColor.WHITE);
            }
            
            lines.add(ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE" + ChatColor.RESET + ": " + timeBlue);
            lines.add(ChatColor.RED.toString() + ChatColor.BOLD + "RED" + ChatColor.RESET + ": " + timeRed);
        }
        
        lines.add("");
        
        lines.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "Coins" + ChatColor.RESET + ": " + playerBank.coins);
        lines.add(ChatColor.AQUA.toString() + ChatColor.BOLD + "Team Gamer Points" + ChatColor.RESET + ": " + gameInstance.getPlayerTeam(player).gamerPoints);
        
        lines.add("");
        
        scoreboard.updateLines(lines);
    }
    
    public boolean purchaseItem(int cost, Currency currency)
    {
        boolean emptySlot = false;
        PlayerInventory inventory = this.player.getInventory();
        for (ItemStack slot : inventory.getContents())
        {
            if (slot == null)
            {
                emptySlot = true;
                break;
            }
        }
        
        if (!emptySlot) return false;
        
        switch (currency)
        {
            case COINS:
                if (cost <= playerBank.coins)
                {
                    playerBank.coins -= cost;
                    
                    if (gameInstance.gameStage.equals(GameStage.BUILDING))
                    {
                        gameInstance.buildingCoinsRemaining.put(player, playerBank.coins);
                    }
                    
                    return true;
                }
                break;
            case EMERALDS:
                ItemStack[] inventoryContents = inventory.getContents();
                int emeraldCount = 0;
                
                for (ItemStack itemStack : inventoryContents)
                {
                    if (itemStack == null)
                    {
                        continue;
                    }
                    
                    if (itemStack.getType().equals(Material.EMERALD))
                    {
                        emeraldCount += itemStack.getAmount();
                    }
                }
                
                int spentEmeralds = 0;
                if (emeraldCount >= cost)
                {
                    for (int i = 0; i < inventoryContents.length; i++)
                    {
                        ItemStack itemStack = inventoryContents[i];
                        if (itemStack == null)
                        {
                            continue;
                        }
                        
                        int remainingCost = cost - spentEmeralds;
                        if (itemStack.getType().equals(Material.EMERALD))
                        {
                            if (itemStack.getAmount() == remainingCost)
                            {
                                itemStack.setAmount(0);
                                player.getInventory().setItem(i, itemStack);
                                return true;
                            }
                            else if (itemStack.getAmount() > remainingCost)
                            {
                                itemStack.setAmount(itemStack.getAmount() - (remainingCost));
                                player.getInventory().setItem(i, itemStack);
                                return true;
                            }
                            else if (itemStack.getAmount() < remainingCost)
                            {
                                spentEmeralds += itemStack.getAmount();
                                itemStack.setAmount(0);
                                player.getInventory().setItem(i, itemStack);
                            }
                        }
                    }
                }
                
                break;
            case GAMER_POINTS:
                GameTeam gameTeam = gameInstance.getPlayerTeam(player);
                
                if (cost <= gameTeam.gamerPoints)
                {
                    return true;
                }
        }
        
        return false;
    }
}
