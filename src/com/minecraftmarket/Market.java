package com.minecraftmarket;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecraftmarket.command.CommandTask;
import com.minecraftmarket.mcommands.Commands;
import com.minecraftmarket.recentgui.RecentListener;
import com.minecraftmarket.shop.ShopListener;
import com.minecraftmarket.shop.ShopTask;
import com.minecraftmarket.signs.SignListener;
import com.minecraftmarket.signs.SignsTask;
import com.minecraftmarket.util.Chat;
import com.minecraftmarket.util.Init;
import com.minecraftmarket.util.Log;
import com.minecraftmarket.util.Metric;
import com.minecraftmarket.util.Settings;
import com.minecraftmarket.util.Update;

public class Market extends JavaPlugin {
	private Long interval;
	private FileConfiguration config;
	private String shopCommand;
	private boolean update;
	private boolean isBoardEnabled;
	private boolean isSignEnabled;
	private boolean isGuiEnabled;
	private static Market instance;
	private CommandTask commandTask;

	@Override
	public void onDisable() {
		stopTasks();
	}

	@Override
	public void onEnable() {
		try {
			registerCommands();

			saveDefaultSettings();

			registerEvents();

			reload();

			startMetrics();

			startTasks();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reload() {

		Init.start();

		loadConfigOptions();
		
		checkUpdate();

		if (authApi()) {

			startGUI();

			startSignTasks();
		}

	}

	private void loadConfigOptions() {
		Chat.get().SetupDefaultLanguage();
		config = Settings.get().getConfig();
		Api.setApi(config.getString("ApiKey", "Apikey here"));
		this.interval = Math.max(config.getLong("Interval", 90L), 10L);
		this.isGuiEnabled = config.getBoolean("Enabled-GUI", true);
		this.shopCommand = config.getString("Shop-Command", "/shop");
		this.update = config.getBoolean("auto-update", true);
		this.isSignEnabled = config.getBoolean("Enabled-signs", true);
		Log.setDebugging(config.getBoolean("Debug", false));
	}

	private void registerEvents() {
		getServer().getPluginManager().registerEvents(new ShopListener(), this);
		getServer().getPluginManager().registerEvents(new ShopCommand(), this);
		getServer().getPluginManager().registerEvents(new SignListener(), this);
		getServer().getPluginManager().registerEvents(new RecentListener(), this);
	}

	private boolean authApi() {
		if (Api.authAPI(Api.getKey())) {
			getLogger().info("Using API Key: " + Api.getKey());
			return true;
		} else {
			getLogger().warning("Invalid API Key! Use \"/MM APIKEY <APIKEY>\" to setup your APIKEY");
			return false;
		}
	}

	private void startMetrics() {
		try {
			Metric metrics = new Metric(this);
			metrics.start();
		} catch (IOException e) {
			Log.log(e);
		}
	}

	private void startGUI() {
		if (isGuiEnabled) {
			new ShopTask().runTaskLater(this, 20L);

		}
	}

	private void runCommandChecker() {
		commandTask = new CommandTask();
		commandTask.runTaskTimerAsynchronously(this, 600L, interval * 20L);
	}

	private void startSignTasks() {
		if (isSignEnabled()) {
			new SignsTask().startSignTask();
		}
	}

	private void startTasks() {
		runCommandChecker();
	}

	private void registerCommands() {
		getCommand("mm").setExecutor(new Commands());
	}

	private void saveDefaultSettings() {
		Settings.get().LoadSettings();
	}

	private void checkUpdate() {
		if (update) {
			new Update();
		}
	}

	private void stopTasks() {
		getServer().getScheduler().cancelTasks(this);
		getLogger().info("Plugin disabled");
	}

	public static Market getPlugin() {
		if (instance == null) instance = new Market();
		return instance;
	}

	public Long getInterval() {
		return interval;
	}

	public void setInterval(Long interval) {
		this.interval = interval;
	}

	public void setConfig(FileConfiguration config) {
		this.config = config;
	}

	public boolean isGuiEnabled() {
		return isGuiEnabled;
	}

	public File getPluginFile() {
		return this.getFile();
	}

	public void setGuiEnabled(boolean guiEnabled) {
		this.isGuiEnabled = guiEnabled;
	}

	public String getShopCommand() {
		return shopCommand;
	}

	public void setShopCommand(String shopCommand) {
		this.shopCommand = shopCommand;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public Market() {
		instance = this;
	}

	public boolean isBoard() {
		return isBoardEnabled;
	}

	public void setIsBoard(boolean isBoard) {
		this.isBoardEnabled = isBoard;
	}

	public boolean isSignEnabled() {
		return isSignEnabled;
	}

	public void setSignEnabled(boolean isSignEnabled) {
		this.isSignEnabled = isSignEnabled;
	}

	public CommandTask getCommandTask() {
		return commandTask;
	}

}
