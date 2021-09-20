package com.thekingelessar.assault.game.teambuffs;

import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BuffSharpness implements IBuff
{
    public GameTeam gameTeam;
    
    public BuffSharpness(GameTeam gameTeam)
    {
        this.gameTeam = gameTeam;
    }
    
    @Override
    public void doEffect()
    {
        for (Player player : gameTeam.getPlayers())
        {
            ItemStack[] items = player.getInventory().getContents();
            for (int i = 0; i < items.length; i++)
            {
                ItemStack itemStack = items[i];
                if (itemStack != null)
                {
                    if (Enchantment.DAMAGE_ALL.canEnchantItem(itemStack))
                    {
                        itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                        items[i] = itemStack;
                    }
                }
            }
            player.getInventory().setContents(items);
        }
    }
}
