package kwwarp.eventos;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;

import kwwarp.metodos.KwMetodos;
import me.kewi.KwWarp;

public class KwEventos implements Listener {

	private static Server s = Bukkit.getServer();
	private static FileConfiguration cnf = KwWarp.getPlugin().getConfig();
	private static BukkitScheduler bs = s.getScheduler();
	private static KwWarp pl = KwWarp.getPlugin();

	@EventHandler
	public void quandoDigitar(final PlayerCommandPreprocessEvent e) {
		final Player p = e.getPlayer();
		String msg = e.getMessage();
		final String pp = p.getName();
		for (final String warps : KwMetodos.getWarps()) {
			if (msg.equalsIgnoreCase("/" + warps)) {
				if (!KwMetodos.getPerms(p, warps)) {
					p.sendMessage(KwMetodos.getReplaced("Sem_Permissao_Warp").replace("{warp}", warps));
					e.setCancelled(true);
					return;
				}
				int tempo = cnf.getInt("Warp.Delay");
				if (!KwMetodos.getDelay().contains(pp)) {
					if (!(p.hasPermission("kwwarp.vip") || p.hasPermission("kwwarp.admin"))) {
						p.sendMessage(KwMetodos.getReplaced("Aguarde").replace("{tempo}", Integer.toString(tempo)));
						KwMetodos.getDelay().add(pp);
						bs.scheduleSyncDelayedTask(pl, new Runnable() {

							@Override
							public void run() {
								if (KwMetodos.getDelay().contains(pp)) {
									KwMetodos.getDelay().remove(pp);
									p.teleport(KwMetodos.teleportWarp(warps));
									p.sendMessage(KwMetodos.getReplaced("Teleportado").replace("{warp}", warps));
									if (KwMetodos.getBoolean("Mensagens.Global.Ativar")) {
										s.broadcastMessage(cnf.getString("Mensagens.Global.Mensagem").replace("&", "§")
												.replace("{p}", pp).replace("{warp}", warps));
									}
									e.setCancelled(true);
								}
							}
						}, 20L * tempo);
					} else {
						p.teleport(KwMetodos.teleportWarp(warps));
						p.sendMessage(KwMetodos.getReplaced("Teleportado").replace("{warp}", warps));
						if (KwMetodos.getBoolean("Mensagens.Global.Ativar")) {
							s.broadcastMessage(cnf.getString("Mensagens.Global.Mensagem").replace("&", "§")
									.replace("{p}", pp).replace("{warp}", warps));
						}
					}
				} else {
					p.sendMessage(KwMetodos.getReplaced("Aguarde").replace("{tempo}", Integer.toString(tempo)));
					e.setCancelled(true);
				}
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void quandoSeMexer(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		String pp = p.getName();
		if (KwMetodos.getDelay().contains(pp)) {
			KwMetodos.getDelay().remove(pp);
			p.sendMessage(KwMetodos.getReplaced("Cancelado"));
		}
	}

}
