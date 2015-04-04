package de.lurch.funcannon.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.lurch.funcannon.particle.ParticleEffect;
import de.lurch.funcannon.util.attributes.ParticleAttributes;
import de.lurch.funcannon.util.attributes.SoundAttributes;

public class FunCannonMode {

	private File file;
	private FileConfiguration config = new YamlConfiguration();

	public FunCannonMode(File file) {
		this.file = file;
		try {
			config.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return file.getName().replace(".yml", "");
	}

	private Map<Sound, SoundAttributes> loadSounds(String path) {
		Map<Sound, SoundAttributes> sounds = new HashMap<Sound, SoundAttributes>();
		for (String string : config.getStringList(path)) {
			String[] args = string.split(" ");
			sounds.put(Sound.valueOf(args[0]), new SoundAttributes(Float.parseFloat(args[1]), Float.parseFloat(args[2])));
		}
		return sounds;
	}

	public Map<Sound, SoundAttributes> getInteractSounds() {
		return loadSounds("InteractSounds");
	}

	public Map<Sound, SoundAttributes> getHitSounds() {
		return loadSounds("HitSounds");
	}

	public String getColoredName() {
		return ChatColor.translateAlternateColorCodes('&', config.getString("Color")) + getName();
	}

	public Material getMatInGUI() {
		return Material.valueOf(config.getString("MatInGUI"));
	}

	public Map<ParticleEffect, ParticleAttributes> getParticleEffects() {
		Map<ParticleEffect, ParticleAttributes> particles = new HashMap<ParticleEffect, ParticleAttributes>();

		for (String string : config.getStringList("ParticleEffects")) {
			String[] args = string.split(" ");
			particles.put(ParticleEffect.valueOf(args[0]), new ParticleAttributes(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]), Float.parseFloat(args[4]), Integer.parseInt(args[5])));
		}
		return particles;
	}

	public boolean isPlayersInvisibleOnHit() {
		return config.getBoolean("PlayersInvisibleOnHit");
	}

	public FunCannonProjectile getProjectile() {
		return FunCannonProjectile.valueOf(config.getString("FunCannonProjectile"));
	}
}
