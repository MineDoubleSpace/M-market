package com.minecraftmarket.gui;

	import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

	import com.minecraftmarket.Market;
import com.minecraftmarket.managers.JsonManager;

	public class NewGui {
		
		private NewGui () {}
		
		static NewGui instance = new NewGui();
		
		public static NewGui getInatance(){
			return instance;
		}
		
		private Market plugin;
		private Inventory inv;
		private ItemStack i1;
		public HashMap<Integer, String> urlMap = new HashMap<Integer, String>();
		public HashMap<Integer, String>  Intems= new HashMap<Integer, String>();
		private boolean debug = plugin.debug;
		
		public void setupGUI(Market plugin){
			inv = Bukkit.getServer().createInventory(null, 54, ChatColor.DARK_GRAY + ""+ ChatColor.BOLD + "Minecraft Market");
			
			createGui(plugin, 0);
		}
		
		public void showGui(Player player) {
	        player.openInventory(inv);
		}
		
		
		public void saveItems(){
			
		}
		
		
		public ItemStack createGui(Market plugin, int num){
			ItemStack pack = null;
			int max = 0;
			int ID = 0;
			try {
				String gui = JsonManager.getJSON("http://www.minecraftmarket.com/api/" + plugin.ApiKey + "/gui", plugin);
				if (plugin.debug) {
					plugin.getLogger().info("Response: " + gui);
				}
				JSONObject json = new JSONObject(gui);
				JSONArray jsonresult = json.optJSONArray("result");
				if (jsonresult != null) {
					max = jsonresult.length();
					if (max <= 54){
//						for (int 1 = 0)
					}
				}

						
						
			}catch (Exception e) {
				if (plugin.debug) {
					e.printStackTrace();
				}
			}
			return pack;
		}
		
		
		
		
		
		
		
		public void createItem(int i, JSONArray jsonresult) {
			ItemStack pack = null;
			String name;
			try {
				name = jsonresult.getJSONObject(i).getString("name");
		
			String cate = jsonresult.getJSONObject(i).getString("category");
			JSONArray urlarray = jsonresult.getJSONObject(i).getJSONArray("url");
			String url = urlarray.getJSONObject(0).getString("url");
			String price = jsonresult.getJSONObject(i).getString("price");
			int id = jsonresult.getJSONObject(i).getInt("id");
			String currency = jsonresult.getJSONObject(i).getString("currency");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GOLD + "Package: " + ChatColor.GREEN + name);
			lore.add("");
			lore.add(ChatColor.GOLD + "Category: " + ChatColor.GREEN + cate);
			lore.add("");
			
			if (!jsonresult.getJSONObject(i).getString("description").equalsIgnoreCase("")){
			String desc = jsonresult.getJSONObject(i).getString("description");
			lore.add(ChatColor.GOLD + "Description:");
			lore.add(ChatColor.translateAlternateColorCodes('&', desc));
			lore.add("");
			
			}
			
			lore.add(ChatColor.GOLD + "Price: " + ChatColor.GREEN + "" + ChatColor.UNDERLINE + price + " " + currency);
			lore.add("");
			lore.add(ChatColor.ITALIC + "*Click here to buy");
			ItemStack item = new ItemStack(Material.CHEST);
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(ChatColor.RESET + "ID: "+ id);
			urlMap.put(id, url);
			im.setLore(lore);
			item.setItemMeta(im);
			pack = item;
			i1 = pack;
			inv.setItem(i, i1);
			} catch (Exception e) {
				if (plugin.debug) {
					e.printStackTrace();
				}
			}
		}
	}
	
