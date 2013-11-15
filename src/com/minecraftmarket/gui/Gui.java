package com.minecraftmarket.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

import com.minecraftmarket.Market;
import com.minecraftmarket.manager.ChatManager;
import com.minecraftmarket.manager.JsonManager;

public class Gui {

	private Gui() {
	}

	static Gui instance = new Gui();

	public static Gui getInatance() {
		if (instance == null)
			new Gui();
		return instance;
	}

	public HashMap<Integer, String> urlMap = new HashMap<Integer, String>();
	public HashMap<Integer, Inventory> inventory = new HashMap<Integer, Inventory>();
	public ArrayList<Integer> IdMap = new ArrayList<Integer>();

	public void setupGUI(Market plugin) {
		createNewGui(plugin);
	}

	public void showGui(Player player, int num) {
		player.openInventory(inventory.get(num));
	}

	public void createNewGui(Market plugin) {
		try {
			String gui = JsonManager.getJSON("http://www.minecraftmarket.com/api/" + plugin.ApiKey + "/gui");
			if (plugin.debug) {
				plugin.getLogger().info("GUI Response: " + gui);
			}
			JSONObject json = new JSONObject(gui);

			JSONArray jsonResult = json.optJSONArray("result");
			JSONArray categoryArray = json.optJSONArray("categories");
			int max = jsonResult.length();
			if (jsonResult != null) {
				for (int i = 0; i < categoryArray.length(); i++) {
					if (i == 0) {
						Inventory inv = createInventory("Categories", getInventorySize(categoryArray.length()));
						for (int cate = 0; cate < categoryArray.length(); cate++) {
							String name = getJsonString(categoryArray, cate, "name");
							inv.setItem(cate, Createcategory(name));
						}
						inventory.put(i, inv);
					}
					int id = getJsonInt(categoryArray, i, "id");
					String name = "Category: " + getJsonString(categoryArray, i, "name");
					int InvSize = itemCound(id, jsonResult);
					Inventory inv = createInventory(name, getInventorySize(InvSize + 1));
					int placement = 0;
					for (int t = 0; t < max; t++) {
						int iid = getJsonInt(jsonResult, t, "categoryid");
						if (iid == id) {
							inv.setItem(placement, createItem(t, plugin, jsonResult));
							placement++;
						}
					}
					inv.setItem(getInventorySize(InvSize) - 1, createCategotyPage());
					IdMap.add(id);
					inventory.put(id, inv);
				}
			}
		} catch (Exception e) {
			if (plugin.debug) {
				e.printStackTrace();
			}
		}
	}

	private int itemCound(int id, JSONArray jsonResult) throws JSONException {
		int num = 0;
		int max = jsonResult.length();
		for (int t = 0; t < max; t++) {
			int iid = getJsonInt(jsonResult, t, "categoryid");
			if (id == iid) {
				num++;
			}
		}
		return num;
	}

	public ItemStack createItem(int i, Market plugin, JSONArray jsonresult) {
		if (jsonresult != null) {
			try {
				ItemStack pack = null;
				String name = getJsonString(jsonresult, i, "name");
				String cate = getJsonString(jsonresult, i, "category");
				String url = jsonresult.getJSONObject(i).getJSONArray("url").getJSONObject(0).getString("url");
				String price = getJsonString(jsonresult, i, "price");
				int id = getJsonInt(jsonresult, i, "id");
				String currency = getJsonString(jsonresult, i, "currency");
				List<String> lore = new ArrayList<String>();
				lore.add(ChatColor.GOLD + getMsg("shop.item") + ChatColor.GREEN + name);
				lore.add("");
				lore.add(ChatColor.GOLD + getMsg("shop.category") + ChatColor.GREEN + cate);
				lore.add("");

				if (!jsonresult.getJSONObject(i).getString("description").equalsIgnoreCase("")) {
					String desc = jsonresult.getJSONObject(i).getString("description");
					lore.add(ChatColor.GOLD + getMsg("shop.description"));
					String[] DescSplit = desc.split("\r\n");
					for (int n = 0; n < DescSplit.length; n++) {
						lore.add(ChatColor.translateAlternateColorCodes('&', DescSplit[n]));
					}
					lore.add("");
				}
				lore.add(ChatColor.GOLD + getMsg("shop.price") + ChatColor.GREEN + "" + ChatColor.UNDERLINE + price + " " + currency);
				lore.add("");
				lore.add(ChatColor.ITALIC + getMsg("shop.click-here"));
				ItemStack item = new ItemStack(Material.CHEST);
				ItemMeta im = item.getItemMeta();
				im.setDisplayName(ChatColor.RESET + "ID: " + id);
				urlMap.put(id, url);
				im.setLore(lore);
				item.setItemMeta(im);
				pack = item;
				return pack;
			} catch (Exception e) {
				if (plugin.debug) {
					e.printStackTrace();
				}
			}
		}
		return null;

	}

	public static String getJsonString(JSONArray jsonresult, int i, String args0) throws JSONException {
		return jsonresult.getJSONObject(i).getString(args0);
	}

	public static int getJsonInt(JSONArray jsonresult, int i, String args0) throws JSONException {
		return jsonresult.getJSONObject(i).getInt(args0);
	}

	public ItemStack createCategotyPage() {
		ItemStack item = new ItemStack(Material.IRON_FENCE, 1);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Back to categories");
		im.setLore(Arrays.asList("", "*Click here go back to categories"));
		item.setItemMeta(im);
		return item;
	}

	private int getInventorySize(int max) {
		if (max <= 0)
			return 9;
		int total = (int) Math.ceil(max / 9.0);
		return total > 5 ? 54 : total * 9;
	}

	private ItemStack Createcategory(String name) {
		ItemStack item = new ItemStack(Material.ENDER_CHEST, 1);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + name);
		im.setLore(Arrays.asList("", "*Click here to open " + name + " category"));
		item.setItemMeta(im);
		return item;
	}

	private Inventory createInventory(String name, int size) {
		Inventory inv;
		if (name.length() > 32){
			inv = Bukkit.createInventory(null, size, name.substring(0, 31));
		}else {
			inv = Bukkit.createInventory(null, size, name);
		}
		return inv;
	}

	public String getMsg(String string) {
		return ChatManager.getInstance().getLanguage().getString(string);
	}
}
