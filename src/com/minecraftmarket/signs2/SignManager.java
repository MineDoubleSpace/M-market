package com.minecraftmarket.signs2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.minecraftmarket.Market;
import com.minecraftmarket.managers.JsonManager;


public class SignManager {
	
	private SignManager(){}
	
	static SignManager instance = new SignManager();
	
	public static SignManager getInstance(){
		return instance;
	}
	
	ArrayList<String> SignInfo = new ArrayList<String>();
	ArrayList<String> SignLoc = new ArrayList<String>();
	
	
	
	public void signChecker(final Market plugin){
		SignInfo.clear();
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
					
					for (int i = 0; i < max; i++){
						String name = jsonresult.getJSONObject(i).getString("username");
						String dateWTime = jsonresult.getJSONObject(i).getString("date");
						String item = jsonresult.getJSONObject(i).getString("item");
						String[] date = dateWTime.split(" ");
						String ffmat = i+1+";"+name+";"+date[0]+";"+item;
						SignInfo.add(ffmat);
					}
					for (String info : getSign().getStringList("locations")){
						String[] Ainfo = info.split(",");
						int num = Integer.parseInt(Ainfo[0]);
						World w = Bukkit.getServer().getWorld(Ainfo[1]);
						double x = Integer.parseInt(Ainfo[2]);
						double y = Integer.parseInt(Ainfo[3]);
						double z = Integer.parseInt(Ainfo[4]);
						Location loc = new Location(w, x, y, z);
						if (loc.getBlock().getState() instanceof Sign){
							Sign sign = (Sign) loc.getBlock().getState();
							Bukkit.broadcastMessage(loc.getBlock().toString());
							String info1 = SignManager.getInstance().SignInfo.get(num);
							sign.setLine(0, ChatColor.UNDERLINE +"Recent Donor");
							String[] SignInfo = info1.split(";");
							sign.setLine(1, SignInfo[1]);
							sign.setLine(2, SignInfo[2]);
							sign.setLine(3, SignInfo[3]);
							sign.update();
			
					}
						
//						Bukkit.broadcastMessage("nht");
						
					}
					
					
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			}
		}, 20L, plugin.interval * 20L);
	}

	
	
	
	
	
	public void saveSign(Sign sign){		
		
	}
	
	
	FileConfiguration signFile;
	File sfile;
	
	public void createSignFile(Market plugin){
		sfile = new File(plugin.getDataFolder(), "Sign.yml");
	if (!sfile.exists()) {
		try {
			sfile.createNewFile();
		} catch (IOException e) {
			if (!plugin.debug){
			plugin.getLogger().info(ChatColor.RED	+ "Error create Sign.yml");
			return;
			}
			e.printStackTrace();
		}
	}
	
	signFile = YamlConfiguration.loadConfiguration(sfile);
	}
	
	public FileConfiguration getSign(){
		return signFile;
	}
	
	public void saveSign(){
		try {
			signFile.save(sfile);
		} catch (IOException e) {
		}
	}
	
	

}
