package kwwarp.eventos.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTeleportWarp extends Event {

	private Player p;
	private String warp;
	private Location loc, pos1, pos2;
	private int vezes;
	private boolean cancelado = false;
	private static HandlerList handlers = new HandlerList();
	private List<Player> l;
	private Map<String, List<Player>> within;

	public PlayerTeleportWarp(Player p, String warp, Location loc, int vezes, Location pos1, Location pos2,
			List<Player> players, Map<String, List<Player>> within) {
		this.p = p;
		this.warp = warp == null ? "" : warp;
		this.loc = loc;
		this.vezes = vezes;
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.l = players;
		this.within = within;
	}

	public Player getPlayer() {
		return p;
	}

	public String getWarpName() {
		return warp;
	}

	public Location getWarp() {
		return loc;
	}

	public Location getPos1() {
		return pos1;
	}

	public Location getPos2() {
		return pos2;
	}

	public List<Player> getPlayers() {
		return l;
	}

	public int getVezes() {
		return vezes;
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
