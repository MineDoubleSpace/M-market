package com.minecraftmarket.managers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandManager {
	
	
	
	public static void sendHelp(CommandSender sender){
		sender.sendMessage("");
		// send help
	}
	
	public static void sendMessage(CommandSender sender,String msg){
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[&2MinecraftMarket&f] " + msg));
	}

}
