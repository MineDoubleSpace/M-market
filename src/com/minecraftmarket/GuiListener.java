package com.minecraftmarket;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiListener implements Listener{
	
	public Market plugin;
	
	public GuiListener(Market instance) {
		instance = plugin;
	}
	
	Gui gui = Gui.getInatance();

	@EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
		try{
		if (e.getCurrentItem() == null){
			return;
			}
		if (e.getCurrentItem().getItemMeta() == null){
			return;
			}
        if (e.getInventory().getName().contains("Category: ")){
        	Player player = (Player) e.getWhoClicked();
        	String name = e.getCurrentItem().getItemMeta().getDisplayName();
        	if (name.contains("Back to categories")){
        		e.setCancelled(true);
            	e.getWhoClicked().closeInventory();
            	e.getWhoClicked().openInventory(gui.inventory.get(0));
            	return;
        	}
        	String[] id = name.split(": ");
        	int id1 = Integer.parseInt(id[1]);
        	String url = gui.urlMap.get(id1);
        	player.sendMessage(ChatColor.RESET + "[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] "
        						+ ChatColor.GOLD + "Item URL: " + ChatColor.AQUA + ""
        						+ ChatColor.ITALIC + ChatColor.UNDERLINE + url);
        	e.getWhoClicked().closeInventory();
        	e.setCancelled(true);
        	return;
        }
        if (e.getInventory().getName().equals("Categories")){
        	int num = gui.IdMap.get(e.getSlot());
        	e.setCancelled(true);
        	e.getWhoClicked().closeInventory();
        	e.getWhoClicked().openInventory(gui.inventory.get(num));
        	}
		}catch (Exception e1){
			if (plugin.debug){
				e1.printStackTrace();
			}
		}
	}

}
