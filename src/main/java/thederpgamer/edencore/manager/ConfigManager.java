package thederpgamer.edencore.manager;

import api.mod.config.FileConfiguration;
import thederpgamer.edencore.EdenCore;

/**
 * Manages mod config files and values.
 *
 * @author TheDerpGamer
 * @version 1.1 - [10/08/2021]
 */
public class ConfigManager {
	
	public static final String[] defaultMainConfig = {
			"build_sector_distance_offset: 100000"
	};
	
	private static FileConfiguration mainConfig;
	
	public static void initialize(EdenCore instance) {
		mainConfig = instance.getConfig("config");
		mainConfig.saveDefault(defaultMainConfig);
	}

	public static FileConfiguration getMainConfig() {
		return mainConfig;
	}
}
