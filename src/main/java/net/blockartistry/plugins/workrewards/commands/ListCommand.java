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

package net.blockartistry.plugins.workrewards.commands;

import net.blockartistry.plugins.workrewards.WorkRewards;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Map;

public class ListCommand implements CommandExecutor
{
    private final WorkRewards plugin;

    protected DecimalFormat fmt = new DecimalFormat("0.##");

    public ListCommand(WorkRewards plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player player = null;

        if (sender instanceof Player)
        {
            player = (Player) sender;
        }

        if (sender instanceof ConsoleCommandSender || (player != null && player.hasPermission("workrewards.list")))
        {
            if (args.length == 0 || args[0].equalsIgnoreCase("rewards"))
            {
                Map<String, Double> list = plugin.rewards.getRewardList();
                sender.sendMessage(ChatColor.GOLD + "Possible work rewards:");
                if (list == null || list.size() == 0)
                {
                    sender.sendMessage(ChatColor.RED + "No rewards defined");
                }
                else
                {
                    for (Map.Entry<String, Double> entry : list.entrySet())
                    {
                        sender.sendMessage(ChatColor.YELLOW + entry.getKey() + ChatColor.WHITE + ": " + ChatColor.GREEN + fmt.format(entry.getValue()));
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("modifiers"))
            {
                Map<String, Double> list = plugin.rewards.getModifierList();
                sender.sendMessage(ChatColor.GOLD + "Per World Modifiers:");
                if (list == null || list.size() == 0)
                {
                    sender.sendMessage(ChatColor.RED + "No modifiers defined");
                }
                else
                {
                    for (Map.Entry<String, Double> entry : list.entrySet())
                    {
                        sender.sendMessage(ChatColor.YELLOW + entry.getKey() + ChatColor.WHITE + ": " + ChatColor.GREEN + fmt.format(entry.getValue()));
                    }
                }
            }
            else
            {
                return false;
            }
            return true;
        }

        return false;
    }
}
