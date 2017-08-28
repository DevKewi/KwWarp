package kwwarp.metodos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;

public class Warp {

	private Variaveis v;
	private Config c;
	private Server s;

	public Warp(Metodos m) {
		v = m.getVariaveis();
		c = m.getArquivos();
		s = m.getPlugin().getServer();
	}

	public boolean temWarp(String warp) {
		if (!c.getWarp().exists())
			return false;
		if (v.getWarps().isEmpty())
			atualizar();
		if (v.getWarps().contains(warp))
			return true;
		if (c.getWarps().getString("Warps." + warp) != null) {
			atualizar();
			return true;
		}
		return false;
	}

	public void atualizar() {
		if (c.getWarp().exists() && c.getWarps().getString("Warps") != null)
			for (String warp : c.getWarps().getConfigurationSection("Warps").getKeys(false))
				if (!v.getWarps().contains(warp))
					v.getWarps().add(warp);
	}

	public Location getWarp(String warp) {
		World w = s.getWorld(c.getWarps().getString("Warps." + warp + ".Mundo"));
		double x, y, z;
		float pitch, yaw;
		x = c.getWarps().getDouble("Warps." + warp + ".X");
		y = c.getWarps().getDouble("Warps." + warp + ".Y");
		z = c.getWarps().getDouble("Warps." + warp + ".Z");
		pitch = (float) c.getWarps().getDouble("Warps." + warp + ".Pitch");
		yaw = (float) c.getWarps().getDouble("Warps." + warp + ".Yaw");
		Location loc = new Location(w, x, y, z);
		loc.setPitch(pitch);
		loc.setYaw(yaw);
		return loc;
	}

	public List<String> getWarps() {
		return v.getWarps();
	}

	public void setWarp(String warp, String id, int slot, Location loc, Location pos, Location pos1) {
		List<String> l = new ArrayList<>();
		c.getWarps().set("Warps." + warp + ".Item.Nome", "&a" + warp);
		c.getWarps().set("Warps." + warp + ".Item.ID", id);
		l.add("&aAltere essa linha, assim que possível!");
		l.add("&aVocê foi a esta warp: {vezes}");
		c.getWarps().set("Warps." + warp + ".Item.Lore", l);
		c.getWarps().set("Warps." + warp + ".Item.Slot", slot);
		c.getWarps().set("Warps." + warp + ".Mundo", loc.getWorld().getName());
		c.getWarps().set("Warps." + warp + ".X", loc.getX());
		c.getWarps().set("Warps." + warp + ".Y", loc.getY());
		c.getWarps().set("Warps." + warp + ".Z", loc.getZ());
		c.getWarps().set("Warps." + warp + ".Pitch", loc.getPitch());
		c.getWarps().set("Warps." + warp + ".Yaw", loc.getYaw());
		c.getWarps().set("Warps." + warp + ".Area.1.X", pos.getX());
		c.getWarps().set("Warps." + warp + ".Area.1.Y", pos.getY());
		c.getWarps().set("Warps." + warp + ".Area.1.Z", pos.getZ());
		c.getWarps().set("Warps." + warp + ".Area.2.X", pos1.getX());
		c.getWarps().set("Warps." + warp + ".Area.2.Y", pos1.getY());
		c.getWarps().set("Warps." + warp + ".Area.2.Z", pos1.getZ());
		try {
			c.getWarps().save(c.getWarp());
			c.getWarps().load(c.getWarp());
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public Location getArea(String warp, int pos) {
		World w = s.getWorld(c.getWarps().getString("Warps." + warp + ".Mundo"));
		double x, y, z;
		x = c.getWarps().getDouble("Warps." + warp + ".Area." + pos + ".X");
		y = c.getWarps().getDouble("Warps." + warp + ".Area." + pos + ".Y");
		z = c.getWarps().getDouble("Warps." + warp + ".Area." + pos + ".Z");
		Location loc = new Location(w, x, y, z);
		return loc;
	}

	public void delWarp(String warp) {
		c.getWarps().set("Warps." + warp.toLowerCase(), null);
		v.getWarps().remove(warp.toLowerCase());
		try {
			c.getWarps().save(c.getWarp());
			c.getWarps().load(c.getWarp());
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

}
