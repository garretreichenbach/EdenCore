package thederpgamer.soe.manager;

import api.mod.config.FileConfiguration;
import thederpgamer.soe.EdenCore;

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
            "max-world-logs: 5",
            "operators: Admin_TheDerpGamer, TheDerpGamer"
    };

    public static void initialize(EdenCore instance) {
        mainConfig = instance.getConfig("config");
        mainConfig.saveDefault(defaultMainConfig);
    }

    public static FileConfiguration getMainConfig() {
        return mainConfig;
    }
}
