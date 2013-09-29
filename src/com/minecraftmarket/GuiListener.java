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

	@EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
		try{
		if (e.getCurrentItem().getItemMeta() == null){
			return;
			}
        if (e.getInventory().getName().contains("Minecraft Market")){
        	Player player = (Player) e.getWhoClicked();
        	String name = e.getCurrentItem().getItemMeta().getDisplayName();
        	String[] id = name.split(": ");
        	int id1 = Integer.parseInt(id[1]);
        	String url = Gui.getInatance().urlMap.get(id1);
        	player.sendMessage(ChatColor.RESET + "[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] "
        						+ ChatColor.GOLD + "Item URL: " + ChatColor.AQUA + ""
        						+ ChatColor.ITALIC + ChatColor.UNDERLINE + url);
        	e.getWhoClicked().closeInventory();
        	e.setCancelled(true);
        	return;
        }
        }catch (Exception e1){}
	}

}
