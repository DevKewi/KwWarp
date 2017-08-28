package kwwarp.metodos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import me.kewi.KwWarp;

public class KwUpdate implements Listener {

	private static Plugin pl = KwWarp.getPlugin();
	protected static String update = "";

	public static void getUpdate(String site, FileConfiguration cnf, String path) {
		if (cnf.getBoolean(path)) {
			try {
				URL url = new URL("http://devkewi.esy.es/" + site + ".txt");
				URLConnection con = url.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String v = in.readLine();

				if (!pl.getDescription().getVersion().equals(v)) {
					System.out.println(" ");
					KwMetodos.getConsole("Novo update disponivel! §bV: " + v);
					KwMetodos.getConsole("Download: http://devkewi.esy.es/" + site + ".jar");
					update = v;
				} else {
					KwMetodos.getConsole("Nenhum update disponivel!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				KwMetodos.getConsole("Erro ao procurar novos updates! Certifique sua internet!");
			}
		}
	}

	@EventHandler
	public void quandoEntrar(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (p.hasPermission("kwwarp.admin")) {
			try {
				URL url = new URL("http://devkewi.esy.es/KwWarp.txt");
				URLConnection con = url.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String v = in.readLine();

				if (!pl.getDescription().getVersion().equals(v)) {
					p.sendMessage("§b[KwWarp] §3Nova atualização está disponível!");
					p.sendMessage("§b[KwWarp] §3Faça o download em: http://devkewi.esy.es/KwWarp.jar");
					p.sendMessage("§b[KwWarp] §3/kwwarp help, para saber mais sobre o autor!");
					update = v;
				}
			} catch (Exception es) {
				es.printStackTrace();
				KwMetodos.getConsole("Erro ao procurar novos updates! Certifique sua internet!");
			}
		}
	}

}