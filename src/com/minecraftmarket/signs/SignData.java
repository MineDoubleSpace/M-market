package com.minecraftmarket.signs;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;

import com.google.common.collect.Lists;
import com.minecraftmarket.Market;
import com.minecraftmarket.util.Log;
import com.minecraftmarket.util.Settings;

public class SignData {

	private static List<SignData> signs = Lists.newArrayList();
	private Location location;
	private Sign sign;
	private Integer number;
	private String date;
	private Block block;
	private String username;
	private String item; // Package name

	public SignData(Location location, int number) {
		this.location = location;
		this.block = location.getBlock();
		this.number = number;
		create();
	}

	public void create() {
		SignData signData = null;
		for (SignData sd : signs) {
			if (sd.getLocation() == this.location) {
				signData = sd;
			}
		}
		if (signData != null) {
			signData.remove();
		}

		signs.add(this);
		if (isSign()) {
			saveAllToConfig();
			this.sign = (Sign) block.getState();
			update();
		}
	}

	public Sign getSign() {
		return this.sign;
	}

	public void update() {
		UpdateAsync async = new UpdateAsync();
		async.runTaskAsynchronously(Market.getPlugin());
	}

	public void updateJson() throws JSONException {
		this.username = Signs.getJsonArray().getJSONObject(number).getString("username");
		this.item = Signs.getJsonArray().getJSONObject(number).getString("item");
		this.date = Signs.getJsonArray().getJSONObject(number).getString("date");
		this.date = date.split(" ")[0];
	}

	public void notFound() {
		sign.setLine(1, ChatColor.DARK_RED + "Not Found.");
		sign.update();
	}

	public boolean isSign() {
		return this.block.getState() instanceof Sign;
	}

	public void updateHead() {
		Skull skull = getSkull(block);
		if (skull != null) {
			skull.setOwner(this.username);
			skull.update();
		}
	}

	public void remove() {
		signs.remove(this);
		saveAllToConfig();
	}

	public static void saveAllToConfig() {
		List<String> listSigns = Lists.newArrayList();
		for (SignData sd : signs) {
			Location loc = sd.getLocation();
			String str = loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ":" + sd.getNumber();
			listSigns.add(str);
		}
		Settings.get().getSignDatabase().set("recent", listSigns);
		Settings.get().saveSignDatabase();
	}

	public static void updateAllSigns() {
		List<SignData> toRemove = Lists.newArrayList();
		for (SignData sd : signs) {
			if (sd.isSign()) {
				sd.update();
			} else {
				toRemove.add(sd);
			}
		}
		for (SignData sd : toRemove) {
			sd.remove();
		}
	}

	public Location convertLocation(String str) {
		String[] args = str.split(":");
		World world = Bukkit.getServer().getWorld(args[0]);
		double x = convertDouble(args[1]);
		double y = convertDouble(args[2]);
		double z = convertDouble(args[3]);
		return new Location(world, x, y, z);
	}

	public static SignData getSignByLocation(Location location) {
		for (SignData sd : signs) {
			if (location.getBlockX() == sd.getLocation().getBlockX() && location.getBlockY() == sd.getLocation().getBlockY() && location.getBlockZ() == sd.getLocation().getBlockZ()) {
				return sd;
			}
		}
		return null;
	}

	public Integer getPlace() {
		return this.number;
	}

	public String getDate() {
		return this.date;
	}

	public String getUsername() {
		return this.username;
	}

	public String getItem() {
		return this.item;
	}

	public Location getLocation() {
		return this.location;
	}

	public Integer getNumber() {
		return this.number;
	}

	private Double convertDouble(String str) {
		return Double.parseDouble(str);
	}

	private Skull getSkull(Block block) {
		Block b = block.getRelative(BlockFace.UP);
		for (BlockFace face : Signs.BLOCKFACES) {
			Block s = b.getRelative(face);
			if ((s.getState() instanceof Skull)) {
				return (Skull) s.getState();
			}
		}
		return null;
	}

	private class UpdateAsync extends BukkitRunnable {

		@Override
		public void run() {
			try {
				updateJson();
				sign.setLine(0, ChatColor.UNDERLINE + "Recent Donor");
				sign.setLine(1, username);
				sign.setLine(2, item);
				sign.setLine(3, date);
				sign.update();
				updateHead();
			} catch (JSONException e) {
				notFound();
			} catch (Exception e) {
				Log.log(e);
			}
		}
	}

}
