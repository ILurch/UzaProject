package de.lurch.funcannon.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.lurch.funcannon.util.FunCannonMode;

public class FunCannonLaunchEvent extends FunCannonEvent {

	private static HandlerList handlers = new HandlerList();
	
	public FunCannonLaunchEvent(Player player, FunCannonMode mode) {
		super(player, mode);
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
