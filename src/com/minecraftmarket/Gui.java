package com.minecraftmarket;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Gui {
	
	private Gui () {}
	
	static Gui instance = new Gui();
	
	public static Gui getInatance(){
		return instance;
	}
	
	public HashMap<Integer, String> urlMap = new HashMap<Integer, String>();
	public HashMap<Integer,Inventory> inventory = new HashMap<Integer,Inventory>();
	public ArrayList<Integer> IdMap = new ArrayList<Integer>();
	
	public void setupGUI(Market plugin){
		
		createNewGui(plugin);
	}
	
	public void showGui(Player player, int num) {
        player.openInventory(inventory.get(num));
	}
	
	/*public void createNew(Market plugin){
		try {
			String gui = getJSON("http://www.minecraftmarket.com/api/" + plugin.ApiKey + "/gui", plugin);
			if (plugin.debug) {
				plugin.getLogger().info("Response: " + gui);
			}
			JSONObject json = new JSONObject(gui);
			
			JSONArray jsonResult = json.optJSONArray("result");
			JSONArray categoryArray = json.optJSONArray("categories");
			int max = jsonResult.length();
			if (jsonResult != null) {
				for (int categoryInt = 0; categoryInt < categoryArray.length(); categoryInt++){
					if (categoryInt == 0){
						for (int main = 0; main < categoryArray.length(); main++ ){
							String name = getJsonString(categoryArray, main, "name");
							int id = getJsonInt(categoryArray, main, "id");
							category.setItem(main, Createcategory(name));
							IdMap.add(id);
						}
					}
					int id = getJsonInt(categoryArray, categoryInt, "id");
					String name = getJsonString(categoryArray, categoryInt, "name");
					int categorySize = itemCound(id, jsonResult);
					int invCount = categorySize;
					int maxCount = 0;
					for (int invNum = 0; invNum < getInvetoryTotal(categorySize); invNum ++){
						Inventory inv = createInventory(name + "  (Page #" + invNum + 1, invCount);
						int slot = 0;
						for (int item = 0; item < max; item++) {
							int iid = getJsonInt(jsonResult, maxCount, "categoryid");
							maxCount++;
							if (iid == id){
							inv.setItem(slot, createItem(item, plugin, jsonResult));
							slot++;
								
							}
							}
							
						}
						
					}
					
				}
				
			}catch (Exception e) {
				if (plugin.debug) {
					e.printStackTrace();
				}
			}
	}*/
	

	public void createNewGui(Market plugin) {
		try {
			String gui = getJSON("http://www.minecraftmarket.com/api/" + plugin.ApiKey + "/gui", plugin);
			if (plugin.debug) {
				plugin.getLogger().info("Response: " + gui);
			}
			JSONObject json = new JSONObject(gui);
			
			JSONArray jsonResult = json.optJSONArray("result");
			JSONArray categoryArray = json.optJSONArray("categories");
			int max = jsonResult.length();
			
			if (jsonResult != null) {
				for (int i = 0; i < categoryArray.length() ; i++){
					// creating Catogories
					if (i == 0){
						Inventory inv = createInventory("Categories", getInventorySize(categoryArray.length()));
						for (int cate = 0; cate < categoryArray.length(); cate++){
							String name = getJsonString(categoryArray, cate, "name");
							inv.setItem(cate, Createcategory(name));
						}
						inventory.put(i, inv);
					}
						int id = getJsonInt(categoryArray, i, "id");
						String name = getJsonString(categoryArray, i, "name");
						int InvSize = itemCound(id, jsonResult);
						Inventory inv = createInventory("Category: " + name, getInventorySize(InvSize+1));
						int placement = 0;
						for (int t = 0; t < max; t++ ){
							int iid = getJsonInt(jsonResult, t, "categoryid");
							if (iid == id){
								inv.setItem(placement, createItem(t, plugin, jsonResult));
								placement++;
							}
						}
						inv.setItem(getInventorySize(InvSize)-1, createCategotyPage());
						IdMap.add(id);
						inventory.put(id, inv);
			}
			}
			}catch (Exception e) {
				if (plugin.debug) {
					e.printStackTrace();
				}
			}
	}
	
	private int itemCound(int id, JSONArray jsonResult ) throws JSONException{
		int num = 0;
		int max = jsonResult.length();
		for (int t = 0; t < max; t++ ){
			int iid = getJsonInt(jsonResult, t, "categoryid");
			if (id == iid){
				num++;
			}
		}
		return num;
//		
	}
	
	public ItemStack createItem(int i, Market plugin, JSONArray jsonresult ){
			if (jsonresult != null) {
				try{
					
					ItemStack pack = null;
					String name = getJsonString(jsonresult, i, "name");
					String cate = getJsonString(jsonresult, i, "category");
					String url = jsonresult.getJSONObject(i).getJSONArray("url").getJSONObject(0).getString("url");
					String price = getJsonString(jsonresult, i, "price");
					int id = getJsonInt(jsonresult, i, "id");
					String currency = getJsonString(jsonresult, i, "currency");
					List<String> lore = new ArrayList<String>();
					lore.add(ChatColor.GOLD + "Item: " + ChatColor.GREEN + name);
					lore.add("");
					lore.add(ChatColor.GOLD + "Category: " + ChatColor.GREEN + cate);
					lore.add("");
					
					if (!jsonresult.getJSONObject(i).getString("description").equalsIgnoreCase("")){
					String desc = jsonresult.getJSONObject(i).getString("description");
					lore.add(ChatColor.GOLD + "Description:");
					String[] DescSplit = desc.split("\r\n");
					for (int n = 0; n < DescSplit.length; n++ ){
						lore.add(ChatColor.translateAlternateColorCodes('&', DescSplit[n]));
					}
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
					return pack;
				}catch (Exception e) {
					if (plugin.debug) {
						e.printStackTrace();
					}
				}
			}
		return null;
			
		
	}
	
	public static String getJsonString(JSONArray jsonresult,int i, String args0) throws JSONException {
		return jsonresult.getJSONObject(i).getString(args0);
	}
	
	
	public static int getJsonInt(JSONArray jsonresult,int i, String args0) throws JSONException {
		return jsonresult.getJSONObject(i).getInt(args0);
	}
	
	public ItemStack createCategotyPage() {
		ItemStack item = new ItemStack(Material.IRON_FENCE, 1);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Back to categories");
		im.setLore(Arrays.asList("","*Click here go back to categories"));
		item.setItemMeta(im);
		return item;
	}
		
	
	public String getJSON(String url, Market plugin) throws JSONException {
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
	
	
	private int getInventorySize(int max) {
		if (max <= 0) 
			return 9;
		int total = (int)Math.ceil(max / 9.0);
		return total > 5 ? 54: total * 9;
	}
	
	private ItemStack Createcategory(String name){
		ItemStack item = new ItemStack(Material.ENDER_CHEST, 1);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + name);
		im.setLore(Arrays.asList("", "*Click here to open " + name + " category" ));
		item.setItemMeta(im);
		return item;
		
	}
	
	private Inventory createInventory(String name, int size){
		Inventory inv = Bukkit.createInventory(null, size, name);
		return inv;
	}
	
	
	/*private int getInvetoryTotal(int max){
		int total = max / 54;
		total = (int) Math.ceil(total);
		return total;*/
		
}
