package de.lurch.funcannon.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;

import de.lurch.funcannon.plugin.FunCannonPlugin;

public abstract class Shoot {

	public static List<UUID> UUIDS = new ArrayList<UUID>();
	
	public static void byPlayer(Player player, FunCannonProjectile projectile) {
		if (player == null)
			return;
		Entity ent = null;
		if (projectile == FunCannonProjectile.ARROW) {
			ent = player.launchProjectile(Arrow.class);
		} else if (projectile == FunCannonProjectile.EGG) {
			ent = player.launchProjectile(Egg.class);
		} else if (projectile == FunCannonProjectile.ENDER_PEARL) {
			ent = player.launchProjectile(EnderPearl.class);
		} else {
			ent = player.launchProjectile(Snowball.class);
		}
		
		UUIDS.add(ent.getUniqueId());
		int usedTimes = 0;
		try{
			usedTimes = FunCannonPlugin.INSTANCE.getConfig().getInt("Players." + player.getUniqueId().toString() + ".UsedTimes");
		}catch(NullPointerException ex){
			FunCannonPlugin.INSTANCE.getConfig().addDefault("Players." + player.getUniqueId().toString() + ".UsedTimes", 0);
		}
		FunCannonPlugin.INSTANCE.getConfig().set("Players." + player.getUniqueId().toString() + ".UsedTimes", usedTimes +1);
	}
}