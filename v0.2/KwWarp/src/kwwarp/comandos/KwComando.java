package kwwarp.comandos;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import kwwarp.metodos.KwMetodos;
import me.kewi.KwWarp;

public class KwComando implements CommandExecutor {

	private static Server s = Bukkit.getServer();
	private static FileConfiguration cnf = KwWarp.getPlugin().getConfig();
	private static BukkitScheduler bs = s.getScheduler();
	private static KwWarp pl = KwWarp.getPlugin();

	@Override
	public boolean onCommand(CommandSender kw, Command cmd, String arg2, String[] args) {
		if (!(kw instanceof Player)) {
			KwMetodos.getErro(true, false, false, null);
			return true;
		}
		final Player p = (Player) kw;
		final String pp = p.getName();
		if (cmd.getName().equalsIgnoreCase("warp")) {
			if (args.length == 0) {
				p.sendMessage(KwMetodos.getReplaced("Utilize"));
				return true;
			}
			final String warp = args[0];
			if (args.length == 1) {
				if (!KwMetodos.getPerms(p, warp)) {
					p.sendMessage(KwMetodos.getReplaced("Sem_Permissao_Warp").replace("{warp}", warp));
					return true;
				}
				if (!KwMetodos.getWarp(warp)) {
					p.sendMessage(KwMetodos.getReplaced("Sem_Warp").replace("{warp}", warp));
					return true;
				} else {
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
										p.teleport(KwMetodos.teleportWarp(warp));
										p.sendMessage(KwMetodos.getReplaced("Teleportado").replace("{warp}", warp));
										if (KwMetodos.getBoolean("Mensagens.Global.Ativar")) {
											s.broadcastMessage(cnf.getString("Mensagens.Global.Mensagem")
													.replace("&", "§").replace("{p}", pp).replace("{warp}", warp));
										}
									}
								}
							}, 20L * tempo);
						} else {
							p.teleport(KwMetodos.teleportWarp(warp));
							p.sendMessage(KwMetodos.getReplaced("Teleportado").replace("{warp}", warp));
							if (KwMetodos.getBoolean("Mensagens.Global.Ativar")) {
								s.broadcastMessage(cnf.getString("Mensagens.Global.Mensagem").replace("&", "§")
										.replace("{p}", pp).replace("{warp}", warp));
							}
						}
					} else {
						p.sendMessage(KwMetodos.getReplaced("Aguarde").replace("{tempo}", Integer.toString(tempo)));
					}
				}
			}
		}
		if (cmd.getName().equalsIgnoreCase("setwarp")) {
			if (!(p.hasPermission(KwMetodos.getCnf("Warp.Permissao")) || p.hasPermission("kwwarp.admin"))) {
				p.sendMessage(KwMetodos.getReplaced("Sem_Permissao"));
				return true;
			}
			if ((args.length == 0) || (args.length > 1)) {
				p.sendMessage(KwMetodos.getReplaced("Utilize"));
				return true;
			}
			String warp = args[0];
			if (args.length == 1) {
				KwMetodos.setWarp(p, warp);
			}
		}
		if (cmd.getName().equalsIgnoreCase("delwarp")) {
			if (!(p.hasPermission(KwMetodos.getCnf("Warp.Permissao")) || p.hasPermission("kwwarp.admin"))) {
				p.sendMessage(KwMetodos.getReplaced("Sem_Permissao"));
				return true;
			}
			if ((args.length == 0) || (args.length > 1)) {
				p.sendMessage(KwMetodos.getReplaced("Utilize"));
				return true;
			}
			String warp = args[0];
			if (args.length == 1) {
				if (KwMetodos.getWarps() != null) {
					for (String warps : KwMetodos.getWarps()) {
						if (warps.equalsIgnoreCase(warp)) {
							KwMetodos.delWarp(warps);
							p.sendMessage(KwMetodos.getReplaced("Warps.Removida").replace("{warp}", warps));
						}
					}
				}
			}
		}
		if (cmd.getName().equalsIgnoreCase("warps")) {
			if (!(p.hasPermission(KwMetodos.getCnf("Warp.Permissao_Ver")) || p.hasPermission("kwwarp.admin"))) {
				p.sendMessage(KwMetodos.getReplaced("Sem_Permissao"));
				return true;
			}
			if (args.length == 0) {
				if (KwMetodos.getWarps() != null) {
					String warps = "";
					for (String w : KwMetodos.getWarps()) {
						warps = warps + ", " + w;
					}
					if (warps.startsWith(", ")) {
						warps = warps.substring(2);
					}
					p.sendMessage(KwMetodos.getReplaced("Warps.Setadas").replace("{warps}", warps));
				} else {
					p.sendMessage(KwMetodos.getReplaced("Warps.Sem_Warp"));
				}
			}
		}
		if (cmd.getName().equalsIgnoreCase("kwwarp")) {
			if (args.length == 0) {
				p.sendMessage("§b[KwWarp] §3Helpzinho básico:");
				p.sendMessage("§bPara dar reload utilize §3/kwwarp reload");
				p.sendMessage("§bPara saber quem programou este plugin utilize §3/kwwarp help");
			}
			if ((args.length == 1) && (args[0].equalsIgnoreCase("reload"))) {
				if (!(p.hasPermission(KwMetodos.getCnf("Warp.Permissao")) || p.hasPermission("kwwarp.admin"))) {
					p.sendMessage(KwMetodos.getReplaced("Sem_Permissao"));
					return true;
				}
				KwWarp.getPlugin().reloadConfig();
				p.sendMessage("§aConfigurações recarregadas!");
			}
			if ((args.length == 1) && (args[0].equalsIgnoreCase("help"))) {
				if (!(p.hasPermission(KwMetodos.getCnf("Warp.Permissao")) || p.hasPermission("kwwarp.admin"))) {
					p.sendMessage("§b[KwWarp] §3Help");
					p.sendMessage("§aUtilize: /warp (nome da warp), porque você não pode ver :D");
					p.sendMessage("§aQualquer coisa adicione §bSkype&a: dev.kewilleen");
					return true;
				}
				p.sendMessage("§b[KwWarp] §3Créditos:");
				p.sendMessage("§b[KwWarp] §3Programador: Dev Kewi");
				p.sendMessage("§b[KwWarp] §3Site: http://devkewi.esy.es");
				p.sendMessage("§b[KwWarp] §3Sugestões/Duvidas/Erros, adicione §bSkype§3: dev.kewilleen");
				p.sendMessage("§b[KwWarp] §3Kewilleen agradece por usufruir este plugin!");
			}
		}
		return false;
	}

}
