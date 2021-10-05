package com.thekingelessar.assault.version.v1_10_2;

import com.thekingelessar.assault.bridge.IUnbreakable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Unbreakable_v1_10_2 implements IUnbreakable
{
    @Override
    public void setUnbreakable(ItemStack itemStack)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.spigot().setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
    }
}