package com.minecraftmarket;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import com.minecraftmarket.gui.Gui;
import com.minecraftmarket.manager.ChatManager;
import com.minecraftmarket.manager.JsonManager;

public class Commands implements CommandExecutor, Listener {

	private Market plugin;

	ChatManager chat = ChatManager.getInstance();

	public String getMsg(String string) {
		return ChatManager.getInstance().getLanguage().getString(string);
	}

	public Commands(Market plugin) {
		if (plugin == null)
			throw new IllegalArgumentException("plugin cannot be null");
		if (!plugin.isInitialized())
			throw new IllegalArgumentException("plugin must be initiaized");
		this.plugin = plugin;
	}

	@EventHandler
	public void onStoreCommand(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().equalsIgnoreCase(plugin.shopCommand)) {
			event.setCancelled(true);
			if (!plugin.isGUIEnabled) {
				return;
			} else {
				Gui.getInatance().showGui(event.getPlayer(), 0);
				return;
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender.isOp() || sender.hasPermission("minecraftmarket.admin"))) {
			sender.sendMessage(ChatColor.DARK_RED + getMsg("messages.no-permissions"));
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("mm")) {
			if (args.length == 0) {
				// sendHelp(sender);
				return true;
			}

			if (args[0].equalsIgnoreCase("reload")) {
				plugin.reload();
				sender.sendMessage(ChatManager.getInstance().prefix + getMsg("messages.reload"));
				return true;
			}

			if (args[0].equalsIgnoreCase("check")) {
				new CommandChecker(plugin).runTaskAsynchronously(plugin);
				sender.sendMessage(ChatManager.getInstance().prefix + getMsg("messages.check"));
				return true;
			}

			if (args[0].equalsIgnoreCase("apikey")) {
				ApiKeyAuth(plugin, args, sender);
				return true;
			}

			if (args[0].equalsIgnoreCase("version")) {
				String v = plugin.getDescription().getVersion();
				sender.sendMessage(ChatManager.getInstance().prefix + " Version " + v);
			}

		}

		return true;
	}

	public void sendHelp(Player player) {
	}

	public void ApiKeyAuth(Market plugin, String[] args, CommandSender sender) {
		if (args.length == 2) {
			String apiKey = args[1];
			String authenticate = "";
			try {
				authenticate = JsonManager.getJSON("http://www.minecraftmarket.com/api/" + apiKey + "/auth");
				JSONObject json = new JSONObject(authenticate);
				JSONArray jsonresult = json.optJSONArray("result");
				String state = jsonresult.getJSONObject(0).getString("status");
				if (state.equalsIgnoreCase("ok")) {
					plugin.ApiKey = apiKey;
					plugin.getConfig().set("ApiKey", apiKey);
					plugin.saveConfig();
					sender.sendMessage(ChatManager.getInstance().prefix + " Server authenticated with Minecraft Market.");
					if (plugin.debug) {
						plugin.getLogger().info("Response: " + authenticate);
						plugin.getLogger().info("Response state " + state);
					}
				}
				plugin.reload();
				return;
			} catch (Exception e) {
				sender.sendMessage(ChatManager.getInstance().prefix + " Server did not authenticate, please check API-KEY.");
				if (plugin.debug) {
					e.printStackTrace();
				}
			}
		}
	}

}
