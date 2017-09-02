package me.kewi;

import org.bukkit.plugin.java.JavaPlugin;

import kwwarp.metodos.Metodos;

public class KwWarp extends JavaPlugin {

	private Metodos m;

	@Override
	public void onEnable() {
		this.m = new Metodos(this);
		m.iniciarPlugin(true);
	}

	@Override
	public void onDisable() {
		m.iniciarPlugin(false);
	}

	public Metodos getMetodos() {
		return m;
	}

}
