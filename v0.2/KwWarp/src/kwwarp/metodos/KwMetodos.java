package kwwarp.metodos;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import kwwarp.comandos.KwComando;
import kwwarp.eventos.KwEventos;
import me.kewi.KwWarp;

public class KwMetodos {

	private static String prefix = "§b[KwWarp] §3";
	private static KwWarp pl = KwWarp.getPlugin();
	private static Server s = pl.getServer();
	private static ConsoleCommandSender ccs = s.getConsoleSender();
	private static FileConfiguration cnf = pl.getConfig();
	private static KwMySQL sql;
	private static PluginManager pm = s.getPluginManager();
	private static ArrayList<String> dly = new ArrayList<>();

	private static void saveMySQL() {
		String mysql, host, database, user, pass;
		mysql = "Warp.MySQL.";
		host = getCnf(mysql + "IP");
		database = getCnf(mysql + "Database");
		user = getCnf(mysql + "Usuario");
		pass = getCnf(mysql + "Senha");
		if (getBoolean(mysql + "Ativar")) {
			sql = KwMySQL.load(host, database, user, pass);
			sql.update(
					"CREATE TABLE IF NOT EXISTS `kw_warp` (`nome` varchar(255) not null, `mundo` varchar(255) not null,`x` DOUBLE,`y` DOUBLE,`z` DOUBLE,`pitch` FLOAT, `yaw` FLOAT);");
			getConsole("MySQL encontrado! Hook com §b<Database>");
		} else {
			sql = KwMySQL.load(new File(pl.getDataFolder(), "warps.db"));
			sql.update(
					"CREATE TABLE IF NOT EXISTS `kw_warp` (`nome` varchar(255) not null, `mundo` varchar(255) not null,`x` DOUBLE,`y` DOUBLE,`z` DOUBLE, `pitch` FLOAT, `yaw` FLOAT);");
		}
	}

	public static void getConsole(String ce) {
		ce = prefix + ce;
		ccs.sendMessage(ce);
	}

	private static void saveSite() {
		KwUpdate.getUpdate("KwWarp", cnf, "Warp.Check_Update");
		pm.registerEvents(new KwUpdate(), pl);
	}

	private static void saveMetodos() {
		if (!new File(pl.getDataFolder(), "config.yml").exists()) {
			pl.saveDefaultConfig();
		}
		saveMySQL();
		s.getPluginCommand("warp").setExecutor(new KwComando());
		s.getPluginCommand("setwarp").setExecutor(new KwComando());
		s.getPluginCommand("warps").setExecutor(new KwComando());
		s.getPluginCommand("delwarp").setExecutor(new KwComando());
		s.getPluginCommand("kwwarp").setExecutor(new KwComando());
		pm.registerEvents(new KwEventos(), pl);
	}

	public static void getMetodos() {
		getConsole("Registrando Eventos!");
		saveMetodos();
		getConsole("Eventos Registrados!");
		getConsole("Plugin Habilitado! §bV: " + pl.getDescription().getVersion());
		if (getBoolean("Warp.Check_Update")) {
			saveSite();
		}
	}

	public static boolean getBoolean(String b) {
		return cnf.getBoolean(b);
	}

	public static String getCnf(String s) {
		return cnf.getString(s);
	}

	public static void getErro(boolean cmd, boolean mysql, boolean file, String file_name) {
		if (cmd) {
			getConsole("Utilize este comando apenas in-game!");
		} else if (mysql) {
			getConsole("Erro ao se conectar ao banco de dados!");
		} else if (file) {
			getConsole("Erro ao criar/salvar o arquivo " + file_name);
		}
	}

	public static String replaced(String rd) {
		return rd.replaceAll("&", "§");
	}

	public static String getReplaced(String s) {
		return replaced(getCnf("Warp.Mensagens." + s));
	}

	public static boolean getWarp(String warp) {
		try {
			PreparedStatement ps = sql.getConnection().prepareStatement("SELECT * FROM `kw_warp` WHERE nome=?");
			ps.setString(1, warp);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString("nome").equalsIgnoreCase(warp)) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Location teleportWarp(String warp) {
		double x = 0, y = 0, z = 0;
		float pitch = 0, yaw = 0;
		World w = null;
		PreparedStatement ps = null;
		try {
			ps = sql.getConnection().prepareStatement("SELECT * FROM `kw_warp` WHERE nome=?");
			ps.setString(1, warp);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				w = s.getWorld(rs.getString("mundo"));
				x = Double.parseDouble(rs.getString("x"));
				y = Double.parseDouble(rs.getString("y"));
				z = Double.parseDouble(rs.getString("z"));
				pitch = Float.parseFloat(rs.getString("pitch"));
				yaw = Float.parseFloat(rs.getString("yaw"));
			}
		} catch (SQLException e) {
			try {
				ps.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		Location loc = new Location(w, x, y, z);
		loc.setPitch(pitch);
		loc.setYaw(yaw);
		return loc;
	}

	public static boolean getPerms(Player p, String warp) {
		return p.hasPermission("kwwarp.warp." + warp) || (p.hasPermission("kwwarp.admin"));
	}

	public static void setWarp(Player p, String warp) {
		Location loc = p.getLocation();
		double x, y, z;
		float pitch, yaw;
		x = loc.getX();
		y = loc.getY();
		z = loc.getZ();
		pitch = loc.getPitch();
		yaw = loc.getYaw();
		String w = loc.getWorld().getName();
		try {
			PreparedStatement ps = sql.getConnection().prepareStatement(
					"INSERT INTO `kw_warp` (`nome`, `mundo`, `x`, `y`, `z`, `pitch`, `yaw`) VALUES (?,?,?,?,?,?,?);");
			ps.setString(1, warp);
			ps.setString(2, w);
			ps.setString(3, Double.toString(x));
			ps.setString(4, Double.toString(y));
			ps.setString(5, Double.toString(z));
			ps.setString(6, Float.toString(pitch));
			ps.setString(7, Float.toString(yaw));
			ps.executeUpdate();
		} catch (SQLException e) {
		}
		p.sendMessage(KwMetodos.getReplaced("Warps.Setada").replace("{warp}", warp));
	}

	public static List<String> getWarps() {
		List<String> warps = new ArrayList<>();
		try {
			PreparedStatement ps = sql.getConnection().prepareStatement("SELECT * FROM `kw_warp`");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				warps.add(rs.getString("nome"));
			}
		} catch (SQLException e) {
		}
		return warps;
	}

	public static void delWarp(String warp) {
		try {
			PreparedStatement ps = sql.getConnection().prepareStatement("DELETE FROM `kw_warp` WHERE nome=?");
			ps.setString(1, warp);
			ps.executeUpdate();
		} catch (SQLException e) {
		}
	}

	public static ArrayList<String> getDelay() {
		return dly;
	}

}
