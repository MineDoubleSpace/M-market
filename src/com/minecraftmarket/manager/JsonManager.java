package com.minecraftmarket.manager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;

public class JsonManager {
	
	public static String getJSON(String url) throws JSONException {
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
	
	public String getJsonString(JSONArray jsonresult, int i, String args0)
			throws JSONException {
		return jsonresult.getJSONObject(1).getString(args0);
	}
	

}
