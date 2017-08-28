package kwwarp.eventos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import kwwarp.eventos.api.PlayerLeaveWarp;
import kwwarp.eventos.api.PlayerTeleportWarp;
import kwwarp.metodos.CuboID;
import kwwarp.metodos.Metodos;
import kwwarp.metodos.Posicao;
import me.kewi.KwWarp;

public class KwEventos implements Listener {

	private Metodos m;
	private KwWarp pl;
	private Server s;
	private BukkitScheduler bs;
	private FileConfiguration fc;
	private Map<String, String> n_warp;

	public KwEventos(Metodos m) {
		this.m = m;
		pl = m.getPlugin();
		s = pl.getServer();
		bs = s.getScheduler();
		fc = m.getArquivos().getWarps();
		n_warp = m.getVariaveis().getWarp();
	}

	@EventHandler
	private void quandoClicar(final InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();
		final String pp = p.getName().toLowerCase();
		int i = e.getSlot();
		ItemStack is = e.getCurrentItem();
		Inventory inv = e.getInventory();
		if (inv.getName().equalsIgnoreCase(m.getCores("Warp.Warps.GUI.Nome"))) {
			if (is != null) {
				for (final String warp : fc.getConfigurationSection("Warps").getKeys(false)) {
					String menu = "Warps." + warp + ".Item.";
					int slot = fc.getInt(menu + "Slot");
					if (i == slot) {
						if (!(p.hasPermission(m.getTexto("Warp.Permissao").replace("{warp}", warp))
								|| p.hasPermission("kwwarp.*"))) {
							p.sendMessage(m.getCores("Warp.Sem_Permissao").replace("{warp}", warp));
							e.setCancelled(true);
							p.closeInventory();
							return;
						}
						if (!(p.hasPermission(m.getTexto("Warp.Delay.Permissao").replace("{warp}", warp))
								|| p.hasPermission("kwwarp.*"))) {
							if (!m.getDelay().temDelay(pp)) {
								m.getDelay().adicionarDelay(pp, m.getConfig().getInt("Warp.Delay.Tempo"));
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
											n_warp.remove(pp);
											m.getVariaveis().getPlayers().remove(p);
											PlayerLeaveWarp lw = new PlayerLeaveWarp(p, w, false,
													m.getWarp().getArea(w, 1), m.getWarp().getArea(w, 2),
													m.getVariaveis().getAreaPlayer());
											Bukkit.getPluginManager().callEvent(lw);
											if (!lw.isCancelled())
												p.sendMessage(m.getCores("Warp.Saiu").replace("{warp}", w));
										}
										List<Player> l = new ArrayList<>();
										if (m.getVariaveis().getAreaPlayer().containsKey(warp))
											l = m.getVariaveis().getAreaPlayer().get(warp);
										l.add(p);
										m.getVariaveis().getAreaPlayer().put(warp, l);
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
														PlayerLeaveWarp lw = new PlayerLeaveWarp(p, warp,
																cid.contains(p.getLocation()),
																m.getWarp().getArea(warp, 1),
																m.getWarp().getArea(warp, 2),
																m.getVariaveis().getAreaPlayer());
														Bukkit.getPluginManager().callEvent(lw);
														if (lw.isCancelled()) {
															e.setCancelled(true);
															p.closeInventory();
															return;
														}
														p.sendMessage(m.getCores("Warp.Saiu").replace("{warp}", warp));
													}
												}
											}, 20, 20);
											m.getVariaveis().getTask().put(pp, task);
										}

										PlayerTeleportWarp tw = new PlayerTeleportWarp(p, warp,
												m.getWarp().getWarp(warp), m.getVezes(pp, warp),
												m.getWarp().getArea(warp, 1), m.getWarp().getArea(warp, 2),
												m.getVariaveis().getPlayers(), m.getVariaveis().getAreaPlayer());
										Bukkit.getPluginManager().callEvent(tw);
										if (tw.isCancelled()) {
											e.setCancelled(true);
											p.closeInventory();
											return;
										}
										p.sendMessage(m.getCores("Warp.Teleportado").replace("{warp}", warp));
										e.setCancelled(true);
										p.closeInventory();
										return;
									}
								}, m.getConfig().getInt("Warp.Delay.Tempo") * 20L);
								p.sendMessage(m.getCores("Warp.Delay.Aguarde").replace("{warp}", warp)
										.replace("{tempo}", m.getDelay().getTempo(pp)));
								e.setCancelled(true);
								p.closeInventory();
								return;
							}
							p.sendMessage(m.getCores("Warp.Delay.Aguarde").replace("{warp}", warp).replace("{tempo}",
									m.getDelay().getTempo(pp)));
							e.setCancelled(true);
							p.closeInventory();
							return;
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
							Bukkit.getPluginManager().callEvent(lw);
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
										Bukkit.getPluginManager().callEvent(lw);
										if (lw.isCancelled()) {
											e.setCancelled(true);
											p.closeInventory();
											return;
										}
										p.sendMessage(m.getCores("Warp.Saiu").replace("{warp}", warp));
									}
								}
							}, 20, 20);
							m.getVariaveis().getTask().put(pp, task);
						}
						PlayerTeleportWarp tw = new PlayerTeleportWarp(p, warp, m.getWarp().getWarp(warp),
								m.getVezes(pp, warp), m.getWarp().getArea(warp, 1), m.getWarp().getArea(warp, 2),
								m.getVariaveis().getPlayers(), m.getVariaveis().getAreaPlayer());
						Bukkit.getPluginManager().callEvent(tw);
						if (tw.isCancelled()) {
							e.setCancelled(true);
							p.closeInventory();
							return;
						}
						p.sendMessage(m.getCores("Warp.Teleportado").replace("{warp}", warp));
						e.setCancelled(true);
						p.closeInventory();
						return;
					}
				}
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void aoProcessarComandos(PlayerCommandPreprocessEvent e) {
		final Player p = e.getPlayer();
		final String pp = p.getName().toLowerCase();
		String cmd = e.getMessage();
		for (final String warp : m.getWarp().getWarps()) {
			if (m.getDelay().temDelay(pp))
				if (m.getConfig().getBoolean("Warp.Delay.Comandos.Bloquear")) {
					List<String> comandos = m.getConfig().getStringList("Warp.Delay.Comandos.Liberado");
					for (String c : comandos)
						if (!cmd.startsWith(c)) {
							p.sendMessage(m.getCores("Warp.Delay.Comandos.Bloqueado").replace("{cmd}", cmd)
									.replace("{warp}", warp));
							e.setCancelled(true);
							return;
						}
				}
		}
	}

	@EventHandler
	private void salvarAreaWarp(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		String pp = p.getName().toLowerCase();
		Action a = e.getAction();
		ItemStack item = p.getItemInHand();
		if (p.hasPermission(m.getTexto("Warp.Setar.Permissao")) || p.hasPermission("kwwarp.*"))
			if (m.getVariaveis().getPos().containsKey(pp)) {
				String id = m.getTexto("Warp.Setar.Item.ID");
				String separar[] = id.split(":");
				ItemStack is;
				if (separar.length > 1) {
					is = new ItemStack(Integer.parseInt(separar[0]), 1, (short) Integer.parseInt(separar[1]));
				} else {
					is = new ItemStack(Integer.parseInt(separar[0]), 1);
				}
				if (item.getTypeId() == is.getTypeId()) {
					if (a == Action.LEFT_CLICK_BLOCK) {
						Posicao pos = m.getVariaveis().getPos().get(pp);
						pos.setPos(e.getClickedBlock().getLocation(), 1);
						m.getVariaveis().getPos().put(pp, pos);
						p.sendMessage(m.getCores("Warp.Setar.Pos.Setado.Um"));
						e.setCancelled(true);
						return;
					}
					if (a == Action.RIGHT_CLICK_BLOCK) {
						Posicao pos = m.getVariaveis().getPos().get(pp);
						pos.setPos(e.getClickedBlock().getLocation(), 2);
						p.sendMessage(m.getCores("Warp.Setar.Pos.Setado.Dois"));
						m.getVariaveis().getPos().put(pp, pos);
						e.setCancelled(true);
						return;
					}
				}
			}
	}

	@EventHandler
	private void aoSerKickado(PlayerKickEvent e) {
		Player p = e.getPlayer();
		String pp = p.getName().toLowerCase();
		if (m.getVariaveis().getTask().containsKey(pp)) {
			bs.cancelTask(m.getVariaveis().getTask().get(pp));
			m.getVariaveis().getTask().remove(pp);
			m.getVariaveis().getPlayers().remove(p);
			if (n_warp.containsKey(pp))
				if (!m.getVariaveis().getAreaPlayer().isEmpty()) {
					String warp = n_warp.get(pp);
					List<Player> l = new ArrayList<>();
					if (m.getVariaveis().getAreaPlayer().containsKey(warp))
						l = m.getVariaveis().getAreaPlayer().get(warp);
					if (l.size() == 1) {
						m.getVariaveis().getAreaPlayer().remove(warp);
						return;
					}
					l.remove(p);
					m.getVariaveis().getAreaPlayer().put(warp, l);
				}
		}
	}

	@EventHandler
	private void quandoDesconectar(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		String pp = p.getName();
		if (m.getVariaveis().getTask().containsKey(pp)) {
			bs.cancelTask(m.getVariaveis().getTask().get(pp));
			m.getVariaveis().getTask().remove(pp);
			m.getVariaveis().getPlayers().remove(p);
			if (n_warp.containsKey(pp))
				if (!m.getVariaveis().getAreaPlayer().isEmpty()) {
					String warp = n_warp.get(pp);
					List<Player> l = new ArrayList<>();
					if (m.getVariaveis().getAreaPlayer().containsKey(warp))
						l = m.getVariaveis().getAreaPlayer().get(warp);
					if (l.size() == 1) {
						m.getVariaveis().getAreaPlayer().remove(warp);
						return;
					}
					l.remove(p);
					m.getVariaveis().getAreaPlayer().put(warp, l);
				}
		}
	}

}
