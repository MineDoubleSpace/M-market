package com.minecraftmarket.gui;

import org.bukkit.scheduler.BukkitRunnable;

public class GuiTask extends BukkitRunnable {

	public void run() {
		Gui.getInstance().setupGUI();
	}

}
