package com.thekingelessar.assault.game.map;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.GameStage;
import com.thekingelessar.assault.game.team.TeamColor;
import com.thekingelessar.assault.util.Coordinate;
import com.thekingelessar.assault.util.Util;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.SkinLayers;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MapBase
{
    public static List<String> shopList = Arrays.asList("FrozenFruit", "TheKingElessar", "Willis644", "Mokabu456", "ForgeSmith", "TheAndvark", "pikaguy", "Dirko", "TheWindrunner", "d0bado", "Saanbu789", "Chadicus_Maximus", "Cyberes");
    
    public TeamColor teamColor;
    public List<Coordinate> defenderSpawns;
    public List<List<Coordinate>> defenderBoundingBoxes;
    public List<Coordinate> attackerSpawns;
    public List<Coordinate> emeraldSpawns;
    
    public List<Coordinate> itemShopCoords;
    public List<Coordinate> buffShopCoords;
    
    public List<NPC> itemShopNPCs = new ArrayList<>();
    public List<NPC> teamBuffShopNPCs = new ArrayList<>();
    
    public Coordinate objective;
    
    public MapBase(TeamColor teamColor, List<Coordinate> defenderSpawns, List<List<Coordinate>> defenderBoundingBoxes, List<Coordinate> attackerSpawns, List<Coordinate> emeraldSpawns, Coordinate objective, List<Coordinate> itemShopCoords, List<Coordinate> buffShopCoords)
    {
        this.teamColor = teamColor;
        this.defenderSpawns = defenderSpawns;
        this.defenderBoundingBoxes = defenderBoundingBoxes;
        this.attackerSpawns = attackerSpawns;
        this.emeraldSpawns = emeraldSpawns;
        
        this.itemShopCoords = itemShopCoords;
        this.buffShopCoords = buffShopCoords;
        
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
            
            SkinLayers trait = npc.getTrait(SkinLayers.class);
            trait.hideCape();
            trait.setVisible(SkinLayers.Layer.CAPE, false);
            
            if (shopLocation == null)
            {
                System.out.println("Shop location is null! " + this.teamColor);
            }
            
            npc.spawn(shopLocation);
            npc.getEntity().teleport(shopLocation);
            this.itemShopNPCs.add(npc);
            
            Entity npcEntity = npc.getEntity();
            if (npcEntity instanceof SkinnableEntity)
            {
                ((SkinnableEntity) npcEntity).getSkinTracker().notifySkinChange(true);
            }
            
            Assault.gameNPCs.add(npc);
        }
        
        if (gameInstance.gameStage.equals(GameStage.ATTACK_ROUNDS))
        {
            for (Coordinate buffShop : buffShopCoords)
            {
                Location buffShopLocation = buffShop.toLocation(gameInstance.gameWorld);
                Random random = new Random();
                String randomName = MapBase.shopList.get(random.nextInt(shopList.size()));
                
                NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Buff Shop");
                npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, randomName);
                npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, false);
                npc.addTrait(new BuffShopTrait());
                
                SkinLayers trait = npc.getTrait(SkinLayers.class);
                trait.hideCape();
                trait.setVisible(SkinLayers.Layer.CAPE, false);
                
                if (buffShopLocation == null)
                {
                    System.out.println("Shop location is null! " + this.teamColor);
                }
                
                npc.spawn(buffShopLocation);
                npc.getEntity().teleport(buffShopLocation);
                this.teamBuffShopNPCs.add(npc);
                
                Entity npcEntity = npc.getEntity();
                if (npcEntity instanceof SkinnableEntity)
                {
                    ((SkinnableEntity) npcEntity).getSkinTracker().notifySkinChange(true);
                }
                
                Assault.gameNPCs.add(npc);
            }
        }
    }
    
    public void destroyShops()
    {
        List<NPC> removedIndeces = new ArrayList<>();
        List<NPC> shopNPCs = this.itemShopNPCs;
        for (NPC npc : shopNPCs)
        {
            Assault.gameNPCs.remove(npc);
            removedIndeces.add(npc);
            npc.despawn();
            npc.destroy();
        }
        this.itemShopNPCs.removeAll(removedIndeces);
        
        if (this.teamBuffShopNPCs.size() != 0)
        {
            List<NPC> removeList = new ArrayList<>();
            for (NPC npc : this.teamBuffShopNPCs)
            {
                Assault.gameNPCs.remove(npc);
                npc.despawn();
                npc.destroy();
                removeList.add(npc);
            }
            this.teamBuffShopNPCs.removeAll(removeList);
        }
    }
    
    public boolean isInDefenderBoundingBox(Location location)
    {
        int i = 0;
        for (List<Coordinate> boundingBox : this.defenderBoundingBoxes)
        {
            if (Util.isInside(location, boundingBox.get(0).toLocation(null), boundingBox.get(1).toLocation(null)))
            {
                return true;
            }
        }
        return false;
    }
}
