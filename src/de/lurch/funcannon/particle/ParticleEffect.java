package de.lurch.funcannon.particle;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.lurch.funcannon.particle.ReflectionUtils.PackageType;

public enum ParticleEffect {

	EXPLOSION_NORMAL("explode", 0, -1), EXPLOSION_LARGE("largeexplode", 1, -1), EXPLOSION_HUGE("hugeexplosion", 2, -1), FIREWORKS_SPARK("fireworksSpark", 3, -1), WATER_BUBBLE("bubble", 4, -1, false, true), WATER_SPLASH("splash", 5, -1), WATER_WAKE("wake", 6, 7), SUSPENDED("suspended", 7, -1, false, true), SUSPENDED_DEPTH("depthSuspend", 8, -1), CRIT("crit", 9, -1), CRIT_MAGIC("magicCrit", 10, -1), SMOKE_NORMAL("smoke", 11, -1), SMOKE_LARGE("largesmoke", 12, -1), SPELL("spell", 13, -1), SPELL_INSTANT("instantSpell", 14, -1), SPELL_MOB("mobSpell", 15, -1), SPELL_MOB_AMBIENT("mobSpellAmbient", 16, -1), SPELL_WITCH("witchMagic", 17, -1), DRIP_WATER("dripWater", 18, -1), DRIP_LAVA("dripLava", 19, -1), VILLAGER_ANGRY("angryVillager", 20, -1), VILLAGER_HAPPY("happyVillager", 21, -1), TOWN_AURA("townaura", 22, -1), NOTE("note", 23, -1), PORTAL("portal", 24, -1), ENCHANTMENT_TABLE("enchantmenttable", 25, -1), FLAME("flame", 26, -1), LAVA("lava", 27, -1), FOOTSTEP("footstep", 28, -1), CLOUD("cloud", 29, -1), REDSTONE("reddust", 30, -1), SNOWBALL("snowballpoof", 31, -1), SNOW_SHOVEL("snowshovel", 32, -1), SLIME("slime", 33, -1), HEART("heart", 34, -1), BARRIER("barrier", 35, 8), ITEM_CRACK("iconcrack", 36, -1, true), BLOCK_CRACK("blockcrack", 37, -1, true), BLOCK_DUST("blockdust", 38, 7, true), WATER_DROP("droplet", 39, 8), ITEM_TAKE("take", 40, 8), MOB_APPEARANCE("mobappearance", 41, 8);

	private static final Map<String, ParticleEffect> NAME_MAP = new HashMap<String, ParticleEffect>();
	private static final Map<Integer, ParticleEffect> ID_MAP = new HashMap<Integer, ParticleEffect>();
	private final String name;
	private final int id;
	private final int requiredVersion;
	private final boolean requiresData;
	private final boolean requiresWater;
	// Initialize map for quick name and id lookup
	static {
		for (ParticleEffect effect : values()) {
			NAME_MAP.put(effect.name, effect);
			ID_MAP.put(effect.id, effect);
		}
	}

	private ParticleEffect(String name, int id, int requiredVersion, boolean requiresData, boolean requiresWater) {
		this.name = name;
		this.id = id;
		this.requiredVersion = requiredVersion;
		this.requiresData = requiresData;
		this.requiresWater = requiresWater;
	}

	private ParticleEffect(String name, int id, int requiredVersion, boolean requiresData) {
		this(name, id, requiredVersion, requiresData, false);
	}

