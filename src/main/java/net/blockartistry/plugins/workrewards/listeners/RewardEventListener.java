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

public class RewardEventListener implements Listener
{
    protected WorkRewards plugin;
    protected double sendThreshold;
    protected String moneyName;
    protected String chatFormat = ChatColor.BLUE + "You have received %s%s for mining the block!";

    public RewardEventListener(WorkRewards plugin, double sendThreshold)
    {
        this.plugin = plugin;
        this.sendThreshold = sendThreshold;

        moneyName = this.plugin.economy.currencyNameSingular();
        if(!moneyName.isEmpty())
        {
            moneyName = " " + moneyName;
        }
    }

    protected boolean hasSilkTouch(ItemStack tool)
    {
        return tool != null && tool.containsEnchantment(Enchantment.SILK_TOUCH);
    }

    protected boolean isModdedBlock(String name)
    {
        //  Name is non-null, > 1 length, and start with a cap letter.  Thus we
        //  don't check for that.
        return name.charAt(0) == 'X' && Character.isDigit(name.charAt(1));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();

        //  Has to be a player
        if (player != null)
        {
            Block block = event.getBlock();
            String name = block.getType().toString();

            if (isModdedBlock(name))
            {
                //  Need the subtype appended to the name
                name += ":" + block.getState().getData().toItemStack().getDurability();
            }

            double reward = plugin.rewards.getReward(name, player.getWorld().getName());

            //  Payout only if there is a reward, the player is not using silk touch, and the player
            //  has permission to receive the reward.
            if (reward > 0.0 && !hasSilkTouch(player.getItemInHand()) && player.hasPermission("workrewards.receive"))
            {
                //  Time for a pay day!
                plugin.economy.depositPlayer(player.getName(), reward);
                if(reward > sendThreshold)
                {
                    player.sendMessage(String.format(chatFormat, plugin.economy.format(reward), moneyName));
                }
            }
        }
    }
}
