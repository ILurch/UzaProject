package de.lurch.funcannon.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.lurch.funcannon.particle.ParticleEffect;
import de.lurch.funcannon.util.FunCannonItem;
import de.lurch.funcannon.util.FunCannonMode;
import de.lurch.funcannon.util.Shoot;
import de.lurch.funcannon.util.attributes.ParticleAttributes;

public class ShootListeners implements Listener {

	private HashMap<String, Long> cooldown = new HashMap<>();
	private FunCannonPlugin plugin;
	private HashMap<String, List<String>> hided = new HashMap<>();

	public ShootListeners(FunCannonPlugin plugin) {
		this.plugin = plugin;
		
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInteract(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (p.getItemInHand().isSimilar(FunCannonItem.get())) {
				event.setCancelled(true);
				if (!p.hasPermission("funcannon.use")) {
					FunCannonPlugin.MSG.sendMessage(p, "Permissions");
					return;
				}
				if (cooldown.containsKey(p.getName())) {
					long secondsLeft = ((Long) cooldown.get(p.getName())) / 1000L + plugin.getConfig().getInt("Settings.Cooldown") - System.currentTimeMillis() / 1000L;
					if (secondsLeft > 0L)
						FunCannonPlugin.MSG.sendMessage(p, "WaitingTime");
					else
						cooldown.remove(p.getName());
				} else {
					FunCannonMode mode = plugin.getPlayerModes().get(p);
					if (mode == null)
						mode = plugin.getDefaultMode();
					Shoot.byPlayer(p, mode.getProjectile());


					for (Sound sound : mode.getInteractSounds().keySet()) {
						p.getWorld().playSound(p.getLocation(), sound, mode.getInteractSounds().get(sound).getPitch(), mode.getInteractSounds().get(sound).getVolume());
					}
					if (!hided.containsKey(event.getPlayer().getName()))
						hided.put(event.getPlayer().getName(), new ArrayList<String>());
					if (!p.hasPermission("funcannon.cooldown.bypass"))
						cooldown.put(p.getName(), System.currentTimeMillis());
					else
						cooldown.remove(p.getName());
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onProjectileHit(final ProjectileHitEvent event) {
		if (event.getEntity() instanceof Projectile) {
			final Projectile projectile = (Projectile) event.getEntity();
			if (Shoot.UUIDS.contains(projectile.getUniqueId())) {
				Player player = (Player) projectile.getShooter();
				FunCannonMode mode = plugin.getPlayerModes().get(player);
				if (mode == null)
					mode = plugin.getDefaultMode();

				for (Sound sound : mode.getHitSounds().keySet()) {
					player.getWorld().playSound(projectile.getLocation(), sound, mode.getHitSounds().get(sound).getPitch(), mode.getHitSounds().get(sound).getVolume());
				}

				for (ParticleEffect effect : mode.getParticleEffects().keySet()) {
					ParticleAttributes att = mode.getParticleEffects().get(effect);
					effect.display(att.getOffsetX(), att.getOffsetY(), att.getOffsetZ(), att.getSpeed(), att.getAmount(), projectile.getLocation(), 100);
				}

				removeProjectile(projectile);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getCause() == DamageCause.PROJECTILE) {
			if (event.getDamager() instanceof Projectile && event.getEntity() instanceof Player) {

				event.setCancelled(true);
				event.setDamage(0.0);

				final Projectile projectile = (Projectile) event.getDamager();
				final Player target = (Player) event.getEntity();
				final Player shooter = (Player) projectile.getShooter();
				FunCannonMode mode = plugin.getPlayerModes().get(shooter);

				if (!Shoot.UUIDS.contains(projectile.getUniqueId()))
					return;
				if (hided.get(shooter.getName()).contains(target.getName()))
					return;
				
				int hittedPlayers = 0;
				try{
					hittedPlayers = FunCannonPlugin.INSTANCE.getConfig().getInt("Players." + shooter.getUniqueId().toString() + ".HittedPlayers");
				}catch(NullPointerException ex){
					FunCannonPlugin.INSTANCE.getConfig().addDefault("Players." + shooter.getUniqueId().toString() + ".HittedPlayers", 0);
				}
				FunCannonPlugin.INSTANCE.getConfig().set("Players." + shooter.getUniqueId().toString() + ".HittedPlayers", hittedPlayers +1);
				
				FunCannonPlugin.MSG.sendMessage(shooter, "Shooted", target);
				if (mode.isPlayersInvisibleOnHit()) {
					FunCannonPlugin.MSG.sendMessage(target, "GotShooted", target);
					hided.get(shooter.getName()).add(target.getName());
					shooter.hidePlayer(target);
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							hided.get(shooter.getName()).remove(target.getName());
							shooter.showPlayer(target);
						}
					}, 20 * 5);
				}
			}
		}
	}

	public void removeProjectile(final Projectile projectile) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				Shoot.UUIDS.remove(projectile.getUniqueId());
			}
		}, 2);
	}
}
