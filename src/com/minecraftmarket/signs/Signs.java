package com.minecraftmarket.signs;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.minecraftmarket.Api;
import com.minecraftmarket.util.Json;
import com.minecraftmarket.util.Log;
import com.minecraftmarket.util.Settings;

public class Signs implements Listener {

	public static ArrayList<String> signsLoc = new ArrayList<String>();

	public final BlockFace[] BLOCKFACES = { BlockFace.SELF, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };

	private Signs() {
	}

	public static Signs getSigns() {
		if (instance == null) instance = new Signs();
		return instance;
	}
	
	private static Signs instance;

	public void callUpdate() {
		Block block = null;
		try {
			String recent = Json.getJSON(Api.getUrl() + "/recentdonor");
			Log.response("Recent payment" , recent);
			if (!Json.isJson(recent)) {
				return;
			}
			JSONObject json = new JSONObject(recent);
			JSONArray jsonResult = json.optJSONArray("result");
			for (int i = 0; i < signsLoc.size(); i++) {
				try {
					int num = getNumber(signsLoc.get(i));
					Location loc = convertLocation(signsLoc.get(i));
					block = loc.getBlock();
					if (block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
						Sign sign = (Sign) block.getState();
						if (num > jsonResult.length()) {
							notFound(sign);
						} else {
							String username = getJsonString(jsonResult, num, "username");
							sign.setLine(0, ChatColor.UNDERLINE + "Recent Donor");
							sign.setLine(1, username);
							sign.setLine(2, getJsonString(jsonResult, num, "item"));
							sign.setLine(3, getDate(getJsonString(jsonResult, num, "date")));
							sign.update();
							updateHead(block, username);
						}

					} else {
						signsLoc.remove(i);
						saveLocationToConfig();
					}
				} catch (JSONException e) {
					if (block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
						Sign sign = (Sign) block.getState();
						notFound(sign);
						sign.update();
						signsLoc.remove(i);
						saveLocationToConfig();
					}
					Log.log(e);
				}
			}
		} catch (Exception e) {
			Log.log(e);
		}
	}

	public void updateSignLocation() {
		signsLoc.clear();
		for (String str : getSignFile().getStringList("recent")) {
			signsLoc.add(str);
		}
	}

	public void updateHead(Block block, String name) {
		Skull skull = getSkull(block);
		if (skull != null) {
			skull.setOwner(name);
			skull.update();
		}

	}

	private void notFound(Sign sign) {
		sign.setLine(0, ChatColor.UNDERLINE + "Recent Donor");
		sign.setLine(1, ChatColor.RED + "Donor not");
		sign.setLine(2, ChatColor.RED + "found");
		sign.setLine(3,"");
		sign.update();
	}

	private Skull getSkull(Block block) {
		Block b = block.getRelative(BlockFace.UP);
		for (BlockFace face : BLOCKFACES) {
			Block s = b.getRelative(face);
			if ((s.getState() instanceof Skull)) {
				return (Skull) s.getState();
			}
		}
		return null;
	}

	public void saveLocation(Location loc, int id) {
		String str = loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ":" + id;
		signsLoc.add(str);

	}

	public synchronized void createSign(Location loc, int id) {
		saveLocation(loc, id);
		saveLocationToConfig();
	}

	public static String getJsonString(JSONArray jsonresult, int i, String args0) throws JSONException {
		return jsonresult.getJSONObject(i).getString(args0);
	}

	public void saveLocationToConfig() {
		getSignFile().set("recent", signsLoc);
		Settings.get().saveSignDatabase();
	}

	public FileConfiguration getSignFile() {
		return Settings.get().getSignDatabase();
	}

	public Location convertLocation(String str) {
		String[] args = str.split(":");
		World world = Bukkit.getServer().getWorld(args[0]);
		double x = convertDouble(args[1]);
		double y = convertDouble(args[2]);
		double z = convertDouble(args[3]);
		Location loc = new Location(world, x, y, z);
		return loc;
	}

	public Double convertDouble(String str) {
		return Double.parseDouble(str);
	}

	public String getDate(String str) {
		String[] date = str.split(" ");
		return date[0];
	}

	public int getNumber(String str) {
		String[] number = str.split(":");
		return Integer.parseInt(number[4]);
	}
}