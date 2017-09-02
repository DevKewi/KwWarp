package kwwarp.metodos;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

public class Posicao {

	private Map<Integer, Location> pos = new HashMap<>();

	public void setPos(Location loc, int pos) {
		if ((pos > 2) || (pos < 1))
			return;
		this.pos.put(pos, loc);
	}

	public Location getPos(int pos) {
		if (temPos(pos))
			return this.pos.get(pos);
		return null;
	}

	public boolean temPos(int pos) {
		return this.pos.containsKey(pos);
	}

	public void clear() {
		pos.remove(1);
		pos.remove(2);
	}

}
