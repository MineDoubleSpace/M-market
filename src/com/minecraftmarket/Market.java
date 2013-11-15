package com.minecraftmarket;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.minecraftmarket.board.BoardTask;
import com.minecraftmarket.gui.Gui;
import com.minecraftmarket.gui.GuiListener;
import com.minecraftmarket.manager.ChatManager;
import com.minecraftmarket.manager.PluginMetric;
import com.minecraftmarket.manager.UpdateManager;

public class Market extends JavaPlugin {
	public String ApiKey = "API key here";
	public Long interval = 60L;
	public BukkitTask checkerTask;
	public FileConfiguration config;
	public boolean debug = false;
	public boolean isGUIEnabled;
	public String shopCommand;
	public boolean canUpdate = true;
	private static Market instance;

	@Override
	public void onDisable() {
		stopTasks();

	}

	@Override
	public void onEnable() {
		registerCommands();

		saveDefaultConfig();

		saveDefaultLanuage();

		registerEvents();

		reload();

		startMetrics();

		startUpdate();

		startTasks();

		getLogger().info("Plugin enabled");
	}

	public void reload() {

		loadConfigOptions();

		authApi();

		startGUI(this);

	}

	public void executeCommand(final String cmd) {
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				getServer().dispatchCommand(getServer().getConsoleSender(), cmd);
			}
		});
	}

	private void loadConfigOptions() {
		reloadConfig();
		this.config = this.getConfig();
		this.ApiKey = config.getString("ApiKey");
		this.debug = config.getBoolean("Debug", false);
		this.interval = config.getLong("Interval", 90L);
		this.interval = Math.max(interval, 30L);
		this.isGUIEnabled = config.getBoolean("Enabled-GUI", true);
		this.shopCommand = config.getString("Shop-Command", "/shop");
		this.canUpdate = config.getBoolean("auto-update", true);
	}

	private void registerEvents() {
		getServer().getPluginManager().registerEvents(new GuiListener(this), this);
		getServer().getPluginManager().registerEvents(new Commands(this), this);
		getServer().getPluginManager().registerEvents(new BoardTask(this), this);
	}

	private void authApi() {
		if (ApiKey.matches("[0-9a-f]+") && ApiKey.length() == 32) {
			getLogger().info("Using API Key: " + ApiKey);
			getInstance().ApiKey = this.ApiKey;
			getInstance().debug = this.debug;
		} else {
			getLogger().warning("Invalid API Key! Use \"/MM APIKEY <APIKEY>\" to setup your APIKEY");
		}
	}

	private void startMetrics() {
		try {
			PluginMetric metrics = new PluginMetric(this);
			metrics.start();
		} catch (IOException e) {
			if (debug) {
				getLogger().warning("Error starting Plugin Metrics");
			}
		}
	}

	private void startUpdate() {
		if (this.canUpdate) {
			new UpdateManager(this, "minecraft-market-free-donation", this.getFile(), UpdateManager.UpdateType.DEFAULT, true);
		} else {
			if (debug)
				getLogger().warning("Auto Update disabled!");
		}
	}

	private void startGUI(final Market plugin) {
		if (isGUIEnabled) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					Gui.getInatance().setupGUI(plugin);
				}
			}, 20L);

		}
	}

	private void runCommandChecker() {
		checkerTask = new CommandChecker(this).runTaskTimerAsynchronously(this, 600L, interval * 20L);
	}

	private void runBoards() {
		new BoardTask(this).runTaskTimerAsynchronously(this, 600L, interval * 20L);
	}

	private void startTasks() {
		runCommandChecker();
		runBoards();
	}

	private void registerCommands() {
		getCommand("mm").setExecutor(new Commands(this));
	}

	private void saveDefaultLanuage() {
		ChatManager.getInstance().SetupDefaultLanguage(this);
	}

	public BukkitTask getCheckerTask() {
		return checkerTask;
	}

	private void stopTasks() {
		getServer().getScheduler().cancelTasks(this);
		getLogger().info("Plugin disabled");
	}

	public static Market getInstance() {
		if (instance == null)
			instance = new Market();
		return instance;
	}

}
