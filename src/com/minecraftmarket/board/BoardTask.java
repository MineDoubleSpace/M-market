package com.minecraftmarket.board;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import com.minecraftmarket.Market;

public class BoardTask extends BukkitRunnable implements Listener {

	public Market plugin;

	public BoardTask(Market plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		Scoreboard board = new Board().getScoreboard(plugin);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.setScoreboard(board);
		}

	}

	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Scoreboard board = new Board().getScoreboard(plugin);
		event.getPlayer().setScoreboard(board);
	}

}
