package com.minecraftmarket.gui;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.minecraftmarket.util.Chat;

public class GuiListener implements Listener {

	Gui gui = Gui.getInstance();

	Chat chat = Chat.get();

	public String getMsg(String string) {
		return Chat.get().getLanguage().getString(string);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		try {
			if (event.getInventory().getName().contains("Category: ") || event.getInventory().getName().equals("Categories")) {
				event.setCancelled(true);
				if (event.getCurrentItem() == null) {
					event.setCancelled(true);
					return;
				}
				if (event.getCurrentItem().getItemMeta() == null) {
					event.setCancelled(true);
					return;
				}
			}
			if (event.getInventory().getName().contains("Category: ")) {
				Player player = (Player) event.getWhoClicked();
				String name = event.getCurrentItem().getItemMeta().getDisplayName();
				if (name.contains("Back to categories")) {
					event.setCancelled(true);
					event.getWhoClicked().closeInventory();
					gui.showCategories((Player) event.getWhoClicked());
					return;
				}
				String[] id = name.split(": ");
				int id1 = Integer.parseInt(id[1]);
				String url = GuiPackage.getById(id1).getUrl();
				player.sendMessage(chat.prefix + ChatColor.GOLD + getMsg("shop.item-url") + ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + ChatColor.UNDERLINE + url);
				event.getWhoClicked().closeInventory();
				event.setCancelled(true);
				return;
			}
			if (event.getInventory().getName().equals("Categories")) {
				int num = GuiCategory.getCategoryBySlot(event.getSlot()).getID();
				event.setCancelled(true);
				event.getWhoClicked().closeInventory();
				gui.showGui((Player) event.getWhoClicked(), num);
			}
		} catch (Exception e1) {

		}
	}

}
