package com.thekingelessar.assault.game;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.UUID;

public class Team
{
    public ArrayList<UUID> members = new ArrayList<>();
    
    public Team()
    {
    
    }
    
    public void addMember(UUID uuid)
    {
        members.add(uuid);
    }
    
    public void addMember(String string)
    {
        members.add(Bukkit.getPlayer(string).getUniqueId());
    }
}
