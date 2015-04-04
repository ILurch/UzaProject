package de.lurch.funcannon.util;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.lurch.funcannon.plugin.FunCannonPlugin;

public class FunCannonItem implements Listener {

	private static ItemStack is;
	private static FunCannonPlugin plugin;

	@SuppressWarnings("deprecation")
	public static void loadItem(FunCannonPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(new FunCannonItem(), plugin);
		FunCannonItem.plugin = plugin;
		is = new ItemStack(Material.getMaterial(plugin.getConfig().getInt("Item.ID")));
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Item.Name")));
		meta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Item.Lore.Line1")), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Item.Lore.Line2"))));
		is.setItemMeta(meta);
	}

	public static void set(Player player) {
		player.getInventory().setItem(plugin.getConfig().getInt("Item.Slot"), get());
		player.updateInventory();
	}

	public static ItemStack get() {
		return is;
	}

	public static void clear(Player player) {

		player.getInventory().remove(get());
		player.updateInventory();
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		ItemStack drop = event.getItemDrop().getItemStack();
		if (drop.equals(get())) {
			event.getItemDrop().remove();
			set(event.getPlayer());
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		try {
			if (event.getCurrentItem().equals(get())) {
				event.setCancelled(true);
				((Player) event.getWhoClicked()).updateInventory();
			}
		} catch (NullPointerException ex) {}
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		if (!plugin.getEnabledWorlds().contains(event.getPlayer().getWorld())) {
			FunCannonItem.clear(event.getPlayer());
		} else {
			FunCannonItem.set(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		if (!FunCannonPlugin.INSTANCE.getEnabledWorlds().contains(event.getPlayer().getWorld())) {
			FunCannonItem.clear(event.getPlayer());
		} else {
			FunCannonItem.set(event.getPlayer());
		}
		if (event.getPlayer().hasPermission("funcannon.use")) {
			plugin.getConfig().addDefault("Players." + event.getPlayer().getName() + ".UsedTimes", Integer.valueOf(0));
			plugin.getConfig().addDefault("Players." + event.getPlayer().getName() + ".HittedPlayers", Integer.valueOf(0));
			plugin.getConfig().options().copyDefaults(true);

			plugin.saveConfig();
		}
		if (plugin.getPlayerModes().get(event.getPlayer()) == null)
			plugin.getPlayerModes().put(event.getPlayer(), plugin.getDefaultMode());

	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.getDrops().remove(FunCannonItem.get());
	}
}
