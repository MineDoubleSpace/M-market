package com.minecraftmarket.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.JSONArray;
import org.json.JSONObject;

import com.minecraftmarket.Market;
import com.minecraftmarket.managers.JsonManager;

public class ApiKeyCommand {
	
	
	public static void ApiKeyAuth(Market plugin, String[] args, CommandSender sender){
	
	String apiKey = args[1];
    String authenticate = "";
   try {
     authenticate = JsonManager.getJSON("http://www.minecraftmarket.com/api/" + apiKey + "/auth", plugin);
     JSONObject json = new JSONObject(authenticate);
     JSONArray jsonresult = json.optJSONArray("result");
   String state = jsonresult.getJSONObject(0).getString("status");
     if(state.equalsIgnoreCase("ok")){
       plugin.ApiKey = apiKey;
       plugin.getConfig().set("ApiKey", apiKey);
       plugin.saveConfig();
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
