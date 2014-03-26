package com.minecraftmarket.signs;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import com.google.common.collect.Lists;

public class SignData {

	static List<SignData> signData = Lists.newArrayList();
	private Location location;
	private Sign sign;
	private Integer place;

	public SignData(Location location, int place) {
		this(location, (Sign) location.getBlock().getState(), place);
	}

	public SignData(Location location, Sign sign, int place) {
		this.location = location;
		this.sign = sign;
		this.place = place;
		signData.add(this);
	}

	public Location getLocation() {
		return location;
	}

	public Sign getSign() {
		return sign;
	}

	public Integer getPlace() {
		return place;
	}

	public static SignData getSignByLocation(Location location) {
		for (SignData sd : signData) {
			if (location == sd.getLocation()) return sd;
		}
		return null;
	}
}
