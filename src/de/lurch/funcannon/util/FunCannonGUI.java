package de.lurch.funcannon.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.lurch.funcannon.plugin.FunCannonPlugin;

public class FunCannonGUI implements Listener {

	private Inventory inv = Bukkit.createInventory(null, 54, "§cSelect a mode!");
	private Map<ItemStack, FunCannonMode> itemList = new HashMap<ItemStack, FunCannonMode>();

	public FunCannonGUI(List<FunCannonMode> modes) {
		load(modes);
		Bukkit.getPluginManager().registerEvents(this, FunCannonPlugin.INSTANCE);
	}

	public void load(List<FunCannonMode> modes) {
		itemList.clear();
		inv.clear();
		for (FunCannonMode mode : modes) {
			itemList.put(getItem(mode), mode);
		}
		for (ItemStack is : itemList.keySet()) {
			inv.addItem(is);
		}
	}

	public static ItemStack getItem(FunCannonMode mode) {
		ItemStack is = new ItemStack(mode.getMatInGUI());
		try {
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(mode.getColoredName());
			is.setItemMeta(meta);
		} catch (NullPointerException ex) {
		}
		return is;
	}

	public void open(Player player) {
		player.openInventory(inv);
	}

	public Set<ItemStack> getItemList() {
		return itemList.keySet();
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (itemList.keySet().contains(event.getCurrentItem())) {
			event.setCancelled(true);
			FunCannonMode mode = itemList.get(event.getCurrentItem());

			if (player.hasPermission("funcannon.mode." + mode.getName())) {
				FunCannonPlugin.MSG.sendMessage(player, "ModeSelected", mode.getColoredName());
				FunCannonPlugin.INSTANCE.getPlayerModes().put(player, mode);
			} else {
				FunCannonPlugin.MSG.sendMessage(player, "Permissions");
			}
		}
	}
}