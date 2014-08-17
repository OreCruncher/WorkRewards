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

package net.blockartistry.plugins.workrewards.listeners;

import net.blockartistry.plugins.workrewards.WorkRewards;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class BlockBreakEventListener implements Listener
{
    protected WorkRewards plugin;
    protected double sendThreshold;
    protected String moneyName;
    protected DecimalFormat fmt = new DecimalFormat("0.##");

    public BlockBreakEventListener(WorkRewards plugin, double sendThreshold)
    {
        this.plugin = plugin;
        this.sendThreshold = sendThreshold;

        moneyName = this.plugin.economy.currencyNameSingular();
        if(!moneyName.isEmpty())
        {
            moneyName = " " + moneyName;
        }
    }

    public boolean hasSilkTouch(ItemStack tool)
    {
        return tool != null && tool.containsEnchantment(Enchantment.SILK_TOUCH);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();

        //  Has to be a player, must have permission, and must not be using a tool with silk touch
        if (player != null && player.hasPermission("workrewards.receive") && !hasSilkTouch(player.getItemInHand()))
        {
            Block block = event.getBlock();
            String name = block.getType().toString();

            if (name.length() > 1 && name.charAt(0) == 'X' && Character.isDigit(name.charAt(1)))
            {
                ItemStack stack = block.getState().getData().toItemStack();
                name = name + ":" + stack.getDurability();
            }

            double reward = WorkRewards.rewards.getReward(name);

            if (reward > 0.0)
            {
                //  Time for a pay day!
                plugin.economy.depositPlayer(player.getName(), reward);
                if(reward > sendThreshold)
                {
                    player.sendMessage(ChatColor.BLUE + "You have received " + fmt.format(reward) + moneyName + " for mining the block!");
                }
            }
        }
    }
}
