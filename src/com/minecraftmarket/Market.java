package com.minecraftmarket;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.minecraftmarket.board.BoardTask;
import com.minecraftmarket.gui.Gui;
import com.minecraftmarket.gui.GuiListener;
import com.minecraftmarket.signs.SignsTask;

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
	public String shopCommand;
	public boolean canUpdate = true;

	public String getDataFolderPath() {
		return getDataFolder().getAbsolutePath() + File.separatorChar;
	}

	@Override
	public void onDisable() {
		// checkerTask.cancel();
		getServer().getScheduler().cancelTasks(this);
		getLogger().info("Plugin disabled");
	}

	@Override
	public void onEnable() {
		commands = new Commands(this);
		getCommand("mm").setExecutor(commands);
		saveDefaultConfig();
		ChatManager.getInstance().SetupDefaultLanguage(this);

		getServer().getPluginManager().registerEvents(new GuiListener(this),this);
		getServer().getPluginManager().registerEvents(new Commands(this), this);
		getServer().getPluginManager().registerEvents(new BoardTask(this), this);
		getServer().getPluginManager().registerEvents(new SignsTask(this), this);
		reload();
		
		//Plugin metric start
		try {
			PluginMetric metrics = new PluginMetric(this);
			metrics.start();
		} catch (IOException e) {}
		
		//Auto update check
		if (this.canUpdate) {
		new Updater(this, "minecraft-market-free-donation",this.getFile(), Updater.UpdateType.DEFAULT, true);
		}

		checkerTask = new CommandChecker(this).runTaskTimerAsynchronously(this,	600L, interval * 20L);
		new BoardTask(this).runTaskTimerAsynchronously(this,600L, interval * 20L);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new SignsTask(this), 20L, 20L);
		//new SignsTask(this).runTaskTimersynchronously(this, 60L, 100L);
		getLogger().info("Plugin enabled");
	}
		


	public void reload() {
		ChatManager.getInstance().reloadLanguage();
		reloadConfig();
		config = this.getConfig();
		version = this.getDescription().getVersion();
		ApiKey = config.getString("ApiKey");
		debug = config.getBoolean("Debug", false);
		interval = config.getLong("Interval", 90L);
		interval = Math.max(interval, 30L);
		isGUIEnabled = config.getBoolean("Enabled-GUI");
		shopCommand = config.getString("Shop-Command");
		canUpdate = config.getBoolean("auto-update");
		if (shopCommand == null) {
			shopCommand = "/shop";
		}

		getLogger().info("Using interval: " + interval);
		if (ApiKey.matches("[0-9a-f]+") && ApiKey.length() == 32) {
			getLogger().info("Using API Key: " + ApiKey);
		} else {
			getLogger()
					.info("Invalid API Key! Please set the correct key in config.yml and use /mm reload to reload your settings");
		}
		if (isGUIEnabled) {
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

	public void saveFiles() {
		File cfile = new File(getDataFolder(), "config.yml");
		try {
			config.save(cfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger()
					.severe(ChatColor.DARK_RED + "Could not save config.yml");
		}
	}

}
