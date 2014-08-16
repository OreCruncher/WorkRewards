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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakEventListener implements Listener
{
    protected WorkRewards plugin;

    public BlockBreakEventListener(WorkRewards plugin)
    {
        this.plugin = plugin;
    }

    public boolean hasSilkTouch(ItemStack tool)
    {
        return tool != null && tool.containsEnchantment(Enchantment.SILK_TOUCH);
    }

    public boolean isSilkTouchSensitive(Block block)
    {
        Material material = block.getType();
        return material == Material.COAL_ORE ||
                material == Material.DIAMOND_ORE ||
                material == Material.EMERALD_ORE ||
                material == Material.REDSTONE_ORE ||
                material == Material.LAPIS_ORE ||
                material == Material.GLOWING_REDSTONE_ORE ||
                material == Material.QUARTZ_ORE ||
                material == Material.GLOWSTONE;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        if (player != null && player.hasPermission("workrewards.receive"))
        {
            Block block = event.getBlock();
            Material material = block.getType();

            String name = material.toString();

            if (name.length() > 1 && name.charAt(0) == 'X' && Character.isDigit(name.charAt(1)))
            {
                ItemStack stack = block.getState().getData().toItemStack();
                name = name + ":" + stack.getDurability();
            }

            double reward = WorkRewards.rewards.getReward(name);

            if (reward > 0.0)
            {
                //  If the block is sensitive to silk touch, and the player is using
                //  a silk touch tool, then return.  Don't want them cashing in
                //  a bunch of times.
                if (isSilkTouchSensitive(block) && hasSilkTouch(player.getItemInHand()))
                {
                    return;
                }

                //  Time for a pay day!
                WorkRewards.economy.depositPlayer(player.getName(), reward);
                player.sendMessage(ChatColor.BLUE + "You have received " + reward + " " + WorkRewards.economy.currencyNamePlural() + "for mining the block!" );
            }
        }
    }
}
