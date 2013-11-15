package com.minecraftmarket.board;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.minecraftmarket.Market;
import com.minecraftmarket.manager.JsonManager;

public class Board {
	
	
	public Scoreboard Sboard;
	
	public Scoreboard getScoreboard (Market plugin){
		Scoreboard board = null;
		try {
			String recent = JsonManager.getJSON("http://www.minecraftmarket.com/api/"+ plugin.ApiKey + "/recentdonor");
			if (plugin.debug) {
				plugin.getLogger().info("Board Response: " + recent);
			}
			JSONObject json = new JSONObject(recent);
			JSONArray jsonResult = json.optJSONArray("result");
			board = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective objective = board.registerNewObjective("Board", "dummy");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "  Recent Donors  ");
			Score score = objective.getScore(Bukkit.getOfflinePlayer(""));
			score.setScore(jsonResult.length()+1);
			int scoreNum = jsonResult.length();
		for (int i = 0; i < jsonResult.length(); i++){
				Score score1 = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + getJsonString(jsonResult, i, "username")));
				score1.setScore(scoreNum);
				scoreNum--;
		}
		}catch (Exception e) {
			if (plugin.debug) {
				e.printStackTrace();
			}
		}
		return board;
	}
	
	public static String getJsonString(JSONArray jsonresult, int i, String args0)
			throws JSONException {
		return jsonresult.getJSONObject(i).getString(args0);
	}
	}