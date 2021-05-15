package com.thekingelessar.assault.game.inventory.teambuffs;

import com.thekingelessar.assault.game.team.GameTeam;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BuffProtection implements IBuff
{
    public GameTeam gameTeam;
    
    public BuffProtection(GameTeam gameTeam)
    {
        this.gameTeam = gameTeam;
    }
    
    @Override
    public void doEffect()
    {
        for (Player player : gameTeam.getPlayers())
        {
            ItemStack[] armor = player.getInventory().getArmorContents();
            for (int i = 0; i < armor.length; i++)
            {
                ItemStack itemStack = armor[i];
                if (itemStack != null)
                {
                    if (Enchantment.PROTECTION_ENVIRONMENTAL.canEnchantItem(itemStack))
                    {
                        itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                        armor[i] = itemStack;
                    }
                }
            }
            player.getInventory().setArmorContents(armor);
        }
    }
}
