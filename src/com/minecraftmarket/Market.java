package com.minecraftmarket;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public class Market extends JavaPlugin {
	public String ApiKey = "API key here";
	public Long interval = 60L;
	private Commands commands;
	@SuppressWarnings("unused")
	private BukkitTask checkerTask;
	public FileConfiguration config;
	public boolean debug = false;
	public String version;
	public boolean isGUIEnabled;

	public String getDataFolderPath() {
		return getDataFolder().getAbsolutePath() + File.separatorChar;
	}

	@Override
	public void onDisable() {
		//checkerTask.cancel();
		getServer().getScheduler().cancelTasks(this);
		getLogger().info("Plugin disabled");
	}

	@Override
	public void onEnable() {
		commands = new Commands(this);
		getCommand("shop").setExecutor(commands);
		getCommand("mm").setExecutor(commands);
		
		getServer().getPluginManager().registerEvents(new GuiListener(this), this);
		
		saveDefaultConfig();
		reload();
		
		checkerTask = new CommandChecker(this).runTaskTimerAsynchronously(this, 600L, interval * 20L);
		
		getLogger().info("Plugin enabled");
		
	}

	public void reload() {
		reloadConfig();
		config = this.getConfig();
		version = this.getDescription().getVersion();
		ApiKey = config.getString("ApiKey");
		debug = config.getBoolean("Debug", false);
		interval = config.getLong("interval", 90L);
		interval =  Math.max(interval, 30L);
		isGUIEnabled = config.getBoolean("Enabled-GUI");
		
		getLogger().info("Using interval: " + interval);
		if (ApiKey.matches("[0-9a-f]+") && ApiKey.length() == 32) {
			getLogger().info("Using API Key: " + ApiKey);
		}
		else {
			getLogger().info("Invalid API Key! Please set the correct key in config.yml and use /mmreload to reload your settings");
		}
		if (isGUIEnabled){
		Gui.getInatance().setupGUI(this);
		}
	}
	
	
	public void executeCommand(final String cmd) {
	    getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				getServer().dispatchCommand(getServer().getConsoleSender(), cmd);
			}
		});
	}
	
}
