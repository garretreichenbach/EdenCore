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
    public static final String[] defaultMainConfig = {
            "debug-mode: false",
            "auto-save-interval: 10000",
            "max-world-logs: 5",
            "entity-transfer-mode: NONE",
            "build-tools-menu-key: -",
            "admin-tools-menu-key: ="
    };

    public static void initialize(EdenCore instance) {
        mainConfig = instance.getConfig("config");
        mainConfig.saveDefault(defaultMainConfig);
    }

    public static FileConfiguration getMainConfig() {
        return mainConfig;
    }

    public static String getDefaultValue(String field) {
        if(mainConfig.getKeys().contains(field)) {
            for(String s : defaultMainConfig) {
                String fieldName = s.substring(0, s.lastIndexOf(":") - 1).trim().toLowerCase();
                if(fieldName.equals(field.toLowerCase().trim())) return s.substring(s.lastIndexOf(":") + 1).trim();
            }
        }
        return null;
    }
}
