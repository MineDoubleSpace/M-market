package com.minecraftmarket.mcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.minecraftmarket.Market;
import com.minecraftmarket.signs.SignUpdate;
import com.minecraftmarket.util.Chat;

public class Signs extends MarketCommand {

	public Signs() {
		super("signs", "Update recent donor signs", "", "minecraftmarket.admin");
	}

	@Override
	public void run(CommandSender sender, String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(Market.getPlugin(), new SignUpdate());
		sender.sendMessage(Chat.get().prefix + getMsg("messages.signs"));
		return;
	}

}
