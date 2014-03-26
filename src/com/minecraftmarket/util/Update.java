package com.minecraftmarket.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.bukkit.configuration.file.YamlConfiguration;

import com.minecraftmarket.Market;

public class Update {
	private Market plugin;
	private UpdateType type;
	private String versionTitle;
	private String versionLink;
	private long totalSize;
	private int sizeLine;
	private int multiplier;
	private boolean announce;
	private URL url;
	private File file;
	private Thread thread;
	private static final String DBOUrl = "http://dev.bukkit.org/server-mods/";
	private String[] noUpdateTag = { "-DEV", "-PRE", "-SNAPSHOT" };
	private static final int BYTE_SIZE = 1024;
	private String updateFolder = YamlConfiguration.loadConfiguration(new File("bukkit.yml")).getString("settings.update-folder");
	private Update.UpdateResult result = Update.UpdateResult.SUCCESS;
	private static final String TITLE = "title";
	private static final String LINK = "link";
	private static final String ITEM = "item";

	public enum UpdateResult {
		SUCCESS, NO_UPDATE, FAIL_DOWNLOAD, FAIL_DBO, FAIL_NOVERSION, FAIL_BADSLUG, UPDATE_AVAILABLE
	}

	public enum UpdateType {
		DEFAULT, NO_VERSION_CHECK, NO_DOWNLOAD
	}

	public Update(Market plugin, String slug, File file, UpdateType type, boolean announce) {
		this.plugin = plugin;
		this.type = type;
		this.announce = announce;
		this.file = file;
		try {
			url = new URL(DBOUrl + slug + "/files.rss");
		} catch (MalformedURLException ex) {
			plugin.getLogger().warning("Error checking for updates");
			result = Update.UpdateResult.FAIL_BADSLUG;
		}
		thread = new Thread(new UpdateRunnable());
		thread.start();
	}

	public Update.UpdateResult getResult() {
		waitForThread();
		return result;
	}

	public long getFileSize() {
		waitForThread();
		return totalSize;
	}

	public String getLatestVersionString() {
		waitForThread();
		return versionTitle;
	}

	public void waitForThread() {
		if (thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveFile(File folder, String file, String u) {
		if (!folder.exists()) {
			folder.mkdir();
		}
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			URL url = new URL(u);
			int fileLength = url.openConnection().getContentLength();
			in = new BufferedInputStream(url.openStream());
			fout = new FileOutputStream(folder.getAbsolutePath() + "/" + file);

			byte[] data = new byte[BYTE_SIZE];
			int count;
			if (announce)
				plugin.getLogger().info("About to download a new update: " + versionTitle);
			long downloaded = 0;
			while ((count = in.read(data, 0, BYTE_SIZE)) != -1) {
				downloaded += count;
				fout.write(data, 0, count);
				int percent = (int) (downloaded * 100 / fileLength);
				if (announce & (percent % 10 == 0)) {
					plugin.getLogger().info("Downloading update: " + percent + "% of " + fileLength + " bytes.");
				}
			}
			if (announce)
				plugin.getLogger().info("Finished updating! please restart to apply changes.");
		} catch (Exception ex) {
			plugin.getLogger().warning("Update unsuccessful.");
			result = Update.UpdateResult.FAIL_DOWNLOAD;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (fout != null) {
					fout.close();
				}
			} catch (Exception ex) {
			}
		}
	}

