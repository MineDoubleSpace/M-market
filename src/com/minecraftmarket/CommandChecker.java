package com.minecraftmarket;

import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;
import org.json.JSONArray;

public class CommandChecker extends BukkitRunnable {

	private Market plugin;
	public CommandChecker(Market plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		String pending = "";
		String expiry = "";
		try {
			pending = JsonManager.getJSON("http://www.minecraftmarket.com/api/" + plugin.ApiKey + "/pending");
			expiry = JsonManager.getJSON("http://www.minecraftmarket.com/api/" + plugin.ApiKey + "/expiry");
			if (plugin.debug) {
				plugin.getLogger().info("Pending Response: " + pending);
				plugin.getLogger().info("Expiry Response: " + expiry);
			}
			JSONArray jsonresult  = new JSONObject(pending).optJSONArray("result");
			JSONArray expiryArray  = new JSONObject(expiry).optJSONArray("result");
			if (jsonresult != null) {
				for (int i = 0; i < jsonresult.length(); i++) {
					String username = jsonresult.getJSONObject(i).getString("username");
					//int id = jsonresult.getJSONObject(i).getInt("id");
					JSONArray commands = jsonresult.getJSONObject(i).getJSONArray("commands");
					if (plugin.getServer().getPlayerExact(username) != null) {
							for (int c = 0; c < commands.length(); c++) {
									String cmd = commands.getJSONObject(c).getString("command");
									int cmdID = commands.getJSONObject(c).getInt("id");
									if (JsonManager.getJSON("http://www.minecraftmarket.com/api/" + plugin.ApiKey + "/executed/" + cmdID) != null) {
										if(!cmd.equals("")){
										plugin.getLogger().info("Executing \"/" + cmd + "\" on behalf of " + username);
										plugin.executeCommand(cmd);
									}
								}
							}
						//	plugin.getLogger().info("Executed commands for cartid: " + id);
						}
				}
			}if (expiryArray != null){
				for (int i = 0; i < expiryArray.length(); i++) {
					String username = expiryArray.getJSONObject(i).getString("username");
				//	int id = expiryArray.getJSONObject(i).getInt("id");
					JSONArray commands = expiryArray.getJSONObject(i).getJSONArray("commands");
					if (plugin.getServer().getPlayerExact(username) != null) {
							for (int c = 0; c < commands.length(); c++) {
									String cmd = commands.getJSONObject(c).getString("command");
									int cmdID = commands.getJSONObject(c).getInt("id");
									if (JsonManager.getJSON("http://www.minecraftmarket.com/api/" + plugin.ApiKey + "/executed/" + cmdID) != null) {
										plugin.getLogger().info("Executing \"/" + cmd + "\" on behalf of " + username);
										plugin.executeCommand(cmd);
								}
							}
						//	plugin.getLogger().info("Executed commands for cartid: " + id);
						}
			}
			}
			
			
		}catch (Exception e) {
			if (plugin.debug) {
				e.printStackTrace();
			}
		}
	}
}