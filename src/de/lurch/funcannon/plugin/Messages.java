package de.lurch.funcannon.plugin;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.lurch.funcannon.util.FunCannonItem;

public class Messages {
	private FunCannonPlugin plugin;
	private File file;
	private FileConfiguration cfg = new YamlConfiguration();

	public Messages(FunCannonPlugin main) {
		this.plugin = main;
		plugin.saveResource("messages.yml", false);
		this.file = new File(plugin.getDataFolder(), "messages.yml");
		reloadConfig();
	}

	public void reloadConfig() {
		try {
			this.cfg.load(this.file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(Player player, String path) {
		String message = ChatColor.translateAlternateColorCodes('&', cfg.getString(path));

		message = message.replace("%hittedPlayers%", plugin.getConfig().getInt("Players." + player.getName() + ".HittedPlayers") + "");
		message = message.replace("%usedTimes%", plugin.getConfig().getInt("Players." + player.getName() + ".UsedTimes") + "");
		message = message.replace("%itemname%", FunCannonItem.get().getItemMeta().getDisplayName());

		plugin.sendMessage(player, message);
	}

	public void sendMessage(Player player, String path, Player target) {
		String message = ChatColor.translateAlternateColorCodes('&', cfg.getString(path)).replace("%playername%", target.getDisplayName());

		message = message.replace("%hittedPlayers%", plugin.getConfig().getInt("Players." + player.getName() + ".HittedPlayers") + "");
		message = message.replace("%usedTimes%", plugin.getConfig().getInt("Players." + player.getName() + ".UsedTimes") + "");
		message = message.replace("%itemname%", FunCannonItem.get().getItemMeta().getDisplayName());

		plugin.sendMessage(player, message);
	}

	public void sendMessage(Player player, String path, String mode) {
		String message = ChatColor.translateAlternateColorCodes('&', cfg.getString(path)).replace("%mode%", ChatColor.translateAlternateColorCodes('&', mode));

		plugin.sendMessage(player, message);
	}
}