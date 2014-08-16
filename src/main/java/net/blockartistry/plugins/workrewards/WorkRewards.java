package net.blockartistry.plugins.workrewards;

import org.bukkit.plugin.java.JavaPlugin;

public final class WorkRewards extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        getLogger().info("onEnable() invoked");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("onDisable() invoked");
    }

}
