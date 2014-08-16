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

import org.bukkit.Material;
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

    private HashMap<String, Double> rewardList;

    public Rewards(WorkRewards plugin)
    {
        this.plugin = plugin;
    }

    protected void addIfNotPresent(Material material)
    {
        String key = material.toString();
        if (!rewardList.containsKey(key))
        {
            setReward(key, 0.0f);
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
        } catch (IOException ex)
        {
            plugin.getLogger().severe("Unable to write material data to output file: " + ex.toString());
        }
    }

    protected void initMapFromOres()
    {
        Logger log = plugin.getLogger();

        log.info("Initializing material list");
        //addIfNotPresent(Material.IRON_ORE);
        //addIfNotPresent(Material.GOLD_ORE);
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

    public void load(FileConfiguration config)
    {
        Logger log = plugin.getLogger();

        rewardList = new HashMap<>();

        //  Get what is in the config and merge
        log.info("Loading configuration data");
        ConfigurationSection section = config.getConfigurationSection("rewards");

        if (section != null)
        {
            Map<String, Object> rewards = section.getValues(false);
            for (Map.Entry<String, Object> entry : rewards.entrySet())
            {
                setReward(entry.getKey().toUpperCase(), Double.parseDouble(entry.getValue().toString()));
            }
        } else
        {
            log.info("No configuration data present");
        }

        //  Get the entities that are known in the server
        initMapFromOres();

        //  Flush it back out to disk because there may be
        //  new entries
        log.info("Saving rewards configuration");
        save(config);
    }

    public void save(FileConfiguration config)
    {
        ConfigurationSection section = config.createSection("rewards", rewardList);

        if (section == null)
        {
            plugin.getLogger().info("Unable to save reward data into config file!");
        }
    }

    public void setReward(String reward, double amount)
    {
        rewardList.put(reward, amount);
    }

    public double getReward(String reward)
    {
        Double r = rewardList.get(reward);
        return r == null ? -1.0 : r;
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
}
