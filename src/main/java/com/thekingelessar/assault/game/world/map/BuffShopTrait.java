package com.thekingelessar.assault.game.world.map;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.team.GameTeam;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.InventoryView;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
//the Trait class has a reference to the attached NPC class through the protected field 'npc' or getNPC().
//The Trait class also implements Listener so you can add EventHandlers directly to your trait.
public class BuffShopTrait extends Trait
{
    public BuffShopTrait()
    {
        super("buffshoptrait");
        plugin = Assault.INSTANCE;
    }
    
    Assault plugin = null;
    
    @EventHandler
    public void rightClick(net.citizensnpcs.api.event.NPCRightClickEvent rightClickEvent)
    {
        if (rightClickEvent.getNPC().equals(this.getNPC()))
        {
            this.openShop(rightClickEvent.getClicker());
        }
    }
    
    @EventHandler
    public void leftClick(net.citizensnpcs.api.event.NPCLeftClickEvent leftClickEvent)
    {
        if (leftClickEvent.getNPC().equals(this.getNPC()))
        {
            this.openShop(leftClickEvent.getClicker());
        }
    }
    
    public void openShop(Player player)
    {
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        
        if (gameInstance != null)
        {
            GameTeam gameTeam = gameInstance.getPlayerTeam(player);
            if (gameTeam != null && gameTeam.equals(gameInstance.getAttackingTeam()))
            {
                InventoryView inventoryView = player.openInventory(gameTeam.shopTeamBuffs.inventory);
            }
        }
    }
}