	public boolean pluginFile(String name) {
		for (File file : new File("plugins").listFiles()) {
			if (file.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	private String getFile(String link) {
		String download = null;
		try {
			URL url = new URL(link);
			URLConnection urlConn = url.openConnection();
			InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
			BufferedReader buff = new BufferedReader(inStream);

			int counter = 0;
			String line;
			while ((line = buff.readLine()) != null) {
				counter++;
				if (line.contains("<li class=\"user-action user-action-download\">")) {
					download = line.split("<a href=\"")[1].split("\">Download</a>")[0];
				} else if (line.contains("<dt>Size</dt>")) {
					sizeLine = counter + 1;
				} else if (counter == sizeLine) {
					String size = line.replaceAll("<dd>", "").replaceAll("</dd>", "");
					multiplier = size.contains("MiB") ? 1048576 : 1024;
					size = size.replace(" KiB", "").replace(" MiB", "");
					totalSize = (long) (Double.parseDouble(size) * multiplier);
				}
			}
			urlConn = null;
			inStream = null;
			buff.close();
			buff = null;
		} catch (Exception ex) {
			ex.printStackTrace();
			plugin.getLogger().warning("Updater failed connecting to webstie");
			result = Update.UpdateResult.FAIL_DBO;
			return null;
		}
		return download;
	}

	private boolean versionCheck(String title) {
		if (type != UpdateType.NO_VERSION_CHECK) {
			String version = plugin.getDescription().getVersion();
			if (title.split(" v").length == 2) {
				String remoteVersion = title.split(" v")[1].split(" ")[0];
				int remVer = -1, curVer = 0;
				try {
					remVer = calVer(remoteVersion);
					curVer = calVer(version);
				} catch (NumberFormatException nfe) {
					remVer = -1;
				}
				if (hasTag(version) || version.equalsIgnoreCase(remoteVersion) || curVer >= remVer) {
					result = Update.UpdateResult.NO_UPDATE;
					return false;
				}
			} else {
				plugin.getLogger().warning("The author of this plugin (" + plugin.getDescription().getAuthors().get(0) + ") has misconfigured their Auto Update system");
				plugin.getLogger().warning("Files uploaded to BukkitDev should contain the version number, seperated from the name by a 'v', such as PluginName v1.0");
				plugin.getLogger().warning("Please notify the author of this error.");
				result = Update.UpdateResult.FAIL_NOVERSION;
				return false;
			}
		}
		return true;
	}

	private Integer calVer(String s) throws NumberFormatException {
		if (s.contains(".")) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < s.length(); i++) {
				Character c = s.charAt(i);
				if (Character.isLetterOrDigit(c)) {
					sb.append(c);
				}
			}
			return Integer.parseInt(sb.toString());
		}
		return Integer.parseInt(s);
	}

	private boolean hasTag(String version) {
		for (String string : noUpdateTag) {
			if (version.contains(string)) {
				return true;
			}
		}
		return false;
	}

	private boolean readFeed() {
		try {
			String title = "";
			String link = "";
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream in = read();
			if (in != null) {
				XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
				while (eventReader.hasNext()) {
					XMLEvent event = eventReader.nextEvent();
					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart().equals(TITLE)) {
							event = eventReader.nextEvent();
							title = event.asCharacters().getData();
							continue;
						}
						if (event.asStartElement().getName().getLocalPart().equals(LINK)) {
							event = eventReader.nextEvent();
							link = event.asCharacters().getData();
							continue;
						}
					} else if (event.isEndElement()) {
						if (event.asEndElement().getName().getLocalPart().equals(ITEM)) {
							versionTitle = title;
							versionLink = link;
							break;
						}
					}
				}
				return true;
			} else {
				return false;
			}
		} catch (XMLStreamException e) {
			plugin.getLogger().warning("Could not reach dev.bukkit.org for update checking. Is it offline?");
			return false;
		}
	}

	private InputStream read() {
		try {
			return url.openStream();
		} catch (IOException e) {
			plugin.getLogger().warning("Could not reach BukkitDev file stream for update checking. Is dev.bukkit.org offline?");
			return null;
		}
	}

	private class UpdateRunnable implements Runnable {

		public void run() {
			if (url != null) {
				if (readFeed()) {
					if (versionCheck(versionTitle)) {
						String fileLink = getFile(versionLink);
						if (fileLink != null && type != UpdateType.NO_DOWNLOAD) {
							String name = file.getName();
							if (fileLink.endsWith(".zip")) {
								String[] split = fileLink.split("/");
								name = split[split.length - 1];
							}
							saveFile(new File("plugins/" + updateFolder), name, fileLink);
						} else {
							result = UpdateResult.UPDATE_AVAILABLE;
						}
					}
				}
			}
		}
	}
}