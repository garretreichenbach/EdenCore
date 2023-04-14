package thederpgamer.edencore.api.starbridge;

import api.common.GameServer;
import api.mod.StarLoader;
import thederpgamer.edencore.data.player.DonatorData;
import thederpgamer.edencore.data.player.PlayerData;
import thederpgamer.edencore.manager.LogManager;
import thederpgamer.edencore.utils.DataUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class StarBridgeAPI {
	private static final HashMap<String, DonatorData> supporters = new HashMap<>();
	private static boolean initialized;

	public static void initialize() {
		if(StarLoader.getModFromName("StarBridge") != null) {
			LogManager.logInfo("StarBridge detected. Initializing API...");
			try {
				File donatorsFile = new File("donators.smdat");
				if(donatorsFile.exists()) {
					try {
						FileInputStream fileInputStream = new FileInputStream(donatorsFile);
						byte[] data = new byte[(int) donatorsFile.length()];
						fileInputStream.read(data);
						fileInputStream.close();
						String[] donators = new String(data, StandardCharsets.UTF_8).split(", ");
						for(String donator : donators) {
							String[] donatorData = donator.split(" \\| ");
							if(donatorData.length == 3) {
								if(!"None".equals(donatorData[2])) supporters.put(donatorData[0], new DonatorData(donatorData[0], Long.parseLong(donatorData[1]), donatorData[2]));
							}
						}
					} catch(Exception e) {
						throw new Exception("Failed to load donators file: " + e.getMessage());
					}
				}
				initialized = true;
			} catch(Exception exception) {
				LogManager.logException("Failed to initialize StarBridge API!", exception);
			}
		}
	}

	public static boolean isDonator(String name) {
		if(!initialized) return false;
		//for(DonatorData supporter : supporters.values()) {
		//	if(supporter.name.equals(name)) return true;
		//}
		return !"None".equals(getDonatorType(name));
	}

	public static String getDonatorType(String name) {
			/*
			for(DonatorData supporter : supporters.values()) {
				if(supporter.name.equals(name)) {
					switch (supporter.tier) {
						case "Explorer":
							return "Explorer";
						case "Captain":
							return "Captain";
						case "Staff":
							return "Staff";
					}
				}
			}
			 */
		try {
			PlayerData playerData = DataUtils.getPlayerData(GameServer.getServerState().getPlayerFromName(name));
			switch(playerData.donatorType) {
				case PlayerData.STAFF:
					return "Staff";
				case PlayerData.EXPLORER:
					return "Explorer";
				case PlayerData.CAPTAIN:
					return "Captain";
				default:
					return "None";
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return "None";
	}

	public static void updateFromPacket(DonatorData[] donators) {
		if(!initialized) return;
		supporters.clear();
		for(DonatorData donator : donators) supporters.put(donator.name, donator);
	}

	public static DonatorData[] getDonators() {
		if(!initialized) return new DonatorData[0];
		return supporters.values().toArray(new DonatorData[0]);
	}
}
