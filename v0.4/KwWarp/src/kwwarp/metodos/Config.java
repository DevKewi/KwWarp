package kwwarp.metodos;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.kewi.KwWarp;

public class Config {

	private KwWarp pl;
	private File f;
	private FileConfiguration fc;

	public Config(Metodos m) {
		pl = m.getPlugin();
		f = new File(getPasta(), "warps.yml");
		fc = YamlConfiguration.loadConfiguration(f);
	}

	public File getPasta() {
		File f = pl.getDataFolder();
		if (!f.exists())
			f.mkdirs();
		return f;
	}

	public File getArquivo() {
		File f = new File(getPasta(), "config.yml");
		if (!f.exists())
			pl.saveDefaultConfig();
		return f;
	}

	public FileConfiguration getConfig() {
		return YamlConfiguration.loadConfiguration(getArquivo());
	}

	public void atualizarConfig() {
		try {
			getConfig().save(getArquivo());
			getConfig().load(getArquivo());
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public File getWarp() {
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return f;
	}

	public FileConfiguration getWarps() {
		return fc;
	}

	public void atualizarWarps() {
		try {
			getWarps().save(getWarp());
			getWarps().load(getWarp());
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

}
