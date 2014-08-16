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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor
{
    private final WorkRewards plugin;

    public ReloadCommand(WorkRewards plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player player = null;

        if(sender instanceof Player)
        {
            player = (Player)sender;
        }

        if(sender instanceof ConsoleCommandSender || (player != null && player.hasPermission("workrewards.reload")))
        {
            plugin.onDisable();
            plugin.reloadConfig();
            plugin.onEnable();
            sender.sendMessage(ChatColor.GOLD + "WorkRewards reloaded.");
            return true;
        }

        return false;
    }
}
