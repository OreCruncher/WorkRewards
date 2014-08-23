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

import net.blockartistry.plugins.workrewards.commands.*;
import net.blockartistry.plugins.workrewards.listeners.RewardEventListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class WorkRewards extends JavaPlugin
{
    public Economy economy;
    public Rewards rewards;
    public RewardEventListener listener;

    @Override
    public void onEnable()
    {
        Logger log = getLogger();

        saveDefaultConfig();

        //  Load up rewards
        rewards = new Rewards(this);
        rewards.load(getConfig());
        rewards.save(getConfig());
        saveConfig();

        //  Configure the commands
        getCommand("wrreload").setExecutor(new ReloadCommand(this));
        getCommand("wrconfig").setExecutor(new ConfigCommand(this));
        getCommand("wrworld").setExecutor(new WorldCommand(this));
        getCommand("wrdumpblocks").setExecutor(new DumpBlocksCommand(this));
        getCommand("wrlist").setExecutor(new ListCommand(this));

        //  Obtain economy reference
        if (!setupEconomy())
        {
            log.warning("Vault economy plugin not found!  Plugin functionality will be disabled!");
        }
        else
        {
            log.info("Economy plugin: " + economy.getName());
            log.info("Fractional digits: " + economy.fractionalDigits());

            //  Register event handlers
            listener = new RewardEventListener(this, getConfig().getDouble("sendPlayerRewardMessageThreshold", 0.0));
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    @Override
    public void onDisable()
    {
        HandlerList.unregisterAll(this);

        //  Unwind the tendrils
        economy = null;
        getCommand("wrreload").setExecutor(null);
        getCommand("wrconfig").setExecutor(null);
        getCommand("wrworld").setExecutor(null);
        getCommand("wrlist").setExecutor(null);
        getCommand("wrdumpblocks").setExecutor(null);
    }

    private boolean setupEconomy()
    {
        if (getServer().getServicesManager().isProvidedFor(net.milkbowl.vault.economy.Economy.class))
        {
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null)
            {
                economy = economyProvider.getProvider();
            }
        }

        return (economy != null);
    }
}
