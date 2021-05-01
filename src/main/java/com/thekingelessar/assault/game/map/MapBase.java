package com.thekingelessar.assault.game.map;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.util.Coordinate;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MapBase
{
    public static List<String> shopList = Arrays.asList("FrozenFruit", "TheKingElessar", "Willis644", "Mokabu456", "ForgeSmith", "TheAndvark", "pikaguy", "Dirko", "TheWindrunner", "d0bado", "Saanbu789", "AshFire265", "Chadicus_Maximus", "Cyberes");
    
    public TeamColor teamColor;
    public Coordinate defenderSpawn;
    public List<Coordinate> defenderBoundingBox;
    public Coordinate attackerSpawn;
    public List<Coordinate> emeraldSpawns;
    
    public List<Coordinate> itemShopCoords;
    public Coordinate teamBuffShopCoords;
    
    public List<NPC> itemShopNPCs = new ArrayList<>();
    public NPC teamBuffShopNPC;
    
    public Coordinate objective;
    
    public MapBase(TeamColor teamColor, Coordinate defenderSpawn, List<Coordinate> defenderBoundingBox, Coordinate attackerSpawn, List<Coordinate> emeraldSpawns, Coordinate objective, List<Coordinate> itemShopCoords, Coordinate teamBuffShopCoords)
    {
        this.teamColor = teamColor;
        this.defenderSpawn = defenderSpawn;
        this.defenderBoundingBox = defenderBoundingBox;
        this.attackerSpawn = attackerSpawn;
        this.emeraldSpawns = emeraldSpawns;
        
        this.itemShopCoords = itemShopCoords;
        this.teamBuffShopCoords = teamBuffShopCoords;
        
        this.objective = objective;
    }
    
    public void spawnShops(GameInstance gameInstance)
    {
        for (Coordinate coordinate : this.itemShopCoords)
        {
            Location shopLocation = coordinate.toLocation(gameInstance.gameWorld);
            Random random = new Random();
            String randomName = MapBase.shopList.get(random.nextInt(shopList.size()));
            
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Shop");
            npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, randomName);
            npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, false);
            npc.addTrait(new ItemShopTrait());
            
            npc.spawn(shopLocation);
            npc.getEntity().teleport(shopLocation);
            this.itemShopNPCs.add(npc);
            
            Entity npcEntity = npc.getEntity();
            if (npcEntity instanceof SkinnableEntity)
            {
                ((SkinnableEntity) npcEntity).getSkinTracker().notifySkinChange(true);
            }
        }
        
        if (gameInstance.gameStage.equals(GameStage.ATTACKING))
        {
            Location buffShopLocation = teamBuffShopCoords.toLocation(gameInstance.gameWorld);
            Random random = new Random();
            String randomName = MapBase.shopList.get(random.nextInt(shopList.size()));
            
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Buff Shop");
            npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, randomName);
            npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, false);
            npc.addTrait(new BuffShopTrait());
            
            npc.spawn(buffShopLocation);
            npc.getEntity().teleport(buffShopLocation);
            this.teamBuffShopNPC = npc;
            
            Entity npcEntity = npc.getEntity();
            if (npcEntity instanceof SkinnableEntity)
            {
                ((SkinnableEntity) npcEntity).getSkinTracker().notifySkinChange(true);
            }
        }
    }
    
    public void destroyShops()
    {
        List<NPC> removedIndeces = new ArrayList<>();
        List<NPC> shopNPCs = this.itemShopNPCs;
        for (NPC npc : shopNPCs)
        {
            removedIndeces.add(npc);
            npc.despawn();
            npc.destroy();
        }
        
        this.itemShopNPCs.removeAll(removedIndeces);
        
        if (this.teamBuffShopNPC != null)
        {
            this.teamBuffShopNPC.despawn();
            this.teamBuffShopNPC.destroy();
            this.teamBuffShopNPC = null;
        }
    }
}
