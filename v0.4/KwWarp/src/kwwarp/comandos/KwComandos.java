package kwwarp.comandos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import kwwarp.eventos.api.PlayerEnterWarp;
import kwwarp.eventos.api.PlayerLeaveWarp;
import kwwarp.eventos.api.PlayerTeleportWarp;
import kwwarp.metodos.CuboID;
import kwwarp.metodos.Metodos;
import kwwarp.metodos.Posicao;
import me.kewi.KwWarp;

public class KwComandos implements CommandExecutor {

	private Metodos m;
	private KwWarp pl;
	private Server s;
	private BukkitScheduler bs;
	private PluginManager pm;
	public Map<String, String> n_warp;

	public KwComandos(Metodos m) {
		this.m = m;
		pl = m.getPlugin();
		s = pl.getServer();
		bs = s.getScheduler();
		pm = s.getPluginManager();
		n_warp = m.getVariaveis().getWarp();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender kw, Command cmd, String arg2, String[] args) {
		if (!(kw instanceof Player)) {
			if (cmd.getName().equalsIgnoreCase("kwwarp")) {
				if ((args.length == 1) && (args[0].equalsIgnoreCase("reload"))) {
					if (kw.hasPermission("kwwarp.*")) {
						m.getArquivos().atualizarConfig();
						m.getArquivos().atualizarWarps();
						kw.sendMessage("§b[KwWarp]§3 Configurações Recarregadas!");
						return true;
					}
				}
				kw.sendMessage("§b[KwWarp]§3 KwWarp - v" + pl.getDescription().getVersion());
				kw.sendMessage("§b[KwWarp]§3 Plugin desenvolvido por Kewilleen G. Sem intenções a fins lucrativos");
				kw.sendMessage("§b[KwWarp]§3 Para recarregar a config.yml e warps.yml, digite: /kwwarp reload");
				return true;
			}
			m.enviarConsole("Utilize este comando apenas in-game!");
			return true;
		}
		final Player p = (Player) kw;
		final String pp = p.getName().toLowerCase();
		if (cmd.getName().equalsIgnoreCase("warp")) {
			if (args.length == 1) {
				final String warp = args[0].toLowerCase();
				if (!m.getWarp().temWarp(warp)) {
					p.sendMessage(m.getCores("Warp.Inexistente").replace("{warp}", warp));
					return true;
				}
				if (!(p.hasPermission(m.getTexto("Warp.Permissao").replace("{warp}", warp))
						|| p.hasPermission("kwwarp.*"))) {
					p.sendMessage(m.getCores("Warp.Sem_Permissao").replace("{warp}", warp));
					return true;
				}
				if (!(p.hasPermission(m.getTexto("Warp.Delay.Permissao").replace("{warp}", warp))
						|| p.hasPermission("kwwarp.*"))) {
					if (!m.getDelay().temDelay(pp)) {
						m.getDelay().adicionarDelay(pp, m.getConfig().getInt("Warp.Delay.Tempo"));
						PlayerEnterWarp pew = new PlayerEnterWarp(p, warp, m.getDelay());
						pm.callEvent(pew);
						if (pew.isCancelled()) {
							return true;
						}
						bs.scheduleSyncDelayedTask(pl, new Runnable() {

							@Override
							public void run() {
								m.getDelay().removerDelay(pp);
								p.teleport(m.getWarp().getWarp(warp));
								if (!m.temWarp(pp, warp)) {
									m.setVezes(pp, warp, 0);
								} else {
									m.addVezes(pp, warp, 1);
								}
								if (m.getVariaveis().getTask().containsKey(pp)) {
									String w = n_warp.get(pp);
									if (m.getVariaveis().getAreaPlayer().containsKey(w)) {
										List<Player> l = m.getVariaveis().getAreaPlayer().get(w);
										if (l.size() == 1) {
											m.getVariaveis().getAreaPlayer().remove(w);
										} else {
											l.remove(p);
											m.getVariaveis().getAreaPlayer().put(w, l);
										}
									}
									bs.cancelTask(m.getVariaveis().getTask().get(pp));
									m.getVariaveis().getTask().remove(pp);
									m.getVariaveis().getPlayers().remove(p);
									PlayerLeaveWarp lw = new PlayerLeaveWarp(p, w, false, m.getWarp().getArea(w, 1),
											m.getWarp().getArea(w, 2), m.getVariaveis().getAreaPlayer());
									n_warp.remove(pp);
									pm.callEvent(lw);
									if (!lw.isCancelled())
										p.sendMessage(m.getCores("Warp.Saiu").replace("{warp}", w));
								}
								List<Player> ps = new ArrayList<>();
								if (m.getVariaveis().getAreaPlayer().containsKey(warp))
									ps = m.getVariaveis().getAreaPlayer().get(warp);
								ps.add(p);
								m.getVariaveis().getAreaPlayer().put(warp, ps);
								m.getVariaveis().getPlayers().add(p);
								if (!m.getVariaveis().getTask().containsKey(pp)) {
									int task = 0;
									n_warp.put(pp, warp);
									task = bs.scheduleSyncRepeatingTask(pl, new Runnable() {

										@Override
										public void run() {
											CuboID cid = new CuboID(m.getWarp().getArea(warp, 1),
													m.getWarp().getArea(warp, 2));
											if (!cid.contains(p.getLocation())) {
												bs.cancelTask(m.getVariaveis().getTask().get(pp));
												if (m.getVariaveis().getAreaPlayer().containsKey(warp)) {
													List<Player> l = m.getVariaveis().getAreaPlayer().get(warp);
													if (l.size() == 1) {
														m.getVariaveis().getAreaPlayer().remove(warp);
													} else {
														l.remove(p);
														m.getVariaveis().getAreaPlayer().put(warp, l);
													}
												}
												m.getVariaveis().getTask().remove(pp);
												m.getVariaveis().getPlayers().remove(p);
												PlayerLeaveWarp lw = new PlayerLeaveWarp(p, warp,
														cid.contains(p.getLocation()), m.getWarp().getArea(warp, 1),
														m.getWarp().getArea(warp, 2), m.getVariaveis().getAreaPlayer());
												pm.callEvent(lw);
												if (lw.isCancelled()) {
													return;
												}
												p.sendMessage(m.getCores("Warp.Saiu").replace("{warp}", warp));
											}
										}
									}, 20, 20);
									m.getVariaveis().getTask().put(pp, task);
								}

								PlayerTeleportWarp tw = new PlayerTeleportWarp(p, warp, m.getWarp().getWarp(warp),
										m.getVezes(pp, warp), m.getWarp().getArea(warp, 1),
										m.getWarp().getArea(warp, 2), m.getVariaveis().getPlayers(),
										m.getVariaveis().getAreaPlayer());
								pm.callEvent(tw);
								if (tw.isCancelled()) {
									return;
								}
								p.sendMessage(m.getCores("Warp.Teleportado").replace("{warp}", warp));
								return;
							}
						}, m.getConfig().getInt("Warp.Delay.Tempo") * 20L);
						p.sendMessage(m.getCores("Warp.Delay.Aguarde").replace("{warp}", warp).replace("{tempo}",
								m.getDelay().getTempo(pp)));
						return true;
					}
					p.sendMessage(m.getCores("Warp.Delay.Aguarde").replace("{warp}", warp).replace("{tempo}",
							m.getDelay().getTempo(pp)));
					return true;
				}
				PlayerEnterWarp pew = new PlayerEnterWarp(p, warp, m.getDelay());
				pm.callEvent(pew);
				if (pew.isCancelled()) {
					return true;
				}
				p.teleport(m.getWarp().getWarp(warp));
				if (!m.temWarp(pp, warp)) {
					m.setVezes(pp, warp, 0);
				} else {
					m.addVezes(pp, warp, 1);
				}
				if (m.getVariaveis().getTask().containsKey(pp)) {
					String w = this.n_warp.get(pp);
					if (m.getVariaveis().getAreaPlayer().containsKey(w)) {
						List<Player> l = m.getVariaveis().getAreaPlayer().get(w);
						if (l.size() == 1) {
							m.getVariaveis().getAreaPlayer().remove(w);
						} else {
							l.remove(p);
							m.getVariaveis().getAreaPlayer().put(w, l);
						}
					}
					bs.cancelTask(m.getVariaveis().getTask().get(pp));
					m.getVariaveis().getTask().remove(pp);
					m.getVariaveis().getPlayers().remove(p);
					this.n_warp.remove(pp);
					PlayerLeaveWarp lw = new PlayerLeaveWarp(p, w, false, m.getWarp().getArea(w, 1),
							m.getWarp().getArea(w, 2), m.getVariaveis().getAreaPlayer());
					pm.callEvent(lw);
					if (!lw.isCancelled())
						p.sendMessage(m.getCores("Warp.Saiu").replace("{warp}", w));
				}
				List<Player> ps = new ArrayList<>();
				if (m.getVariaveis().getAreaPlayer().containsKey(warp))
					ps = m.getVariaveis().getAreaPlayer().get(warp);
				ps.add(p);
				m.getVariaveis().getAreaPlayer().put(warp, ps);
				m.getVariaveis().getPlayers().add(p);
				if (!m.getVariaveis().getTask().containsKey(pp)) {
					int task = 0;
					this.n_warp.put(pp, warp);
					task = bs.scheduleSyncRepeatingTask(pl, new Runnable() {

						@Override
						public void run() {
							CuboID cid = new CuboID(m.getWarp().getArea(warp, 1), m.getWarp().getArea(warp, 2));
							if (!cid.contains(p.getLocation())) {
								bs.cancelTask(m.getVariaveis().getTask().get(pp));
								m.getVariaveis().getTask().remove(pp);
								m.getVariaveis().getPlayers().remove(p);
								if (m.getVariaveis().getAreaPlayer().containsKey(warp)) {
									List<Player> l = m.getVariaveis().getAreaPlayer().get(warp);
									if (l.size() == 1) {
										m.getVariaveis().getAreaPlayer().remove(warp);
									} else {
										l.remove(p);
										m.getVariaveis().getAreaPlayer().put(warp, l);
									}
								}
								PlayerLeaveWarp lw = new PlayerLeaveWarp(p, warp, cid.contains(p.getLocation()),
										m.getWarp().getArea(warp, 1), m.getWarp().getArea(warp, 2),
										m.getVariaveis().getAreaPlayer());
								pm.callEvent(lw);
								if (lw.isCancelled()) {
									return;
								}
								p.sendMessage(m.getCores("Warp.Saiu").replace("{warp}", warp));
							}
						}
					}, 20, 20);
					m.getVariaveis().getTask().put(pp, task);
				}
				PlayerTeleportWarp tw = new PlayerTeleportWarp(p, warp, m.getWarp().getWarp(warp), m.getVezes(pp, warp),
						m.getWarp().getArea(warp, 1), m.getWarp().getArea(warp, 2), m.getVariaveis().getPlayers(),
						m.getVariaveis().getAreaPlayer());
				pm.callEvent(tw);
				if (tw.isCancelled()) {
					return true;
				}
				p.sendMessage(m.getCores("Warp.Teleportado").replace("{warp}", warp));
				return true;
			}
			p.sendMessage(m.getCores("Warp.Utilize"));
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("setwarp")) {
			if (!(p.hasPermission(m.getTexto("Warp.Setar.Permissao")) || p.hasPermission("kwwarp.*"))) {
				p.sendMessage(m.getCores("Warp.Setar.Sem_Permissao"));
				return true;
			}
			if (args.length == 0) {
				String id = m.getTexto("Warp.Setar.Item.ID");
				String separar[] = id.split(":");
				ItemStack is;
				if (separar.length > 1) {
					is = new ItemStack(Integer.parseInt(separar[0]), 1, (short) Integer.parseInt(separar[1]));
				} else {
					is = new ItemStack(Integer.parseInt(separar[0]), 1);
				}
				int slot = m.getConfig().getInt("Warp.Setar.Item.Slot");
				p.getInventory().setItem(slot, is);
				p.updateInventory();
				p.sendMessage(m.getCores("Warp.Setar.Utilize"));
				Posicao pos = new Posicao();
				m.getVariaveis().getPos().put(pp, pos);
				return true;
			}
			if (args.length == 3) {
				if (!m.getVariaveis().getPos().containsKey(pp)) {
					p.sendMessage(m.getCores("Warp.Setar.Use"));
					return true;
				}
				Posicao pos = m.getVariaveis().getPos().get(pp);
				if (!pos.temPos(1)) {
					p.sendMessage(m.getCores("Warp.Setar.Pos.Um"));
					return true;
				}
				if (!pos.temPos(2)) {
					p.sendMessage(m.getCores("Warp.Setar.Pos.Um"));
					return true;
				}
				String id = args[1];
				String data[] = id.split(":");
				if (data.length > 1) {
					if (!m.temNumero(data[0])) {
						p.sendMessage(m.getCores("Warp.Setar.IDs.ID").replace("{id}", data[0]));
						return true;
					}
					if (!m.temNumero(data[1])) {
						p.sendMessage(m.getCores("Warp.Setar.IDs.Data").replace("{data}", data[1]));
						return true;
					}
				} else {
					if (!m.temNumero(data[0])) {
						p.sendMessage(m.getCores("Warp.Setar.IDs.ID").replace("{id}", data[0]));
						return true;
					}
					if (Material.getMaterial(Integer.parseInt(data[0])) == null) {
						p.sendMessage(m.getCores("Warp.Setar.IDs.ID").replace("{id}", data[0]));
						return true;
					}
				}
				String slot = args[2];
				if (!m.temNumero(slot)) {
					p.sendMessage(m.getCores("Warp.Setar.Slot"));
					return true;
				}
				int s = Integer.parseInt(slot);
				if (s < 0 || s > m.getConfig().getInt("Warp.Warps.GUI.Tamanho")) {
					p.sendMessage(m.getCores("Warp.Setar.Tamanho"));
					return true;
				}
				p.sendMessage(m.getCores("Warp.Setar.Pos.Setou"));
				m.getWarp().setWarp(args[0], id, s, p.getLocation(), pos.getPos(1), pos.getPos(2));
				pos.clear();
				m.getVariaveis().getPos().remove(pp);
				return true;
			}
			p.sendMessage(m.getCores("Warp.Setar.Use"));
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("delwarp")) {
			if (!(p.hasPermission(m.getTexto("Warp.Remover.Permissao")) || p.hasPermission("kwwarp.*"))) {
				p.sendMessage(m.getCores("Warp.Remover.Sem_Permissao"));
				return true;
			}
			if (args.length == 1) {
				String warp = args[0].toLowerCase();
				if (!m.getWarp().temWarp(warp)) {
					p.sendMessage(m.getCores("Warp.Remover.Inexistente"));
					return true;
				}
				CuboID cid = new CuboID(m.getWarp().getArea(warp, 1), m.getWarp().getArea(warp, 2));
				for (Player ps : s.getOnlinePlayers())
					if (cid.contains(ps.getLocation())) {
						if (m.getVariaveis().getTask().containsKey(ps.getName().toLowerCase())) {
							bs.cancelTask(m.getVariaveis().getTask().get(ps.getName().toLowerCase()));
							m.getVariaveis().getTask().remove(ps.getName().toLowerCase());
							m.getVariaveis().getPlayers().remove(ps);
						}
					}
				m.getWarp().delWarp(warp);
				p.sendMessage(m.getCores("Warp.Remover.Removeu").replace("{warp}", args[0]));
				return true;
			}
			p.sendMessage(m.getCores("Warp.Remover.Utilize"));
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("warps")) {
			if (m.getArquivos().getWarp().exists() && m.getArquivos().getWarps().getString("Warps") != null)
				p.openInventory(m.getInventario().getWarps(pp));
		}
		if (cmd.getName().equalsIgnoreCase("kwwarp")) {
			if ((args.length == 1) && (args[0].equalsIgnoreCase("reload"))) {
				if (kw.hasPermission("kwwarp.*")) {
					m.getArquivos().atualizarConfig();
					m.getArquivos().atualizarWarps();
					kw.sendMessage("§b[KwWarp]§3 Configurações Recarregadas!");
					return true;
				}
			}
			kw.sendMessage("§b[KwWarp]§3 KwWarp - v" + pl.getDescription().getVersion());
			kw.sendMessage("§b[KwWarp]§3 Plugin desenvolvido por Kewilleen G. Sem intenções a fins lucrativos");
			kw.sendMessage("§b[KwWarp]§3 Para recarregar a config.yml e warps.yml, digite: /kwwarp reload");
			return true;
		}
		return false;
	}

}
