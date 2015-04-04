package de.lurch.funcannon.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.lurch.funcannon.plugin.FunCannonPlugin;
import de.lurch.funcannon.util.FunCannonItem;
import de.lurch.funcannon.util.FunCannonMode;

public class CommandFunCannon implements CommandExecutor {
	private FunCannonPlugin plugin;

	public CommandFunCannon(FunCannonPlugin plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("You've to be a player to perform this command!");
			return true;
		}
		Player p = (Player) sender;

		if (args.length == 0) {
			plugin.sendMessage(p, "§aThis Plugin was made by §cILurch");
			return true;
		} else {
			if (args[0].equalsIgnoreCase("help")) {
				plugin.sendMessage(p, "§a/fc §8<§amodes§8|§atoggleworld§8|§areload§8|§agui§8>");
				plugin.sendMessage(p, "§a/fc stats §8<§aPlayer§8>");
				plugin.sendMessage(p, "§a/fc mode §8<§amodename§8>");
			} else if (args[0].equalsIgnoreCase("stats")) {
				if (p.hasPermission("funcannon.stats") && args.length == 1) {
					FunCannonPlugin.MSG.sendMessage(p, "YourStats");
				} else if (p.hasPermission("funcannon.stats.others") && args.length > 1) {
					Player target = Bukkit.getPlayer(args[1]);

					if (target == null) {
						FunCannonPlugin.MSG.sendMessage(p, "PlayerIsNotOnline");
						return true;
					}
					try {
						FunCannonPlugin.MSG.sendMessage(p, "OthersStats");
					} catch (Exception ex) {
						FunCannonPlugin.MSG.sendMessage(p, "PlayerNotFound");
					}
				} else {
					FunCannonPlugin.MSG.sendMessage(p, "Permissions");
				}

			} else if (args[0].equalsIgnoreCase("toggleworld")) {
				if (p.hasPermission("funcannon.toggleworld")) {
					if (plugin.getEnabledWorlds().contains(p.getWorld().getName())) {
						this.plugin.getEnabledWorlds().remove(p.getWorld().getName());
						FunCannonPlugin.MSG.sendMessage(p, "DisabledWorld");
					} else {
						List<String> worlds = plugin.getEnabledWorlds();
						worlds.add(p.getWorld().getName());
						FunCannonPlugin.MSG.sendMessage(p, "EnabledWorld");
						this.plugin.getConfig().set("Settings.Worlds", worlds);
						this.plugin.saveConfig();
					}
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (this.plugin.getEnabledWorlds().contains(player.getWorld().getName()))
							FunCannonItem.set(player);
						else
							FunCannonItem.clear(player);
					}
				} else {
					FunCannonPlugin.MSG.sendMessage(p, "Permissions");
				}
			} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
				if (p.hasPermission("funcannon.reload")) {
					plugin.sendMessage(p, "§cReloaded!");
					this.plugin.onDisable();
					this.plugin.reload();
				} else
					FunCannonPlugin.MSG.sendMessage(p, "Permissions");
			} else if (args[0].equalsIgnoreCase("gui")) {
				if (p.hasPermission("funcannon.gui"))
					plugin.getGUI().open(p);
				else
					FunCannonPlugin.MSG.sendMessage(p, "Permissions");
			} else if (args[0].equalsIgnoreCase("modes")) {
				if (p.hasPermission("funcannon.modes")) {
					String out = "";
					for (FunCannonMode mode : plugin.getModes()) {
						if (mode.equals(plugin.getModes().get(0))) {
							out += mode.getColoredName();
						} else
							out += "§8, " + mode.getColoredName();
					}
					p.sendMessage(out);
				}
			} else if (args[0].equalsIgnoreCase("mode")) {
				if (args.length == 1) {
					Bukkit.dispatchCommand(p, "fc help");
				} else {
					FunCannonMode mode = null;
					for (FunCannonMode modes : plugin.getModes()) {
						if (modes.getName().equals(args[1]))
							mode = modes;
					}
					if (mode == null) {
						FunCannonPlugin.MSG.sendMessage(p, "ModeNotFound");
					} else {
						if (p.hasPermission("funcannon.mode." + mode.getName())) {
							FunCannonPlugin.MSG.sendMessage(p, "ModeSelected", mode.getColoredName());
							plugin.getPlayerModes().put(p, mode);
						} else {
							FunCannonPlugin.MSG.sendMessage(p, "Permissions");
						}
					}
				}
			} else
				FunCannonPlugin.MSG.sendMessage(p, "UnknownCommand");
		}
		return true;
	}
}