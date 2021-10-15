package com.thekingelessar.assault.game.player;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.database.AssaultTableManager;
import com.thekingelessar.assault.database.Statistic;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.Objective;
import com.thekingelessar.assault.game.inventory.Currency;
import com.thekingelessar.assault.game.inventory.shopitems.ShopItemArmor;
import com.thekingelessar.assault.game.inventory.shops.ShopAttacking;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.game.timertasks.TaskCountdownRespawn;
import com.thekingelessar.assault.util.Util;
import com.thekingelessar.assault.util.version.XMaterial;
import com.thekingelessar.assault.util.version.XSound;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class GamePlayer
{
    public Player player;
    public GameInstance gameInstance;
    public GameTeam gameTeam;
    public FastBoard scoreboard;
    
    public PlayerBank playerBank;
    public ShopAttacking shopAttacking;
    
    public TaskCountdownRespawn taskCountdownRespawn;
    
    public long startTimeInAir = 0; // in nanoseconds
    
    public List<ItemStack> spawnItems = new ArrayList<>();
    public List<ItemStack> spawnArmor = new ArrayList<>();
    
    List<Material> itemsToDrop = Arrays.asList(XMaterial.EMERALD.parseMaterial(), XMaterial.TNT.parseMaterial(), XMaterial.OBSIDIAN.parseMaterial());
    
    public long lastCompassUpdate = 0;
    
    public boolean flightReset = true;
    
    public GamePlayer(Player player, GameInstance gameInstance, GameTeam gameTeam)
    {
        this.player = player;
        this.gameInstance = gameInstance;
        this.playerBank = new PlayerBank(0, this);
        this.scoreboard = new FastBoard(player)
        {
            @Override
            public boolean hasLinesMaxLength()
            {
                return true;
            }
        };
        this.gameTeam = gameTeam;
    }
    
    public void swapReset()
    {
        spawnItems = new ArrayList<>();
        spawnArmor = new ArrayList<>();
        
        ItemStack boots = new ItemStack(XMaterial.LEATHER_BOOTS.parseMaterial());
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(gameInstance.getPlayerTeam(player).color.color);
        boots.setItemMeta(bootsMeta);
        Assault.unbreakable.setUnbreakable(boots);
        spawnArmor.add(boots);
        
        ItemStack leggings = new ItemStack(XMaterial.CHAINMAIL_LEGGINGS.parseMaterial());
        Assault.unbreakable.setUnbreakable(leggings);
        spawnArmor.add(leggings);
        
        ItemStack chestplate = new ItemStack(XMaterial.CHAINMAIL_CHESTPLATE.parseMaterial());
        Assault.unbreakable.setUnbreakable(leggings);
        spawnArmor.add(chestplate);
        
        ItemStack helmet = new ItemStack(XMaterial.LEATHER_HELMET.parseMaterial());
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(gameInstance.getPlayerTeam(player).color.color);
        helmet.setItemMeta(helmetMeta);
        Assault.unbreakable.setUnbreakable(helmet);
        spawnArmor.add(helmet);
        
        ItemStack woodSword = new ItemStack(XMaterial.WOODEN_SWORD.parseMaterial());
        Assault.unbreakable.setUnbreakable(woodSword);
        spawnItems.add(woodSword);
        
        ItemStack compass = new ItemStack(XMaterial.COMPASS.parseMaterial());
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Nearest Star");
        compassMeta.setLore(Arrays.asList(Util.RESET_CHAT + "This compass points towards the", Util.RESET_CHAT + "nearest Nether Star!"));
        compass.setItemMeta(compassMeta);
        spawnItems.add(compass);
    }
    
    public void setArmor(Material chestplate, Material leggings)
    {
        for (ItemStack armorPiece : new ArrayList<>(this.spawnArmor))
        {
            Material armorMaterial = armorPiece.getType();
            if (ShopItemArmor.CHESTPLATES.contains(armorMaterial))
            {
                this.spawnArmor.remove(armorPiece);
            }
            else if (ShopItemArmor.LEGGINGS.contains(armorMaterial))
            {
                this.spawnArmor.remove(armorPiece);
            }
        }
        
        ItemStack itemChestplate = new ItemStack(chestplate);
        Assault.unbreakable.setUnbreakable(itemChestplate);
        spawnArmor.add(itemChestplate);
        
        ItemStack itemLeggings = new ItemStack(leggings);
        Assault.unbreakable.setUnbreakable(itemLeggings);
        spawnArmor.add(itemLeggings);
        
        this.spawnArmor = Util.sortArmor(this.spawnArmor);
        this.player.getInventory().setArmorContents(spawnArmor.toArray(new ItemStack[0]));
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
        player.setVelocity(velocity.zero());
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
            player.setItemOnCursor(null);
            player.getOpenInventory().getTopInventory().clear();
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
        
        this.setCompassObjective();
    }
    
    public void respawn(PlayerMode playerMode, boolean delay, DeathType deathType)
    {
        this.player.playSound(player.getLocation(), XSound.ENTITY_SKELETON_HURT.parseSound(), 1.0F, 1.0F);
        
        
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
        
        UUID uuid = player.getUniqueId();
        if (this.gameInstance.deathsInGame.containsKey(uuid))
        {
            int oldDeaths = this.gameInstance.deathsInGame.get(uuid);
            this.gameInstance.deathsInGame.put(uuid, oldDeaths + 1);
        }
        else
        {
            this.gameInstance.deathsInGame.put(uuid, 1);
        }
        
        int mostDeathsOld = (int) AssaultTableManager.getInstance().getValue(uuid, Statistic.MOST_DEATHS_IN_SINGLE_GAME);
        if (gameInstance.deathsInGame.containsKey(uuid))
        {
            int mostDeathsNew = gameInstance.deathsInGame.get(uuid);
            if (mostDeathsNew > mostDeathsOld)
            {
                AssaultTableManager.getInstance().insertValue(player, Statistic.MOST_DEATHS_IN_SINGLE_GAME, mostDeathsNew);
            }
        }
        
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
            
            attackerPlayer.killPlayer(victimPlayer.player, false, true);
            
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
    
    public void killPlayer(Player victim, boolean arrow, boolean indirect)
    {
        AssaultTableManager.getInstance().incrementValue(this.player, Statistic.KILLS);
        
        UUID uuid = player.getUniqueId();
        if (this.gameInstance.killsInGame.containsKey(uuid))
        {
            int oldKills = this.gameInstance.killsInGame.get(uuid);
            this.gameInstance.killsInGame.put(uuid, oldKills + 1);
        }
        else
        {
            this.gameInstance.killsInGame.put(uuid, 1);
        }
        
        int mostKillsOld = (int) AssaultTableManager.getInstance().getValue(uuid, Statistic.MOST_KILLS_IN_SINGLE_GAME);
        if (gameInstance.killsInGame.containsKey(uuid))
        {
            int mostKillsNew = gameInstance.killsInGame.get(uuid);
            if (mostKillsNew > mostKillsOld)
            {
                AssaultTableManager.getInstance().insertValue(player, Statistic.MOST_KILLS_IN_SINGLE_GAME, mostKillsNew);
            }
        }
        
        if (this.gameTeam.teamStage.equals(TeamStage.ATTACKING))
        {
            gameTeam.gamerPoints += 1;
        }
        
        GamePlayer victimPlayer = gameInstance.getPlayerTeam(victim).getGamePlayer(victim);
        
        if (!arrow && !indirect)
        {
            String deathMessage = victimPlayer.addSwordDeathFeed(this.player);
            Assault.INSTANCE.getLogger().log(Level.INFO, ChatColor.stripColor(String.format("DEATH [%s]: %s", gameInstance.gameUUID, deathMessage)));
        }
        
        if (this.gameInstance.gameMap.flatCoinsOnKill)
        {
            this.playerBank.addCoins(this.gameInstance.gameMap.coinsOnKill);
        }
        else if (this.gameInstance.gameMap.percentCoinsOnKill)
        {
            this.playerBank.addCoins((int) (this.gameInstance.gameMap.rateOnKill * (victimPlayer.playerBank.coins)));
        }
        
        this.player.playSound(this.player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 0.8F, 1.0F);
        
        this.updateScoreboard();
    }
    
    public String addSwordDeathFeed(Player killer)
    {
        String message = killer.getDisplayName() + Util.RESET_CHAT + " stabbed " + this.player.getDisplayName() + Util.RESET_CHAT + " to death";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addBowDeathFeed(Player killer)
    {
        String message = killer.getDisplayName() + Util.RESET_CHAT + " shot " + this.player.getDisplayName() + Util.RESET_CHAT + " to death";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addVoidFallFeed()
    {
        String message = this.player.getDisplayName() + Util.RESET_CHAT + " fell into the void";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addVoidDeathFeed(Player killer)
    {
        String message = killer.getDisplayName() + Util.RESET_CHAT + " knocked " + this.player.getDisplayName() + Util.RESET_CHAT + " into the void";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addRespawnDeathFeed(Player killer)
    {
        String message = killer.getDisplayName() + Util.RESET_CHAT + " fought " + this.player.getDisplayName() + Util.RESET_CHAT + " to the edge";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addDeathFeed()
    {
        String message = this.player.getDisplayName() + Util.RESET_CHAT + " died";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addContactDeathFeed()
    {
        String message = this.player.getDisplayName() + Util.RESET_CHAT + " was pricked to death";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addFallDeathFeed()
    {
        String message = this.player.getDisplayName() + Util.RESET_CHAT + " fell to their death";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addDrownDeathFeed()
    {
        String message = this.player.getDisplayName() + Util.RESET_CHAT + " drowned";
        for (Player player : this.gameInstance.getPlayers())
        {
            player.sendRawMessage(message);
        }
        return message;
    }
    
    public String addExplosionDeathFeed()
    {
        String message = this.player.getDisplayName() + Util.RESET_CHAT + " blew up";
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
        
        lines.add("Your team: " + gameInstance.getPlayerTeam(player).color.getFormattedName(false, false, ChatColor.BOLD) + Util.RESET_CHAT);
        lines.add("");
        
        if (gameInstance.gameStage.equals(GameStage.BUILDING))
        {
            lines.add(ChatColor.WHITE.toString() + ChatColor.BOLD + "Building time: " + Util.RESET_CHAT + Util.secondsToMinutes(gameInstance.buildingSecondsLeft, true));
        }
        else
        {
            String teamOneTime = "";
            String teamTwoTime = "";
            
            if (gameInstance.getTeamOne().didForfeit())
            {
                teamOneTime = "Forfeit";
            }
            else
            {
                teamOneTime = Util.secondsToMinutes((int) gameInstance.getTeamOne().displaySeconds, true);
            }
            
            if (gameInstance.getTeamTwo().didForfeit())
            {
                teamTwoTime = "Forfeit";
            }
            else
            {
                teamTwoTime = Util.secondsToMinutes((int) gameInstance.getTeamTwo().displaySeconds, true);
            }
            
            if (gameInstance.modFirstTo5Stars.enabled)
            {
                teamOneTime += String.format(" %s%s✬", gameInstance.getTeamOne().starsPickedUp, ChatColor.WHITE);
                teamTwoTime += String.format(" %s%s✬", gameInstance.getTeamTwo().starsPickedUp, ChatColor.WHITE);
            }
            
            lines.add(gameInstance.getTeamOne().color.getFormattedName(false, true, ChatColor.BOLD) + Util.RESET_CHAT + ": " + teamOneTime);
            lines.add(gameInstance.getTeamTwo().color.getFormattedName(false, true, ChatColor.BOLD) + Util.RESET_CHAT + ": " + teamTwoTime);
        }
        
        lines.add("");
        
        lines.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "Coins" + Util.RESET_CHAT + ": " + playerBank.coins);
        lines.add(ChatColor.AQUA.toString() + ChatColor.BOLD + "Team Gamer Points" + Util.RESET_CHAT + ": " + gameInstance.getPlayerTeam(player).gamerPoints);
        
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
    
    public void setCompassObjective()
    {
        long currentTime = System.nanoTime();
        if ((currentTime - this.lastCompassUpdate) / 1000000000. > 1)
        {
            Location playerLocation = this.player.getLocation();
            Objective closestObjective = null;
            double smallestDistance = Double.MAX_VALUE;
            
            List<Objective> compassObjectives = this.gameInstance.getCompassObjectives(this);
            if (compassObjectives == null || compassObjectives.size() == 0)
            {
                this.player.setCompassTarget(new Location(this.gameInstance.gameWorld, 0, 0, 0));
                return;
            }
            
            for (Objective objective : compassObjectives)
            {
                if (objective.item != null)
                {
                    double distanceSquare = objective.item.getLocation().distanceSquared(playerLocation);
                    if (distanceSquare < smallestDistance)
                    {
                        closestObjective = objective;
                        smallestDistance = distanceSquare;
                    }
                }
            }
            
            if (closestObjective != null)
            {
                this.player.setCompassTarget(closestObjective.item.getLocation());
            }
        }
    }
}
