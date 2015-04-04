package de.lurch.funcannon.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.lurch.funcannon.command.CommandFunCannon;
import de.lurch.funcannon.util.FunCannonGUI;
import de.lurch.funcannon.util.FunCannonItem;
import de.lurch.funcannon.util.FunCannonMode;

/**
 * @author ILurch
 */
public class FunCannonPlugin extends JavaPlugin {

	public static Messages MSG;
	public static FunCannonPlugin INSTANCE;

	private List<String> enabledWorlds = new ArrayList<String>();
	private List<FunCannonMode> modes = new ArrayList<FunCannonMode>();
	private FunCannonGUI gui;
	private FunCannonMode defaultmode;
	private Map<Player, FunCannonMode> playerModes = new HashMap<Player, FunCannonMode>();

	@Override
	public void onDisable() {
		enabledWorlds.clear();
		modes.clear();
		MSG = null;
		INSTANCE = null;
	}

	@Override
	public void onEnable() {
		reload();
		getCommand("funcannon").setExecutor(new CommandFunCannon(this));
		Bukkit.getPluginManager().registerEvents(new ShootListeners(this), this);
	}

	public void reload() {
		INSTANCE = this;
		MSG = new Messages(this);

		this.saveDefaultMode();

		this.saveDefaultConfig();
		this.reloadConfig();
		FunCannonItem.loadItem(this);
		loadWorlds();
		loadModes();
		gui = new FunCannonGUI(modes);
		
	}

	private void saveDefaultMode() {
		File path = new File("plugins/FunCannon/modes");
		path.mkdir();

		File file = new File("plugins/FunCannon/modes", "default.yml");
		if (!file.exists()) {
			try {
				Files.copy(getResource("default.yml"), file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadModes() {
		modes.clear();
		File[] files = new File(getDataFolder() + "/modes").listFiles();
		if (files == null)
			return;
		for (File file : files) {
			if (modes.size() >= 54) {
				getLogger().info("The maximum of 54 modes is reached!");
				return;
			}
			try {
				FunCannonMode mode = new FunCannonMode(file);

				if (mode.getName().equals("default"))
					defaultmode = mode;
				if (!modes.contains(mode)) {
					modes.add(mode);
				}
			} catch (Exception ex) {
				getLogger().warning("The mode \"" + file.getName() + "\" could not be loaded!");
			}
		}
	}

	private void loadWorlds() {
		enabledWorlds = getConfig().getStringList("Settings.Worlds");
	}

	public List<FunCannonMode> getModes() {
		return modes;
	}

	public List<String> getEnabledWorlds() {
		return enabledWorlds;
	}

	public void sendMessage(Player player, String message) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Settings.Prefix")) + message);
	}

	public FunCannonGUI getGUI() {
		return gui;
	}

	public Map<Player, FunCannonMode> getPlayerModes() {
		return playerModes;
	}

	public FunCannonMode getDefaultMode() {
		return defaultmode;
	}
}
