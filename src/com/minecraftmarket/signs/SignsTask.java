package com.minecraftmarket.signs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.minecraftmarket.Market;

public class SignsTask extends BukkitRunnable implements Listener{
	public Market plugin = new Market();

	public SignsTask(Market market) {
		this.plugin = market;
	}

	@Override
	public void run() {
		Signs sign = new Signs();
		sign.callUpdate(plugin);
		
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		if (event.getLine(0).equalsIgnoreCase("[Recent]")){
			Signs sign = new Signs();
			try{
				int id = Integer.parseInt(event.getLine(1));
				Location loc = event.getBlock().getLocation();
				event.getPlayer().sendMessage(sign.chat.prefix+ChatColor.GREEN + "Sign created and updating...");
				sign.saveLocation(loc, id);
				//sign.callUpdate(plugin);
			}catch (NumberFormatException ex){
				event.getBlock().breakNaturally();
				event.getPlayer().sendMessage(sign.chat.prefix+ ChatColor.DARK_RED +"Wrong sign format");
		}
	}
	}
	
	

}
