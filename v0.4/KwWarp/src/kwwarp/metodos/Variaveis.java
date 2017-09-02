package kwwarp.metodos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

public class Variaveis {

	private List<Player> players = new ArrayList<>();
	private List<String> warps = new ArrayList<>();
	private Map<String, Posicao> pos = new HashMap<>();
	private Map<String, Integer> task = new HashMap<>();
	private Map<String, String> warp = new HashMap<>();
	private Map<String, List<Player>> within = new HashMap<>();

	public List<String> getWarps() {
		return warps;
	}

	public Map<String, Posicao> getPos() {
		return pos;
	}

	public Map<String, Integer> getTask() {
		return task;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Map<String, String> getWarp() {
		return warp;
	}

	public Map<String, List<Player>> getAreaPlayer() {
		return within;
	}

}
