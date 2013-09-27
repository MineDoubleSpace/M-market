package com.minecraftmarket;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {

	private Market plugin;

	public Commands(Market plugin) {
        if (plugin == null)
            throw new IllegalArgumentException("plugin cannot be null");
        if (!plugin.isInitialized())
            throw new IllegalArgumentException("plugin must be initiaized");
        this.plugin = plugin;
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.isOp()) {
			return false;
		}
		if (command.getName().equalsIgnoreCase("mmreload")) {
			plugin.reload();
			sender.sendMessage("[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] Configuration reloaded.");
			return true;
		}
		else if (command.getName().equalsIgnoreCase("mmcheck")) {
			new CommandChecker(plugin).runTaskAsynchronously(plugin);
			sender.sendMessage("[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] Checking for new purchases...");
			return true;
		}
		return false;
	}

}

