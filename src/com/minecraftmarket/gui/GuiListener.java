package com.minecraftmarket.gui;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.minecraftmarket.Market;
import com.minecraftmarket.manager.ChatManager;

public class GuiListener implements Listener {

	private Market plugin;

	public GuiListener(Market plugin) {
		if (plugin == null)
			throw new IllegalArgumentException("plugin cannot be null");
		if (!plugin.isInitialized())
			throw new IllegalArgumentException("plugin must be initiaized");
		this.plugin = plugin;
	}

	Gui gui = Gui.getInatance();

	ChatManager chat = ChatManager.getInstance();

	public String getMsg(String string) {
		return ChatManager.getInstance().getLanguage().getString(string);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		try {
			if (e.getInventory().getName().contains("Category: ") || e.getInventory().getName().equals("Categories")) {
				e.setCancelled(true);
				if (e.getCurrentItem() == null) {
					e.setCancelled(true);
					return;
				}
				if (e.getCurrentItem().getItemMeta() == null) {
					e.setCancelled(true);
					return;
				}
			}
			if (e.getInventory().getName().contains("Category: ")) {
				Player player = (Player) e.getWhoClicked();
				String name = e.getCurrentItem().getItemMeta().getDisplayName();
				if (name.contains("Back to categories")) {
					e.setCancelled(true);
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().openInventory(gui.inventory.get(0));
					return;
				}
				String[] id = name.split(": ");
				int id1 = Integer.parseInt(id[1]);
				String url = gui.urlMap.get(id1);
				player.sendMessage(chat.prefix + ChatColor.GOLD + getMsg("shop.item-url") + ChatColor.AQUA + "" + ChatColor.ITALIC + ChatColor.UNDERLINE + url);
				e.getWhoClicked().closeInventory();
				e.setCancelled(true);
				return;
			}
			if (e.getInventory().getName().equals("Categories")) {
				int num = gui.IdMap.get(e.getSlot());
				e.setCancelled(true);
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().openInventory(gui.inventory.get(num));
			}
		} catch (Exception e1) {
			if (plugin.debug) {
				e1.printStackTrace();
			}
		}
	}

}
