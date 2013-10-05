package com.minecraftmarket;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class CommandChecker extends BukkitRunnable {

	private Market plugin;
	public CommandChecker(Market plugin) {
		this.plugin = plugin;
	}
	
	public String getJSON(String url) throws JSONException {
	    try {
	        URL u = new URL(url);
	        HttpURLConnection c = (HttpURLConnection) u.openConnection();
	        c.setRequestMethod("GET");
	        c.setRequestProperty("Content-length", "0");
	        c.setRequestProperty("Accept", "application/json");
	        c.setUseCaches(false);
	        c.setAllowUserInteraction(false);
	        c.setConnectTimeout(10000);
	        c.setReadTimeout(10000);
	        c.connect();
	        int status = c.getResponseCode();
	        plugin.getLogger().info("Response Message: " + c.getResponseMessage());
	        switch (status) {
	            case 200:
	            case 201:
	            	Scanner s = new Scanner(c.getInputStream());
	            	s.useDelimiter("\\Z");
	            	return s.next();
	        }

	    } catch (MalformedURLException ex) {
	    } catch (IOException ex) {
	    }
	    return "";
	}
	
	@Override
	public void run() {
		String pending = "";
		try {
			pending = getJSON("http://www.minecraftmarket.com/api/" + plugin.ApiKey + "/pending");
			if (plugin.debug) {
				plugin.getLogger().info("Response: " + pending);
			}
			JSONObject json = new JSONObject(pending);
			JSONArray jsonresult = json.optJSONArray("result");
			if (jsonresult != null) {
				for (int i = 0; i < jsonresult.length(); i++) {
					String username = jsonresult.getJSONObject(i).getString("username");
					int id = jsonresult.getJSONObject(i).getInt("id");
					JSONArray commands = jsonresult.getJSONObject(i).getJSONArray("commands");
	       			//plugin.getLogger().info("username: " + username + " id: " + id + " cmd: " + commands.toString());
					if (plugin.getServer().getPlayerExact(username) != null) {
						if (getJSON("http://www.minecraftmarket.com/api/" + plugin.ApiKey + "/executed/" + id) != null) {
							for (int c = 0; c < commands.length(); c++) {
								String[] cmds = commands.getString(c).split(";");
								for (String cmd : cmds) {
									plugin.getLogger().info("Executing \"/" + cmd + "\" on behalf of " + username);
									plugin.executeCommand(cmd);
								}
							}
							plugin.getLogger().info("Executed commands for cartid: " + id);
						}
					}
				}
			}
		}
		catch (Exception e) {
			if (plugin.debug) {
				e.printStackTrace();
			}
		}
	}
	
	

}
