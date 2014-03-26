package com.minecraftmarket.gui;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.google.common.collect.Lists;
import com.minecraftmarket.util.Chat;

public class GuiPackage {

	static List<GuiPackage> packages = Lists.newArrayList();

	private String name;
	private ItemStack icon;
	private String url;
	private int id;
	private String currency;
	private String description;
	private Material material;
	private ItemMeta meta;
	private String category;
	private String price;
	private String iconID;

	public GuiPackage(int id, String name, String currency, String price, String category, String description, String url, String iconID) {
		this.iconID = iconID;
		this.material = getMaterialType();
		this.name = name;
		this.id = id;
		this.url = url;
		this.currency = currency;
		this.price = price;
		this.description = description;
		this.icon = new ItemStack(material, 1);
		this.category = category;
		this.meta = this.icon.getItemMeta();
	}

	public void create() {
		packages.add(this);
		meta.setDisplayName(ChatColor.RESET + "ID: " + id);
		createLore();
		icon.setItemMeta(meta);
		icon.setData(getMaterialData());
	}

	public void remove() {
		packages.remove(this);
	}
	
	public static void removeAll() {
		packages.clear();
	}

	public static List<GuiPackage> getCategories() {
		return packages;
	}

	private void createLore() {
		List<String> lore = Lists.newArrayList();
		lore.add(Chat.get().getMsg("shop.item") + ChatColor.GREEN + name);
		lore.add("");
		lore.add(Chat.get().getMsg("shop.category") + ChatColor.GREEN + category);
		lore.add("");
		if (!description.equals("")) {
			lore.add(ChatColor.GOLD + Chat.get().getMsg("shop.description"));
			String[] DescSplit = ChatColor.translateAlternateColorCodes('&', description).split("\r\n");
			for (String str : DescSplit)
				lore.add(str);
			lore.add("");
		}
		lore.add(ChatColor.GOLD + Chat.get().getMsg("shop.price") + ChatColor.GREEN + "" + ChatColor.UNDERLINE + price + " " + currency);
		lore.add("");
		lore.add(ChatColor.ITALIC + Chat.get().getMsg("shop.click-here"));
		meta.setLore(lore);

	}

	@SuppressWarnings("deprecation")
	private MaterialData getMaterialData() {
		int iconid;
		byte bit;
		try {
			if (this.iconID.contains(":")) {
				String[] s = this.iconID.split(":");
				iconid = Integer.parseInt(s[0]);
				bit = (byte) Integer.parseInt(s[1]);
				return new MaterialData(iconid, bit);
			}
			return new MaterialData(Integer.parseInt(iconID));
		} catch (Exception e) {
			return new MaterialData(Material.CHEST);
		}
	}

	@SuppressWarnings("deprecation")
	private Material getMaterialType() {
		int iconid;
		try {
			if (this.iconID.contains(":")) {
				String[] s = this.iconID.split(":");
				iconid = Integer.parseInt(s[0]);
				return Material.getMaterial(iconid);
			}
			iconid = Integer.parseInt(iconID);
			return Material.getMaterial(iconid);
		} catch (Exception e) {
			return Material.CHEST;
		}
	}

	public static GuiPackage getById(int id) {
		for (GuiPackage gp : packages) {
			if (gp.getId() == id) return gp;
		}
		return null;
	}

	public static List<GuiPackage> getPackages() {
		return packages;
	}

	public String getName() {
		return name;
	}

	public ItemStack getItem() {
		return icon;
	}

	public String getUrl() {
		return url;
	}

	public int getId() {
		return id;
	}

	public String getCurrency() {
		return currency;
	}

	public String getDescription() {
		return description;
	}

	public Material getMaterial() {
		return material;
	}

	public ItemMeta getMeta() {
		return meta;
	}

}
