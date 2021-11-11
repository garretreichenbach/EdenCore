package thederpgamer.edencore.manager;

import api.mod.config.FileConfiguration;
import thederpgamer.edencore.EdenCore;

/**
 * Manages mod config files and values.
 *
 * @version 1.1 - [10/08/2021]
 * @author TheDerpGamer
 */
public class ConfigManager {

    //Main Config
    private static FileConfiguration mainConfig;
    public static final String[] defaultMainConfig = {
            "debug-mode: false",
            "auto-save-interval: 10000",
            "max-world-logs: 5",
            "entity-transfer-mode: NONE"
    };

    //Key Config
    private static FileConfiguration keyConfig;
    public static final String[] defaultKeyConfig = {
            "exchange-menu-key: *",
            "build-sector-key: -",
            "events-menu-key: ["
    };

    public static void initialize(EdenCore instance) {
        mainConfig = instance.getConfig("config");
        mainConfig.saveDefault(defaultMainConfig);

        keyConfig = instance.getConfig("key-bindings");
        keyConfig.saveDefault(defaultKeyConfig);
    }

    public static FileConfiguration getMainConfig() {
        return mainConfig;
    }

    public static FileConfiguration getKeyConfig() {
        return keyConfig;
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

    public static char getKeyBinding(String field) {
        String binding = keyConfig.getString(field);
        if(binding != null && !binding.toUpperCase().equals("NONE")) return binding.toUpperCase().charAt(0);
        else return '\0';
    }
}
