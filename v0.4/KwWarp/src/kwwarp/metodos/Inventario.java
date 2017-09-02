package kwwarp.metodos;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.kewi.KwWarp;

public class Inventario {

	private Metodos m;
	private KwWarp pl;
	private Server s;
	private FileConfiguration fc;

	public Inventario(Metodos m) {
		this.m = m;
		pl = m.getPlugin();
		s = pl.getServer();
		fc = m.getArquivos().getWarps();
	}

	public Inventory getWarps(String jogador) {
		Inventory inv = s.createInventory(null, m.getConfig().getInt("Warp.Warps.GUI.Tamanho"),
				m.getCores("Warp.Warps.GUI.Nome"));
		for (String item : fc.getConfigurationSection("Warps").getKeys(false)) {
			String menu = "Warps." + item + ".Item.";
			String id = fc.getString(menu + "ID");
			String data[] = id.split(":");
			ItemStack is;
			if (data.length > 1) {
				is = new ItemStack(Integer.parseInt(data[0]), 1, (short) Integer.parseInt(data[1]));
			} else {
				is = new ItemStack(Integer.parseInt(data[0]), 1);
			}
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(fc.getString(menu + "Nome").replace("&", "§"));
			List<String> l = new ArrayList<>();
			for (String s : fc.getStringList(menu + "Lore"))
				l.add(m.putCor(s).replace("{vezes}", Integer.toString(m.getVezes(jogador, item))));
			im.setLore(l);
			is.setItemMeta(im);
			inv.setItem(fc.getInt(menu + "Slot"), is);
		}
		return inv;
	}

}
