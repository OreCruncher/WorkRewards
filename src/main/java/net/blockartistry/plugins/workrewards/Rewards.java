/*
* Copyright (C) 2014 OreCruncher (OreCruncher@gmail.com)
*
* This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
* To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the License for more details. You should have
* received a copy of the License along with this program. If not, see http://creativecommons.org/licenses/by-nc-sa/4.0/.
*/

package net.blockartistry.plugins.workrewards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class Rewards
{
    protected final WorkRewards plugin;

    //  The String key is forced upper case when inserted.  This ensures
    //  consistent use context, as well as the fact that a majority of the
    //  time the string will be upper case already thus allowing the
    //  underlying string routines to be more optimal in memory use.
    private HashMap<String, Double> rewardList;
    private HashMap<String, Double> worldModifiers;

    public Rewards(WorkRewards plugin)
    {
        this.plugin = plugin;
    }

    protected void addIfNotPresent(Material material)
    {
        String key = material.toString().toUpperCase();
        if (!rewardList.containsKey(key))
        {
            setReward(key, 0.0f);
        }
    }

    protected void addIfNotPresent(World world)
    {
        String key = world.getName();
        if (!worldModifiers.containsKey(key))
        {
            setWorld(key, 1.0f);
        }
    }

    public void dumpMaterialEnum()
    {
        List<String> material = new ArrayList<>();
        Material[] list = Material.values();

        for (Material m : list)
        {
            if (m.isBlock() && m.isSolid())
            {
                material.add(m.toString());
            }
        }

        try
        {
            FileConfiguration outConfig = new YamlConfiguration();
            outConfig.set("blocks", material);
            outConfig.save(new File(plugin.getDataFolder(), "blocks.yml"));
        }
        catch (IOException ex)
        {
            plugin.getLogger().severe("Unable to write material data to output file: " + ex.toString());
        }
    }

    protected void initMapFromOres()
    {
        Logger log = plugin.getLogger();

        log.info("Initializing material list");
        addIfNotPresent(Material.DIAMOND_ORE);
        addIfNotPresent(Material.COAL_ORE);
        addIfNotPresent(Material.REDSTONE_ORE);
        addIfNotPresent(Material.GLOWING_REDSTONE_ORE);
        addIfNotPresent(Material.GLOWSTONE);
        addIfNotPresent(Material.LAPIS_ORE);
        addIfNotPresent(Material.QUARTZ_ORE);
        addIfNotPresent(Material.EMERALD_ORE);
        addIfNotPresent(Material.MOB_SPAWNER);
        addIfNotPresent(Material.MONSTER_EGGS); // Stone block that spawns silver fish
    }

    protected void initMultiplierWorlds()
    {
        List<World> worlds = Bukkit.getWorlds();
        for (World w : worlds)
        {
            addIfNotPresent(w);
        }
    }

    public void load(FileConfiguration config)
    {
        Logger log = plugin.getLogger();

        rewardList = new HashMap<>();
        worldModifiers = new HashMap<>();

        //  Get what is in the config and merge
        log.info("Loading configuration data");
        ConfigurationSection section = config.getConfigurationSection("rewards");

        if (section != null)
        {
            Map<String, Object> rewards = section.getValues(false);
            for (Map.Entry<String, Object> entry : rewards.entrySet())
            {
                setReward(entry.getKey(), Double.parseDouble(entry.getValue().toString()));
            }
        }
        else
        {
            log.info("No reward data present");
        }

        section = config.getConfigurationSection("worldModifiers");
        if (section != null)
        {
            Map<String, Object> modifiers = section.getValues(false);
            for (Map.Entry<String, Object> entry : modifiers.entrySet())
            {
                setWorld(entry.getKey(), Double.parseDouble(entry.getValue().toString()));
            }
        }
        else
        {
            log.info("No world modifier data present");
        }

        //  Get the entities that are known in the server
        initMapFromOres();
        initMultiplierWorlds();

        //  Flush it back out to disk because there may be
        //  new entries
        log.info("Saving rewards configuration");
        save(config);
    }

    public void save(FileConfiguration config)
    {
        ConfigurationSection worlds = config.createSection("worldModifiers", worldModifiers);
        if (worlds == null)
        {
            plugin.getLogger().info("Unable to save world modifier data into config file!");
        }

        ConfigurationSection section = config.createSection("rewards", rewardList);

        if (section == null)
        {
            plugin.getLogger().info("Unable to save reward data into config file!");
        }
    }

    public void setReward(String reward, double amount)
    {
        if (reward != null)
        {
            rewardList.put(reward.toUpperCase(), amount);
        }
    }

    public void setWorld(String world, double multiplier)
    {
        if (world != null)
        {
            worldModifiers.put(world.toUpperCase(), multiplier);
        }

    }

    public double getReward(String reward, String world)
    {
        Double r = null;
        Double m = null;
        if (reward != null && world != null)
        {
            r = rewardList.get(reward.toUpperCase());
            m = worldModifiers.get(world.toUpperCase());
        }
        return r == null ? -1.0 : r * ((m == null) ? (1.0d) : (m));
    }

    public Map<String, Double> getRewardList()
    {
        HashMap<String, Double> result = new HashMap<>();

        for (Map.Entry<String, Double> entry : rewardList.entrySet())
        {
            Double r = entry.getValue();
            if (r != null && r > 0.0)
            {
                result.put(entry.getKey(), r);
            }
        }

        return result;
    }

    public Map<String, Double> getModifierList()
    {
        return new HashMap<>(worldModifiers);
    }
}
