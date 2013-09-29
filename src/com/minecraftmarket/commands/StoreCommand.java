package com.minecraftmarket.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minecraftmarket.Market;
import com.minecraftmarket.gui.Gui;

public class StoreCommand implements CommandExecutor{
	
	Market plugin;
	
	public StoreCommand(Market instance) {
		instance = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("shop")){
			if (!plugin.isGUIEnabled){
			return true;
		}
		if (sender instanceof Player){
			Gui.getInatance().showGui((Player) sender);
			return true;
		}
	}
	return true;
	}

}
