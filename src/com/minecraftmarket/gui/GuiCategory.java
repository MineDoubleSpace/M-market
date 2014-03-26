package com.minecraftmarket.gui;

import java.util.List;

import org.bukkit.inventory.Inventory;

import com.google.common.collect.Lists;

public class GuiCategory {

	private static List<GuiCategory> categories = Lists.newArrayList();
	private int ID;
	private int slot;
	private String name;
	private Inventory inventory;

	public GuiCategory(String name, int ID, int slot, Inventory inventory) {
		this.name = name;
		this.ID = ID;
		this.slot = slot;
		this.inventory = inventory;
	}

	public int getID() {
		return ID;
	}

	public String getName() {
		return name;
	}

	public int getSlot() {
		return slot;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void create() {
		if (!categories.contains(this)) categories.add(this);
	}

	public static GuiCategory getCategoryByID(int ID) {
		for (GuiCategory category : categories) {
			if (category.getID() == ID) {
				return category;
			}
		}
		return null;
	}

	public void remove() {
		categories.remove(this);
	}

	public static List<GuiCategory> getCategories() {
		return categories;
	}

	public static void removeAll() {
		for (GuiCategory gc : categories) {
			gc.getInventory().clear();
		}
		categories.clear();
	}

	public static GuiCategory getCategoryBySlot(int slot) {
		for (GuiCategory category : categories) {
			if (category.getSlot() == slot) {
				return category;
			}
		}
		return null;
	}

}
