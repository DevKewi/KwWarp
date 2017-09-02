package kwwarp.metodos;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;

import kwwarp.comandos.KwComandos;
import kwwarp.eventos.KwEventos;
import me.kewi.KwWarp;

public class Metodos {

	private KwWarp pl;
	private Server s;
	private ConsoleCommandSender ccs;
	private PluginManager pm;
	private Variaveis v = new Variaveis();
	private Config c;
	private FileConfiguration fc;
	private Dados sql = new Dados();
	private Delay d;
	private Warp w;
	private Inventario inv;

	public Metodos(KwWarp pl) {
		this.pl = pl;
		s = pl.getServer();
		ccs = s.getConsoleSender();
		pm = s.getPluginManager();
		c = new Config(this);
		fc = c.getConfig();
		d = new Delay(this);
		w = new Warp(this);
		inv = new Inventario(this);
	}

	public KwWarp getPlugin() {
		return pl;
	}

	public Config getArquivos() {
		return c;
	}

	public FileConfiguration getConfig() {
		return fc;
	}

	public Variaveis getVariaveis() {
		return v;
	}

	public Dados getDados() {
		return sql;
	}

	public Delay getDelay() {
		return d;
	}

	public Warp getWarp() {
		return w;
	}

	public Inventario getInventario() {
		return inv;
	}

	public void enviarConsole(String mensagem) {
		mensagem = "§b[KwWarp] §3" + mensagem;
		ccs.sendMessage(mensagem);
	}

	public String getTexto(String config) {
		try {
			if (fc.getString(config) != null)
				return fc.getString(config);
		} catch (Exception e) {
		}
		return "Linha inexistente: " + config;
	}

	public String putCor(String cor) {
		return cor.replace("&", "§");
	}

	public String getCores(String config) {
		return putCor(getTexto(config));
	}

	public String getLista(String config) {
		StringBuilder sb = new StringBuilder();
		for (String s : fc.getStringList(config))
			sb.append(putCor(s) + "\n");
		return sb.toString();
	}

	public void iniciarPlugin(boolean ativar) {
		if (ativar) {
			enviarConsole("Registrando Eventos!");
			registrarMetodos();
			enviarConsole("Eventos Registrados!");
			enviarConsole("Plugin Habilitado!");
			return;
		}
	}

	private void registrarMetodos() {
		if (fc.getBoolean("Warp.Update"))
			if (getVersao().equalsIgnoreCase(pl.getDescription().getVersion())) {
				enviarConsole("Voce esta usufluindo da versao mais recente!");
			} else {
				enviarConsole("Ha uma nova versao! Voce esta usando a v" + pl.getDescription().getVersion());
			}
		registrarDados();
		registrarComandos();
		registrarEventos();
	}

	private void registrarDados() {
		if (fc.getBoolean("MySQL.Ativar")) {
			sql.load(getTexto("MySQL.Host"), getTexto("MySQL.Database"), getTexto("MySQL.Usuario"),
					getTexto("MySQL.Senha"));
			sql.update(
					"CREATE TABLE IF NOT EXISTS kw_warp(jogador VARCHAR(16) NOT NULL, warp VARCHAR(30) NOT NULL, vezes INT NOT NULL);");
		} else {
			File f = new File(c.getPasta(), "usuarios.db");
			sql.startConexao(f);
			sql.update(
					"CREATE TABLE IF NOT EXISTS kw_warp(jogador VARCHAR(16) NOT NULL, warp VARCHAR(30) NOT NULL, vezes INT NOT NULL);");
		}
	}

	private void registrarEventos() {
		pm.registerEvents(new KwEventos(this), pl);
	}

	private void registrarComandos() {
		s.getPluginCommand("warp").setExecutor(new KwComandos(this));
		s.getPluginCommand("warps").setExecutor(new KwComandos(this));
		s.getPluginCommand("setwarp").setExecutor(new KwComandos(this));
		s.getPluginCommand("delwarp").setExecutor(new KwComandos(this));
		s.getPluginCommand("kwwarp").setExecutor(new KwComandos(this));
	}

	public boolean temNumero(String texto) {
		try {
			Integer.parseInt(texto);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public boolean temWarp(String jogador, String warp) {
		try {
			PreparedStatement ps = sql.ps("SELECT * FROM kw_warp WHERE jogador=? AND warp=?;");
			ps.setString(1, jogador.toLowerCase());
			ps.setString(2, warp.toLowerCase());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void setVezes(String jogador, String warp, int vezes) {
		if (!temWarp(jogador, warp))
			try {
				PreparedStatement ps = sql.ps("INSERT INTO kw_warp (jogador, warp, vezes) VALUES (?,?,?);");
				ps.setString(1, jogador.toLowerCase());
				ps.setString(2, warp.toLowerCase());
				ps.setInt(3, vezes);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		try {
			PreparedStatement ps = sql.ps("UPDATE kw_warp SET vezes=? WHERE jogador=? AND warp=?;");
			ps.setInt(1, vezes);
			ps.setString(2, jogador.toLowerCase());
			ps.setString(3, warp.toLowerCase());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addVezes(String jogador, String warp, int vezes) {
		setVezes(jogador, warp, getVezes(jogador, warp) + 1);
	}

	public int getVezes(String jogador, String warp) {
		try {
			PreparedStatement ps = sql.ps("SELECT * FROM kw_warp WHERE jogador=? AND warp=?;");
			ps.setString(1, jogador.toLowerCase());
			ps.setString(2, warp.toLowerCase());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getInt("vezes");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getVersao() {
		String urlloc = "http://devkewi.esy.es/plugins/kwwarp/versao.txt";
		try {
			URL url = new URL(urlloc);
			URLConnection openConnection = url.openConnection();
			openConnection.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
			Scanner r = new Scanner(openConnection.getInputStream());
			StringBuilder sb = new StringBuilder();
			while (r.hasNext()) {
				sb.append(r.next());
			}
			r.close();
			return sb.toString();
		} catch (IOException localIOException) {
		}
		return null;
	}

}
