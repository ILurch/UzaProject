package de.lurch.funcannon.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.lurch.funcannon.util.FunCannonMode;

public class FunCannonHitEvent extends FunCannonEvent {

	private static HandlerList handlers = new HandlerList();
	
	private Location loc;
	private Player hitted;
	
	public FunCannonHitEvent(Player player, FunCannonMode mode, Location loc, Player hitted) {
		super(player, mode);
		this.loc = loc;
		this.hitted = hitted;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Player getHitted() {
		return hitted;
	}
}