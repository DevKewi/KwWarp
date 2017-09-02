package kwwarp.eventos.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import kwwarp.metodos.Delay;

public class PlayerEnterWarp extends Event {

	private Player p;
	private String warp;
	private Delay d;
	private boolean cancelado;
	private static HandlerList handlers = new HandlerList();

	public PlayerEnterWarp(Player p, String warp, Delay delay) {
		this.p = p;
		this.warp = warp;
		this.d = delay;
		this.cancelado = false;
	}

	public Player getPlayer() {
		return p;
	}

	public String getWarpName() {
		return warp;
	}

	public Delay getDelay() {
		return d;
	}

	public String getTempo() {
		return d.getTempo(p.getName().toLowerCase());
	}

	public boolean isCancelled() {
		return cancelado;
	}

	public void setCanceled(boolean cancelar) {
		this.cancelado = cancelar;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