	private ParticleEffect(String name, int id, int requiredVersion) {
		this(name, id, requiredVersion, false);
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public int getRequiredVersion() {
		return requiredVersion;
	}

	public boolean getRequiresData() {
		return requiresData;
	}

	public boolean getRequiresWater() {
		return requiresWater;
	}

	public boolean isSupported() {
		if (requiredVersion == -1) {
			return true;
		}
		return ParticlePacket.getVersion() >= requiredVersion;
	}

	public static ParticleEffect fromName(String name) {
		for (Entry<String, ParticleEffect> entry : NAME_MAP.entrySet()) {
			if (!entry.getKey().equalsIgnoreCase(name)) {
				continue;
			}
			return entry.getValue();
		}
		return null;
	}

	public static ParticleEffect fromId(int id) {
		for (Entry<Integer, ParticleEffect> entry : ID_MAP.entrySet()) {
			if (entry.getKey() != id) {
				continue;
			}
			return entry.getValue();
		}
		return null;
	}

	private static boolean isWater(Location location) {
		Material material = location.getBlock().getType();
		return material == Material.WATER || material == Material.STATIONARY_WATER;
	}

	private static boolean isLongDistance(Location location, List<Player> players) {
		for (Player player : players) {
			if (player.getLocation().distanceSquared(location) < 65536) {
				continue;
			}
			return true;
		}
		return false;
	}

	private static boolean isDataCorrect(ParticleEffect effect, ParticleData data) {
		return ((effect == BLOCK_CRACK || effect == BLOCK_DUST) && data instanceof BlockData) || effect == ITEM_CRACK && data instanceof ItemData;
	}

	public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
		if (!isSupported()) {
			throw new ParticleVersionException("This particle effect is not supported by your server version");
		}
		if (requiresData) {
			throw new ParticleDataException("This particle effect requires additional data");
		}
		if (requiresWater && !isWater(center)) {
			throw new IllegalArgumentException("There is no water at the center location");
		}
		new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, range > 256, null).sendTo(center, range);
	}

	public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
		if (!isSupported()) {
			throw new ParticleVersionException("This particle effect is not supported by your server version");
		}
		if (requiresData) {
			throw new ParticleDataException("This particle effect requires additional data");
		}
		if (requiresWater && !isWater(center)) {
			throw new IllegalArgumentException("There is no water at the center location");
		}
		new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, isLongDistance(center, players), null).sendTo(center, players);
	}

	public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
		display(offsetX, offsetY, offsetZ, speed, amount, center, Arrays.asList(players));
	}

	public void display(Vector direction, float speed, Location center, double range) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
		if (!isSupported()) {
			throw new ParticleVersionException("This particle effect is not supported by your server version");
		}
		if (requiresData) {
			throw new ParticleDataException("This particle effect requires additional data");
		}
		if (requiresWater && !isWater(center)) {
			throw new IllegalArgumentException("There is no water at the center location");
		}
		new ParticlePacket(this, direction, speed, range > 256, null).sendTo(center, range);
	}

	public void display(Vector direction, float speed, Location center, List<Player> players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
		if (!isSupported()) {
			throw new ParticleVersionException("This particle effect is not supported by your server version");
		}
		if (requiresData) {
			throw new ParticleDataException("This particle effect requires additional data");
		}
		if (requiresWater && !isWater(center)) {
			throw new IllegalArgumentException("There is no water at the center location");
		}
		new ParticlePacket(this, direction, speed, isLongDistance(center, players), null).sendTo(center, players);
	}

	public void display(Vector direction, float speed, Location center, Player... players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
		display(direction, speed, center, Arrays.asList(players));
	}

	public void display(ParticleData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range) throws ParticleVersionException, ParticleDataException {
		if (!isSupported()) {
			throw new ParticleVersionException("This particle effect is not supported by your server version");
		}
		if (!requiresData) {
			throw new ParticleDataException("This particle effect does not require additional data");
		}
		if (!isDataCorrect(this, data)) {
			throw new ParticleDataException("The particle data type is incorrect");
		}
		new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, range > 256, data).sendTo(center, range);
	}

	public void display(ParticleData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players) throws ParticleVersionException, ParticleDataException {
		if (!isSupported()) {
			throw new ParticleVersionException("This particle effect is not supported by your server version");
		}
		if (!requiresData) {
			throw new ParticleDataException("This particle effect does not require additional data");
		}
		if (!isDataCorrect(this, data)) {
			throw new ParticleDataException("The particle data type is incorrect");
		}
		new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, isLongDistance(center, players), data).sendTo(center, players);
	}

	public void display(ParticleData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players) throws ParticleVersionException, ParticleDataException {
		display(data, offsetX, offsetY, offsetZ, speed, amount, center, Arrays.asList(players));
	}

	public void display(ParticleData data, Vector direction, float speed, Location center, double range) throws ParticleVersionException, ParticleDataException {
		if (!isSupported()) {
			throw new ParticleVersionException("This particle effect is not supported by your server version");
		}
		if (!requiresData) {
			throw new ParticleDataException("This particle effect does not require additional data");
		}
		if (!isDataCorrect(this, data)) {
			throw new ParticleDataException("The particle data type is incorrect");
		}
		new ParticlePacket(this, direction, speed, range > 256, data).sendTo(center, range);
	}

	public void display(ParticleData data, Vector direction, float speed, Location center, List<Player> players) throws ParticleVersionException, ParticleDataException {
		if (!isSupported()) {
			throw new ParticleVersionException("This particle effect is not supported by your server version");
		}
		if (!requiresData) {
			throw new ParticleDataException("This particle effect does not require additional data");
		}
		if (!isDataCorrect(this, data)) {
			throw new ParticleDataException("The particle data type is incorrect");
		}
		new ParticlePacket(this, direction, speed, isLongDistance(center, players), data).sendTo(center, players);
	}

	public void display(ParticleData data, Vector direction, float speed, Location center, Player... players) throws ParticleVersionException, ParticleDataException {
		display(data, direction, speed, center, Arrays.asList(players));
	}

	public static abstract class ParticleData {
		private final Material material;
		private final byte data;
		private final int[] packetData;

		@SuppressWarnings("deprecation")
		public ParticleData(Material material, byte data) {
			this.material = material;
			this.data = data;
			this.packetData = new int[] { material.getId(), data };
		}

		public Material getMaterial() {
			return material;
		}

		public byte getData() {
			return data;
		}

		public int[] getPacketData() {
			return packetData;
		}

		public String getPacketDataString() {
			return "_" + packetData[0] + "_" + packetData[1];
		}
	}

	public static final class ItemData extends ParticleData {

		public ItemData(Material material, byte data) {
			super(material, data);
		}
	}

	public static final class BlockData extends ParticleData {

		public BlockData(Material material, byte data) throws IllegalArgumentException {
			super(material, data);
			if (!material.isBlock()) {
				throw new IllegalArgumentException("The material is not a block");
			}
		}
	}

	private static final class ParticleDataException extends RuntimeException {
		private static final long serialVersionUID = 3203085387160737484L;

		public ParticleDataException(String message) {
			super(message);
		}
	}

	private static final class ParticleVersionException extends RuntimeException {
		private static final long serialVersionUID = 3203085387160737484L;

		public ParticleVersionException(String message) {
			super(message);
		}
	}

	public static final class ParticlePacket {
		private static int version;
		private static Class<?> enumParticle;
		private static Constructor<?> packetConstructor;
		private static Method getHandle;
		private static Field playerConnection;
		private static Method sendPacket;
		private static boolean initialized;
		private final ParticleEffect effect;
		private final float offsetX;
		private final float offsetY;
		private final float offsetZ;
		private final float speed;
		private final int amount;
		private final boolean longDistance;
		private final ParticleData data;
		private Object packet;

		public ParticlePacket(ParticleEffect effect, float offsetX, float offsetY, float offsetZ, float speed, int amount, boolean longDistance, ParticleData data) throws IllegalArgumentException {
			initialize();
			if (speed < 0) {
				throw new IllegalArgumentException("The speed is lower than 0");
			}
			if (amount < 1) {
				throw new IllegalArgumentException("The amount is lower than 1");
			}
			this.effect = effect;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.offsetZ = offsetZ;
			this.speed = speed;
			this.amount = amount;
			this.longDistance = longDistance;
			this.data = data;
		}

		public ParticlePacket(ParticleEffect effect, Vector direction, float speed, boolean longDistance, ParticleData data) throws IllegalArgumentException {
			initialize();
			if (speed < 0) {
				throw new IllegalArgumentException("The speed is lower than 0");
			}
			this.effect = effect;
			this.offsetX = (float) direction.getX();
			this.offsetY = (float) direction.getY();
			this.offsetZ = (float) direction.getZ();
			this.speed = speed;
			this.amount = 0;
			this.longDistance = longDistance;
			this.data = data;
		}

		public static void initialize() throws VersionIncompatibleException {
			if (initialized) {
				return;
			}
			try {
				version = Integer.parseInt(Character.toString(PackageType.getServerVersion().charAt(3)));
				if (version > 7) {
					enumParticle = PackageType.MINECRAFT_SERVER.getClass("EnumParticle");
				}
				Class<?> packetClass = PackageType.MINECRAFT_SERVER.getClass(version < 7 ? "Packet63WorldParticles" : "PacketPlayOutWorldParticles");
				packetConstructor = ReflectionUtils.getConstructor(packetClass);
				getHandle = ReflectionUtils.getMethod("CraftPlayer", PackageType.CRAFTBUKKIT_ENTITY, "getHandle");
				playerConnection = ReflectionUtils.getField("EntityPlayer", PackageType.MINECRAFT_SERVER, false, "playerConnection");
				sendPacket = ReflectionUtils.getMethod(playerConnection.getType(), "sendPacket", PackageType.MINECRAFT_SERVER.getClass("Packet"));
			} catch (Exception exception) {
				throw new VersionIncompatibleException("Your current bukkit version seems to be incompatible with this library", exception);
			}
			initialized = true;
		}

		public static int getVersion() {
			return version;
		}

		public static boolean isInitialized() {
			return initialized;
		}

		public void sendTo(Location center, Player player) throws PacketInstantiationException, PacketSendingException {
			if (packet == null) {
				try {
					packet = packetConstructor.newInstance();
					Object id;
					if (version < 8) {
						id = effect.getName() + (data == null ? "" : data.getPacketDataString());
					} else {
						id = enumParticle.getEnumConstants()[effect.getId()];
					}
					ReflectionUtils.setValue(packet, true, "a", id);
					ReflectionUtils.setValue(packet, true, "b", (float) center.getX());
					ReflectionUtils.setValue(packet, true, "c", (float) center.getY());
					ReflectionUtils.setValue(packet, true, "d", (float) center.getZ());
					ReflectionUtils.setValue(packet, true, "e", offsetX);
					ReflectionUtils.setValue(packet, true, "f", offsetY);
					ReflectionUtils.setValue(packet, true, "g", offsetZ);
					ReflectionUtils.setValue(packet, true, "h", speed);
					ReflectionUtils.setValue(packet, true, "i", amount);
					if (version > 7) {
						ReflectionUtils.setValue(packet, true, "j", longDistance);
						ReflectionUtils.setValue(packet, true, "k", data == null ? new int[0] : data.getPacketData());
					}
				} catch (Exception exception) {
					throw new PacketInstantiationException("Packet instantiation failed", exception);
				}
			}
			try {
				sendPacket.invoke(playerConnection.get(getHandle.invoke(player)), packet);
			} catch (Exception exception) {
				throw new PacketSendingException("Failed to send the packet to player '" + player.getName() + "'", exception);
			}
		}

		public void sendTo(Location center, List<Player> players) throws IllegalArgumentException {
			if (players.isEmpty()) {
				throw new IllegalArgumentException("The player list is empty");
			}
			for (Player player : players) {
				sendTo(center, player);
			}
		}

		@SuppressWarnings("deprecation")
		public void sendTo(Location center, double range) throws IllegalArgumentException {
			if (range < 1) {
				throw new IllegalArgumentException("The range is lower than 1");
			}
			String worldName = center.getWorld().getName();
			double squared = range * range;
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!player.getWorld().getName().equals(worldName) || player.getLocation().distanceSquared(center) > squared) {
					continue;
				}
				sendTo(center, player);
			}
		}

		private static final class VersionIncompatibleException extends RuntimeException {
			private static final long serialVersionUID = 3203085387160737484L;

			/**
			 * Construct a new version incompatible exception
			 *
			 * @param message
			 *            Message that will be logged
			 * @param cause
			 *            Cause of the exception
			 */
			public VersionIncompatibleException(String message, Throwable cause) {
				super(message, cause);
			}
		}

		private static final class PacketInstantiationException extends RuntimeException {
			private static final long serialVersionUID = 3203085387160737484L;

			/**
			 * Construct a new packet instantiation exception
			 *
			 * @param message
			 *            Message that will be logged
			 * @param cause
			 *            Cause of the exception
			 */
			public PacketInstantiationException(String message, Throwable cause) {
				super(message, cause);
			}
		}

		private static final class PacketSendingException extends RuntimeException {
			private static final long serialVersionUID = 3203085387160737484L;

			/**
			 * Construct a new packet sending exception
			 *
			 * @param message
			 *            Message that will be logged
			 * @param cause
			 *            Cause of the exception
			 */
			public PacketSendingException(String message, Throwable cause) {
				super(message, cause);
			}
		}
	}
}
