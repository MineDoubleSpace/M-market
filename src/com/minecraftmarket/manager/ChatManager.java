package com.minecraftmarket.manager;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.minecraftmarket.Market;

public class ChatManager {

	private ChatManager() {
	}

	static ChatManager instance = new ChatManager();

	public static ChatManager getInstance() {
		return instance;
	}

	File langFile;
	FileConfiguration lang;

	public String prefix;

	public void SetupDefaultLanguage(Market plugin) {
		langFile = new File(plugin.getDataFolder(), "language.yml");
		if (!langFile.exists()) {
			plugin.saveResource("language.yml", false);
		}
		lang = YamlConfiguration.loadConfiguration(langFile);
		prefix = ChatColor.translateAlternateColorCodes('&', getLanguage()
				.getString("messages.prefix"));
		reloadLanguage();
	}

	public FileConfiguration getLanguage() {
		return lang;
	}

	public void reloadLanguage() {
		lang = YamlConfiguration.loadConfiguration(langFile);
	}

}
