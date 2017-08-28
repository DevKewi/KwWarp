package me.kewi;

import org.bukkit.plugin.java.JavaPlugin;

import kwwarp.metodos.KwMetodos;

public class KwWarp extends JavaPlugin {

	private static KwWarp pl;

	@Override
	public void onEnable() {
		pl = this;
		KwMetodos.getMetodos();
	}

	public static KwWarp getPlugin() {
		return pl;
	}
}
