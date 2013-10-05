package com.minecraftmarket.signs2;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.minecraftmarket.Market;

public class SignChecker implements Listener{
	
	Market plugin;
	
	public SignChecker(Market instance) {
		plugin = instance;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event){
		String[] lines = event.getLines();
		int num = 0;
		if (event.getPlayer().isOp()){
			if (lines[0].equalsIgnoreCase("[RDonor]")){
				
				try{
					num = Integer.parseInt(lines[1]);
				} catch (Exception e){
					event.getBlock().breakNaturally();
					return;
				}
				
				if (num > 30){
					event.getBlock().breakNaturally();
					return;
				}
				int max = SignManager.getInstance().SignInfo.size();
				if (num > max){
					event.setLine(0, ChatColor.DARK_RED + "Donor not");
					event.setLine(1, ChatColor.DARK_RED + "found");
					event.setLine(2, "");
					event.setLine(3, "");
					return;
				}
								/*String info = SignManager.getInstance().SignInfo.get(num-1);
								event.setLine(0, ChatColor.UNDERLINE +"Recent Donor");
								String[] SignInfo = info.split(";");
								event.setLine(1, SignInfo[1]);
								event.setLine(2, SignInfo[2]);
								event.setLine(3, SignInfo[3]);*/
								String loc = num+","+event.getBlock().getLocation().getWorld().getName()+","+
								event.getBlock().getLocation().getBlockX()+","+
								event.getBlock().getLocation().getBlockY()+","
								+event.getBlock().getLocation().getBlockZ();
								Bukkit.broadcastMessage(loc);
				SignManager.getInstance().SignLoc.add(loc);  
				SignManager.getInstance().getSign().set("locations", SignManager.getInstance().SignLoc);
				SignManager.getInstance().saveSign();
			}
		}
		
	}

}
