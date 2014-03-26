package com.minecraftmarket;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.minecraftmarket.gui.Gui;

public class ShopCommand implements Listener {

	@EventHandler
	public void onStoreCommand(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().equalsIgnoreCase(Market.getPlugin().getShopCommand())) {
			event.setCancelled(true);
			if (!Market.getPlugin().isGuiEnabled()) {
				return;
			} else {
				Gui.getInstance().showCategories(event.getPlayer());
				return;
			}
		}
	}
}
