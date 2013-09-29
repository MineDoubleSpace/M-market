package com.minecraftmarket.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.minecraftmarket.Market;
import com.minecraftmarket.listeners.CommandChecker;
import com.minecraftmarket.managers.CommandManager;

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
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.isOp()) {
			return false;
		}
		if (cmd.getName().equalsIgnoreCase("mm")){
			if (args.length == 0){
				CommandManager.sendHelp(sender);
				return true;
			}
			
			
			if (args[0].equalsIgnoreCase("reload")){
				plugin.reload();
				sender.sendMessage("[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] Configuration reloaded.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("check")){
				new CommandChecker(plugin).runTaskAsynchronously(plugin);
				sender.sendMessage("[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] Checking for new purchases...");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("apikey")){
				ApiKeyCommand.ApiKeyAuth(plugin, args, sender);
				return true;
			}
		
		}
		return true;
	}
}
