package kwwarp.eventos.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveWarp extends Event {

	private Player p;
	private String warp;
	private boolean area;
	private Location pos1, pos2;
	private boolean cancelado;
	private static HandlerList handlers = new HandlerList();
	private Map<String, List<Player>> within;

	public PlayerLeaveWarp(Player player, String warp, boolean area, Location pos1, Location pos2,
			Map<String, List<Player>> within) {
		this.p = player;
		this.warp = warp == null ? "" : warp;
		this.area = area;
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.within = within;
		this.cancelado = false;
	}

	public Player getPlayer() {
		return p;
	}

	public String getWarpName() {
		return warp;
	}

	public boolean withinArea() {
		return area;
	}

	public Location getPos1() {
		return pos1;
	}

	public Location getPos2() {
		return pos2;
	}

	public Map<String, List<Player>> getPlayerByWarp() {
		return within;
	}

	public List<Player> getPlayersWarp(String warp) {
		List<Player> l = new ArrayList<>();
		if (within.containsKey(warp.toLowerCase()))
			l = within.get(warp.toLowerCase());
		return l;
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
