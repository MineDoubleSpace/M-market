package com.minecraftmarket;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateCheck {
	
private UpdateCheck () {}
	
	static UpdateCheck instance = new UpdateCheck();
	
	public static UpdateCheck getInatance(){
		return instance;
	}
	
	public void checkUpdate(Market plugin){
			String Nversion;
		try {
	           Nversion = getJSON("http://www.minecraftmarket.com/api/version/auth");
	           JSONObject json = new JSONObject(Nversion);
	           String JsonVersion = json.getString("version");
	           String Cversion = "v"+plugin.version;
	           if (!Cversion.equals(JsonVersion)){
	        	  plugin.getLogger().info("[MinecraftMarket] Update available: http://dev.bukkit.org/bukkit-plugins/minecraft-market-free-donation/");
	        	  plugin.getLogger().info("[MinecraftMarket] " + JsonVersion);
	        	  plugin.getLogger().info("[MinecraftMarket] " + Cversion);
	        	  return;
	           }
	           plugin.getLogger().info("[MinecraftMarket] Plugin is up to date");
		}catch (Exception e) {
			if (plugin.debug) {
				e.printStackTrace();
			}
		}
	}
	
	public String getJSON(String url) throws JSONException {
	    try {
	        URL u = new URL(url);
	        HttpURLConnection c = (HttpURLConnection) u.openConnection();
	        c.setRequestMethod("GET");
	        c.setRequestProperty("Content-length", "0");
	        c.setRequestProperty("Accept", "application/json");
	        c.setUseCaches(false);
	        c.setAllowUserInteraction(false);
	        c.setConnectTimeout(10000);
	        c.setReadTimeout(10000);
	        c.connect();
	        int status = c.getResponseCode();
//	        plugin.getLogger().info("Response Message: " + c.getResponseMessage());
	        switch (status) {
	            case 200:
	            case 201:
	            	Scanner s = new Scanner(c.getInputStream());
	            	s.useDelimiter("\\Z");
	            	return s.next();
	        }

	    } catch (MalformedURLException ex) {
	    } catch (IOException ex) {
	    }
	    return "";
	}

}
