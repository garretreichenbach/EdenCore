package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import org.apache.commons.lang3.math.NumberUtils;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.manager.ConfigManager;

import javax.annotation.Nullable;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/22/2021
 */
public class ServerConfigCommand implements CommandInterface {

    @Override
    public String getCommand() {
        return "config";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Used to read and write server config values without requiring a restart.\n" +
                "- /%COMMAND% get <field_name> : Returns the current value of the specified field in server config.\n" +
                "- /%COMMAND% get_default <field_name> : Returns the default value of the specified field in server config.\n" +
                "- /%COMMAND% get_all : Lists all fields in server config and their current values.\n" +
                "- /%COMMAND% set <field_name> <field_value> : Sets the value of the specified field in server config.\n" +
                "- /%COMMAND% set_default <field_name> : Sets the specified field in server config to it's default value.\n" +
                "- /%COMMAND% set_default_all : Sets all fields in server config to their default values." +
                "- /%COMMAND% reload : Reloads the server config.";
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }

    @Override
    public boolean onCommand(PlayerState playerState, String[] args) {
        if(args != null && args.length > 0) {
            String subCommand = args[0].toLowerCase();
            switch(subCommand) {
                case "get":
                    if(args.length == 2) {
                        if(ConfigManager.getMainConfig().getKeys().contains(args[1])) PlayerUtils.sendMessage(playerState, "The current value of \"" + args[1] + "\" is \"" + ConfigManager.getMainConfig().getString(args[1]) + "\".");
                        else PlayerUtils.sendMessage(playerState, "There is no field by the name \"" + args[1] + "\" in server config.");
                    } else return false;
                    break;
                case "get_default":
                    if(args.length == 2) {
                        if(ConfigManager.getMainConfig().getKeys().contains(args[1])) PlayerUtils.sendMessage(playerState, "The default value of \"" + args[1] + "\" is \"" + ConfigManager.getDefaultValue(args[1]) + "\".");
                        else PlayerUtils.sendMessage(playerState, "There is no field by the name \"" + args[1] + "\" in server config.");
                    } else return false;
                    break;
                case "get_all":
                    if(args.length == 1) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("Current Config:\n");
                        for(String field : ConfigManager.getMainConfig().getKeys()) builder.append(field).append(": ").append(ConfigManager.getMainConfig().getString(field)).append("\n");
                        PlayerUtils.sendMessage(playerState, builder.toString().trim());
                    } else return false;
                    break;
                case "set":
                    if(args.length == 3) {
                        if(ConfigManager.getMainConfig().getKeys().contains(args[1])) {
                            boolean success = setConfigValue(args[1], args[2]);
                            if(success) {
                                PlayerUtils.sendMessage(playerState, "Successfully set field \"" + args[1] + "\" to \"" + args[2] + "\".");
                                ConfigManager.getMainConfig().reloadConfig();
                            } else PlayerUtils.sendMessage(playerState, "The value of \"" + args[1] + "\" must be of type " + getType(ConfigManager.getMainConfig().getString(args[2])) + ".");
                        } else PlayerUtils.sendMessage(playerState, "There is no field by the name \"" + args[1] + "\" in server config.");
                    } else return false;
                    break;
                case "set_default":
                    if(args.length == 2) {
                        if(ConfigManager.getMainConfig().getKeys().contains(args[1])) {
                            String defaultValue = ConfigManager.getDefaultValue(args[1]);
                            boolean success = setConfigValue(args[1], defaultValue);
                            if(success) {
                                PlayerUtils.sendMessage(playerState, "Successfully set field \"" + args[1] + "\" to \"" + defaultValue + "\".");
                                ConfigManager.getMainConfig().reloadConfig();
                            } else PlayerUtils.sendMessage(playerState, "The value of \"" + args[1] + "\" must be of type " + getType(ConfigManager.getMainConfig().getString(defaultValue)) + ".");
                        } else PlayerUtils.sendMessage(playerState, "There is no field by the name \"" + args[1] + "\" in server config.");
                    } else return false;
                    break;
                case "set_default_all":
                    if(args.length == 1) {
                        ConfigManager.getMainConfig().saveDefault(ConfigManager.defaultMainConfig);
                        ConfigManager.getMainConfig().reloadConfig();
                        PlayerUtils.sendMessage(playerState, "Successfully set default values for server config.");
                    } else return false;
                    break;
                case "reload":
                    if(args.length == 1) {
                        ConfigManager.getMainConfig().reloadConfig();
                        PlayerUtils.sendMessage(playerState, "Successfully reloaded server config.");
                    } else return false;
                    break;
                default: return false;
            }
        }
        return true;
    }

    @Override
    public void serverAction(@Nullable PlayerState playerState, String[] strings) {

    }

    @Override
    public StarMod getMod() {
        return EdenCore.getInstance();
    }

    private boolean setConfigValue(String field, String value) {
        try {
            String fieldType = getType(ConfigManager.getMainConfig().getString(field));
            String inputType = getType(value);
            if(fieldType.equals(inputType)) {
                ConfigManager.getMainConfig().set(field, value);
                return true;
            } else return false;
        } catch(Exception exception) {
            return false;
        }
    }

    private String getType(String value) {
        value = value.toLowerCase().trim();
        if(value.equals("true") || value.equals("false")) return "BOOLEAN";
        else if(value.contains(",")) return "LIST";
        else if(NumberUtils.isNumber(value)) {
            if(value.contains(".")) return "DOUBLE";
            else return "INTEGER";
        } else return "STRING";
    }
}
