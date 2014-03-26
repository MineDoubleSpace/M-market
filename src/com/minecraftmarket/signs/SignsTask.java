package com.minecraftmarket.signs;

import org.bukkit.scheduler.BukkitTask;

import com.minecraftmarket.Market;

public class SignsTask {

	private BukkitTask task;

	public Market plugin = Market.getPlugin();

	public void startSignTask() {
		Signs.getSigns().updateSignLocation();
		if (task != null) task.cancel();
		task = new AsyncSignUpdate().runTaskTimer(plugin, 600L, plugin.getInterval() * 20);
	}

}
