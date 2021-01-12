package com.thekingelessar.assault.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static com.thekingelessar.assault.Assault.INSTANCE;

public class ConfigMaker extends YamlConfiguration
{
    private final String fileName;
    private final String dir;
    
    public ConfigMaker(String fileName, String dir)
    {
        this.fileName = fileName + (fileName.endsWith(".yml") ? "" : ".yml");
        this.dir = dir;
        createFile();
    }
    
    private void createFile()
    {
        try
        {
            File file = new File(INSTANCE.getDataFolder() + dir, fileName);
            if (!file.exists())
            {
                if (INSTANCE.getResource(fileName) != null)
                {
                    INSTANCE.saveResource(fileName, false);
                }
                else
                {
                    save(file);
                }
            }
            else
            {
                load(file);
                save(file);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void save()
    {
        try
        {
            save(new File(INSTANCE.getDataFolder() + dir, fileName));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}