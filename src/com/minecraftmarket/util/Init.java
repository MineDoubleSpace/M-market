package com.minecraftmarket.util;

public class Init {

	public static void start() {
		Settings.get().reloadConfig();
		Settings.get().reloadLanguageConfig();
		try {
			String ver = Settings.get().getConfig().getString("version");
			if (ver == null) {
				upgrade();
				return;
			}
			if (!ver.equalsIgnoreCase("1.8")) {
				upgrade();
				return;
			}
			

		} catch (Exception e) {
			Log.log(e);
		}
	}

	private static void upgrade() {
		Log.log("------------------- beginning initialization to MinecraftMarket v1.8 -----------------");
		Settings.get().getConfig().set("version", "1.8");
		Log.log("   ~ Changing version");
		Settings.get().getConfig().set("Enabled-signs", true);
		Log.log("   + Adding Enabled-signs to config");
		Chat.get().getLanguage().set("signs.no-permissions", "You do not have access to create signs");
		Log.log("   + Adding permission message to language");
		Chat.get().getLanguage().set("signs.created", "Sign created and updating..");
		Log.log("   + Adding sign created message to language");
		Chat.get().getLanguage().set("messages.check", "Checking for new purchases..");
		Log.log("   ~ Removing ...123 debug message from language");
		Chat.get().getLanguage().set("messages.signs", "Updating signs..");
		Log.log("   + Adding update signs to language");
		Settings.get().saveAll();
		Log.log("   * Saving files..");
		Settings.get().getLanguageFile();
		Settings.get().reloadConfig();
		Settings.get().reloadLanguageConfig();
		Log.log("------------------------------ initialization successful ------------------------------");
	}
	
}
