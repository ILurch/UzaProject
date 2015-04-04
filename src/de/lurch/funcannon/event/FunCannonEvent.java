package de.lurch.funcannon.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.lurch.funcannon.util.FunCannonMode;

public abstract class FunCannonEvent extends Event {

	private HandlerList handlers = new HandlerList();
	private Player player;
	private FunCannonMode mode;
	
	
	public FunCannonEvent(Player player, FunCannonMode mode) {
		this.player = player;
		this.mode = mode;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public FunCannonMode getFunCannonMode() {
		return mode;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
