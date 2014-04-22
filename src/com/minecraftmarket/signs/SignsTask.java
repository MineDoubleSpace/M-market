package com.minecraftmarket.signs;

import org.bukkit.scheduler.BukkitTask;

import com.minecraftmarket.Market;

public class SignsTask {

	private static BukkitTask task;

	public void startSignTask() {
		Signs.getSigns().updateJson();
		Signs.getSigns().setup();
		if (task != null) task.cancel();
		task = new SignUpdate().runTaskTimer(Market.getPlugin(), 600L, Market.getPlugin().getInterval() * 20);
	}
	
	public void cancel() {
		task.cancel();
	}

}
