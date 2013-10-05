package com.minecraftmarket.signs2;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.minecraftmarket.Market;
import com.minecraftmarket.managers.JsonManager;

public class SignUpdater {
	
	public void signChecker(final Market plugin){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				int max;
				String gui;
				try {
					gui = JsonManager.getJSON("http://www.minecraftmarket.com/api/" + plugin.ApiKey + "/recentdonor", plugin);
				if (plugin.debug) {
					plugin.getLogger().info("Response: " + gui);
				}
				JSONObject json = new JSONObject(gui);
				JSONArray jsonresult = json.optJSONArray("result");
				if (jsonresult != null) {
					max = jsonresult.length();
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			}
		}, 20L, plugin.interval * 20L);
	}

	

}
