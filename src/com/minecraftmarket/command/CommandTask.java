package com.minecraftmarket.command;

import org.bukkit.scheduler.BukkitRunnable;

import com.minecraftmarket.Market;

public class CommandTask extends BukkitRunnable {

	Market plugin = Market.getPlugin();

	@Override
	public void run() {
		FetchCommand fetchCommand = new FetchCommand();
		fetchCommand.fetchPending();
		CommandExecutor.executeAllPending();
		CommandExecutor.clean();
	}

	public static void check() {
		new CommandTask().runTaskAsynchronously(Market.getPlugin());
	}
}