package com.minecraftmarket.signs;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.minecraftmarket.ChatManager;
import com.minecraftmarket.JsonManager;
import com.minecraftmarket.Market;

public class Signs implements Listener {

	public ArrayList<String> signsLoc = new ArrayList<String>();

	ChatManager chat = ChatManager.getInstance();
	public Market plugin;

	@EventHandler
	public void onSignChange(SignChangeEvent event){
		if (event.getLine(0).equalsIgnoreCase("[Recent]")){
			try{
				int id = Integer.parseInt(event.getLine(1));
				Location loc = event.getBlock().getLocation();
				saveLocation(loc, id);
			}catch (NumberFormatException ex){
				event.getBlock().breakNaturally();
				event.getPlayer().sendMessage(chat.prefix+ ChatColor.DARK_RED +"Wrong sign format");
		}
	}
	}
	
	public void updateSignLocation(){
		for (String str: getSignFile().getStringList("signs")){
			signsLoc.add(str);
		}
	}
	
	
	public void callUpdate(Market plugin){
		try {
			String recent = JsonManager.getJSON("http://www.minecraftmarket.com/api/"+ plugin.ApiKey + "/recentdonor");
			if (plugin.debug) {
				plugin.getLogger().info("Sign Response: " + recent);
			}
			JSONObject json = new JSONObject(recent);
			JSONArray jsonResult = json.optJSONArray("result");
		
		for (int i = 0; i < signsLoc.size(); i++){
			int num = getNumber(signsLoc.get(i));
			Location loc = convertLocation(signsLoc.get(i));
			Block block = loc.getBlock();
			Bukkit.broadcastMessage(block.getState().toString());
			if (block.getState() instanceof Sign){
				Sign sign = (Sign) block.getState();
				sign.setLine(0, "test");
				sign.setLine(2, "test");
				sign.setLine(3, "test");
				sign.setLine(0, "test");
				sign.update();
				Bukkit.broadcastMessage("got");
				
				//sign.setLine(1, getJsonString(jsonResult, num, "username"));
				//sign.setLine(2, getDate(getJsonString(jsonResult, num, "date")));
				//sign.setLine(3, getJsonString(jsonResult, num, "item"));
			}else{
			//	signsLoc.remove(i);
				Bukkit.broadcastMessage("got1");
			}
		}
		}catch (Exception e) {
			if (plugin.debug) {
				e.printStackTrace();
			}
		}
		}
	
	
	
	

	public void saveLocation(Location loc, int id) {
		String str = loc.getWorld().getName()+ ":" +loc.getBlockX() + ":" + loc.getBlockY() + ":"	+ loc.getBlockZ() + ":" + id;
		signsLoc.add(str);

	}
	
	public static String getJsonString(JSONArray jsonresult, int i, String args0)throws JSONException {
		return jsonresult.getJSONObject(i).getString(args0);
	}
	
	
	public void saveLocationToConfig(){
		getSignFile().set("signs", signsLoc);
		plugin.saveConfig();
	}
	
	public FileConfiguration getSignFile(){
		return plugin.getConfig();
	}
	
	public Location convertLocation(String str){
		String[] args = str.split(":");
		World world = Bukkit.getServer().getWorld(args[0]);
		double x = convertDouble(args[1]);
		double y = convertDouble(args[2]);
		double z = convertDouble(args[3]);
		Location loc = new Location(world, x, y, z);
		return loc;
	}
	
	public Double convertDouble(String str){
		return Double.parseDouble(str);
	}
	
	public String getDate(String str){
		String[] date = str.split(" ");
		return date[0];
	}
	
	public int getNumber(String str){
		String[] number = str.split(":");
		return Integer.parseInt(number[4]);
	}
	
	
}
