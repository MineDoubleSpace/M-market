package com.minecraftmarket;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {

	private Market plugin;

	public Commands(Market plugin) {
        if (plugin == null)
            throw new IllegalArgumentException("plugin cannot be null");
        if (!plugin.isInitialized())
            throw new IllegalArgumentException("plugin must be initiaized");
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
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.isOp()) {
			return false;
		}
		if (command.getName().equalsIgnoreCase("mmreload")) {
			plugin.reload();
			sender.sendMessage("[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] Configuration reloaded.");
			return true;
		}
		else if (command.getName().equalsIgnoreCase("mmcheck")) {
			new CommandChecker(plugin).runTaskAsynchronously(plugin);
			sender.sendMessage("[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] Checking for new purchases...");
			return true;
		}
		else if (command.getName().equalsIgnoreCase("mmapikey"))
		{
			if (args.length == 1)
			{
				String apiKey = args[0];
				String authenticate = "";
				try {
					authenticate = getJSON("http://www.minecraftmarket.com/api/" + apiKey + "/auth");
					JSONObject json = new JSONObject(authenticate);
					JSONArray jsonresult = json.optJSONArray("result");
					String state = jsonresult.getJSONObject(0).getString("status");
					if(state.equalsIgnoreCase("ok")){
						plugin.ApiKey = apiKey;
						plugin.getConfig().set("apikey", apiKey);
						plugin.saveConfig();
						sender.sendMessage("[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] Server authenticated with Minecraft Market.");
						if (plugin.debug) {
							plugin.getLogger().info("Response: " + authenticate);
							plugin.getLogger().info("Response state" + state);
						}
					}
					}
				catch (Exception e) {
					sender.sendMessage("[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] Server did not authenticate, please check API-KEY.");
					if (plugin.debug) {
						e.printStackTrace();
				}
			}
			
		}
	}
		return false;
	}
}

