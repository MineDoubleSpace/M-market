package com.minecraftmarket.gui;

import java.util.Arrays;

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

import com.minecraftmarket.Api;
import com.minecraftmarket.util.Json;
import com.minecraftmarket.util.Log;

public class Gui {

	static Gui instance;
	private Inventory guiHub;

	public void setupGUI() {
		createNewGui();
	}

	public void showGui(Player player, int num) {
		GuiCategory catetory = GuiCategory.getCategoryByID(num);
		if (catetory.getInventory() != null) {
			player.openInventory(catetory.getInventory());
			return;
		}
	}

	public void showCategories(Player player) {
		if (guiHub != null) player.openInventory(guiHub);
	}

	public void createNewGui() {
		try {
			clearCache();
			String gui = Json.getJSON(Api.getUrl() + "/gui");
			Log.response("GUI", gui);
			if (Json.isJson(gui)) {
				JSONObject json = new JSONObject(gui);
				JSONArray itemArray = json.optJSONArray("result");
				JSONArray categoryArray = json.optJSONArray("categories");
				int max = itemArray.length();
				for (int i = 0; i < categoryArray.length(); i++) {
					if (i == 0) {
						guiHub = createInventory("Categories", getInventorySize(categoryArray.length()));
						for (int cate = 0; cate < categoryArray.length(); cate++) {
							String name = getJsonString(categoryArray, cate, "name");
							guiHub.setItem(cate, Createcategory(name));
						}
					}
					int id = getJsonInt(categoryArray, i, "id");
					String name = "Category: " + getJsonString(categoryArray, i, "name");
					int InvSize = itemCount(id, itemArray);
					Inventory inv = createInventory(name, getInventorySize(InvSize + 1));
					int placement = 0;
					for (int t = 0; t < max; t++) {
						int iid = getJsonInt(itemArray, t, "categoryid");
						if (iid == id) {
							inv.setItem(placement, createItem(t, itemArray));
							placement++;
						}
					}
					inv.setItem(inv.getSize() - 1, createCategoryPage());
					GuiCategory category = new GuiCategory(name, id, i, inv);
					category.create();
				}
			}
		} catch (Exception e) {
			Log.log(e);
		}
	}

	private int itemCount(int id, JSONArray jsonResult) throws JSONException {
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

	private void clearCache() {
		if (guiHub != null) {
			guiHub.clear();
		}
		GuiCategory.removeAll();
		GuiPackage.removeAll();
	}

	private ItemStack createItem(int i, JSONArray jsonresult) {
		if (jsonresult != null) {
			try {
				String name = getJsonString(jsonresult, i, "name");
				String cate = getJsonString(jsonresult, i, "category");
				String url = jsonresult.getJSONObject(i).getJSONArray("url").getJSONObject(0).getString("url");
				String price = getJsonString(jsonresult, i, "price");
				int id = getJsonInt(jsonresult, i, "id");
				String icon;
				try {
					icon = getJsonString(jsonresult, i, "iconid");
				} catch (Exception e) {
					icon = "54";
				}
				String currency = getJsonString(jsonresult, i, "currency");
				String description = jsonresult.getJSONObject(i).getString("description");

				//Create GUI package
				GuiPackage guiPackage = new GuiPackage(id, name, currency, price, cate, description, url, icon);
				guiPackage.create();
				return guiPackage.getItem();
			} catch (Exception e) {
				Log.log(e);
			}
		}
		return null;

	}

	private static String getJsonString(JSONArray jsonresult, int i, String args0) throws JSONException {
		return jsonresult.getJSONObject(i).getString(args0);
	}

	private static int getJsonInt(JSONArray jsonresult, int i, String args0) throws JSONException {
		return jsonresult.getJSONObject(i).getInt(args0);
	}

	private ItemStack createCategoryPage() {
		ItemStack item = new ItemStack(Material.IRON_FENCE, 1);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Back to categories");
		im.setLore(Arrays.asList("", "*Click here go back to categories"));
		item.setItemMeta(im);
		return item;
	}

	private int getInventorySize(int max) {
		if (max <= 0) return 9;
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
		if (name.length() > 32) {
			inv = Bukkit.createInventory(null, size, name.substring(0, 31));
		} else {
			inv = Bukkit.createInventory(null, size, name);
		}
		return inv;
	}

	public static Gui getInstance() {
		if (instance == null) instance = new Gui();
		return instance;
	}

	private Gui() {
	}
}
