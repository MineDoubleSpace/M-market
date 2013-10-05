package com.minecraftmarket;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Commands implements CommandExecutor {

	private Market plugin;

	public Commands(Market plugin) {
        if (plugin == null)
            throw new IllegalArgumentException("plugin cannot be null");
        if (!plugin.isInitialized())
            throw new IllegalArgumentException("plugin must be initiaized");
        this.plugin = plugin;
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("shop")){
			if (!plugin.isGUIEnabled){
				return true;
			}
			
			if (sender instanceof Player){
				Gui.getInatance().showGui((Player) sender, 0);
				return true;
			}
			return true;
		}
		
		if (!sender.isOp()) {
			return false;
		}
		if (cmd.getName().equalsIgnoreCase("mm")){
			if (args.length == 0){
				//sendHelp(sender);
				return true;
			}
			
			
			if (args[0].equalsIgnoreCase("reload")){
				plugin.reload();
				sender.sendMessage("[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] Configuration reloaded.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("check")){
				new CommandChecker(plugin).runTaskAsynchronously(plugin);
				sender.sendMessage("[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] Checking for new purchases...");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("apikey")){
				ApiKeyAuth(plugin, args, sender);
				return true;
			}
		
		}
			
		return true;
	}
	
	public void sendHelp(Player player){
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
	
	
	public void ApiKeyAuth(Market plugin, String[] args, CommandSender sender){
		
		String apiKey = args[1];
	    String authenticate = "";
	   try {
	     authenticate = getJSON("http://www.minecraftmarket.com/api/" + apiKey + "/auth");
	     JSONObject json = new JSONObject(authenticate);
	     JSONArray jsonresult = json.optJSONArray("result");
	   String state = jsonresult.getJSONObject(0).getString("status");
	     if(state.equalsIgnoreCase("ok")){
	       plugin.ApiKey = apiKey;
	       plugin.getConfig().set("ApiKey", apiKey);
	       plugin.saveConfig();
	       plugin.reload();
	       sender.sendMessage("[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] Server authenticated with Minecraft Market.");
	       if (plugin.debug) {
	         plugin.getLogger().info("Response: " + authenticate);
	         plugin.getLogger().info("Response state" + state);
	       }
	     }
	     return;
	     } catch (Exception e) {
	     sender.sendMessage("[" + ChatColor.DARK_GREEN + "MinecraftMarket" + ChatColor.RESET + "] Server did not authenticate, please check API-KEY.");
	     if (plugin.debug) {
	       e.printStackTrace();
	     }
	    }
	   }

}
