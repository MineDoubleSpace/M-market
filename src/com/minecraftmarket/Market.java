package com.minecraftmarket;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;





import com.minecraftmarket.commands.Commands;
import com.minecraftmarket.listeners.CommandChecker;
import com.minecraftmarket.listeners.Updater;
import com.minecraftmarket.managers.MarketManager;
import com.minecraftmarket.signs2.SignManager;


public class Market extends JavaPlugin {
	public String ApiKey = "API key here";
	public Long interval = 60L;
	public Commands commands;
	@SuppressWarnings("unused")
	private BukkitTask checkerTask;
	public FileConfiguration config;
	public boolean debug = false;
	public String version;
	public boolean isGUIEnabled;
	public boolean isAutoUpdate;

	public String getDataFolderPath() {
		return getDataFolder().getAbsolutePath() + File.separatorChar;
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		getLogger().info("Plugin disabled");
	}

	@Override
	public void onEnable() {
		MarketManager.Enable(this);
		
		if (isAutoUpdate){
		new Updater(this, "minecraft-market-free-donation", this.getFile(), Updater.UpdateType.DEFAULT, true);
		reload();
		}
		
		SignManager.getInstance().createSignFile(this);
		SignManager.getInstance().signChecker(this);
		
		checkerTask = new CommandChecker(this).runTaskTimerAsynchronously(this, 600L, interval * 20L);
		
		getLogger().info("Plugin enabled");
		
	}

	public void reload() {
		MarketManager.Reload(this);
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
