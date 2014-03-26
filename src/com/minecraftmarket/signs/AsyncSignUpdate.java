package com.minecraftmarket.signs;

import org.bukkit.scheduler.BukkitRunnable;

public class AsyncSignUpdate extends BukkitRunnable {

	@Override
	public void run() {
		Signs.getSigns().callUpdate();
	}

}