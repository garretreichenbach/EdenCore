package thederpgamer.edencore.manager;

import api.mod.config.FileConfiguration;
import thederpgamer.edencore.EdenCore;

/**
 * Manages mod config files and values.
 *
 * @author TheDerpGamer
 * @since 06/27/2021
 */
public class ConfigManager {

    //Main Config
    private static FileConfiguration mainConfig;
    private static final String[] defaultMainConfig = {
            "debug-mode: false",
            "auto-save-interval: 10000",
            "max-world-logs: 5",
            "operators: null"
    };

    public static void initialize(EdenCore instance) {
        mainConfig = instance.getConfig("config");
        mainConfig.saveDefault(defaultMainConfig);
    }

    public static FileConfiguration getMainConfig() {
        return mainConfig;
    }
}
