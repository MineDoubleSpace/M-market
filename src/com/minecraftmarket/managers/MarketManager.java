package com.minecraftmarket.managers;


import com.minecraftmarket.Market;
import com.minecraftmarket.commands.Commands;
import com.minecraftmarket.commands.StoreCommand;
import com.minecraftmarket.gui.Gui;
import com.minecraftmarket.gui.GuiListener;

public class MarketManager {
	
	
	
	public static void Reload(Market plugin){
		plugin.reloadConfig();
		plugin.config = plugin.getConfig();
		plugin.version = plugin.getDescription().getVersion();
		plugin.ApiKey = plugin.config.getString("ApiKey");
		plugin.debug = plugin.config.getBoolean("Debug", false);
		plugin.interval = plugin.config.getLong("interval", 90L);
		plugin.interval =  Math.max(plugin.interval, 30L);
		try{ 
			plugin.isGUIEnabled = plugin.config.getBoolean("Enabled-GUI");
		}catch (Exception e){
			if (plugin.debug){
				e.printStackTrace();
			}else{
				plugin.getLogger().info("Error on config file! Did you update it??");
			}
		}
		
		plugin.getLogger().info("Using interval: " + plugin.interval);
		if (plugin.ApiKey.matches("[0-9a-f]+") && plugin.ApiKey.length() == 32) {
			plugin.getLogger().info("Using API Key: " + plugin.ApiKey);
		}
		else {
			plugin.getLogger().info("Invalid API Key! Please set the correct key in config.yml and use /mmreload to reload your settings");
		}
		Gui.getInatance().setupGUI(plugin);
		
	}
	
	
	
	public static void Enable(Market plugin){
		plugin.getCommand("mm").setExecutor(new Commands(plugin));
		plugin.getCommand("store").setExecutor(new StoreCommand(plugin));
		
		plugin.getServer().getPluginManager().registerEvents(new GuiListener(plugin), plugin);
		
		plugin.saveDefaultConfig();
		plugin.reload();
		
		plugin.getLogger().info("Plugin enabled");
	}

}
