package thederpgamer.edencore.commands;

import api.common.GameCommon;
import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.manager.LogManager;
import thederpgamer.edencore.utils.BuildSectorUtils;
import thederpgamer.edencore.utils.DataUtils;
import thederpgamer.edencore.utils.ServerUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;

/**
 * <Description>
 *
 * @version 1.0 - [09/07/2021]
 * @author TheDerpGamer
 */
public class BuildSectorCommand implements CommandInterface {

    //Todo: Make this into a GUI instead at some point

    public static final long MAX_WAIT_TIME = 10000; //How long to wait for the the AI toggling method to finish before killing it due to stall

    @Override
    public String getCommand() {
        return "build_sector";
    }

    @Override
    public String[] getAliases() {
        return new String[] {
                "build_sector"
        };
    }

    @Override
    public String getDescription() {
        return "Main command for managing player build sectors.\n" +
               "- /%COMMAND% enter [player_name] : Enters your build sector or the build sector belonging to the specified player if you have permission.\n" +
               "- /%COMMAND% leave : Leaves the current build sector.\n" +
               "- /%COMMAND% invite <player_name> : Invites the specified player to your build sector.\n" +
               "- /%COMMAND% remove <player_name> : Removes the specified player from your build sector.\n" +
               "- /%COMMAND% list : Lists the players that have access to your build sector.\n" +
               "- /%COMMAND% allow <player_name> <permission> : Allows a player access to the specified permission in your build sector.\n" +
               "- /%COMMAND% deny <player_name> <permission> : Denys a player access to the specified permission in your build sector.\n" +
               "- /%COMMAND% permissions <player_name> : Lists the permissions assigned to the specified player in your build sector.\n" +
               "- /%COMMAND% spawn <catalog_name> : Spawns the specified catalog entry in the current build sector if you have the correct permissions.\n" +
               "- /%COMMAND% spawn_enemy <catalog_name> : Spawns the specified catalog entry in the current build sector and sets it as an enemy if you have the correct permissions.\n" +
               "- /%COMMAND% delete [entity_name] : Deletes the specified entity, or the one the currently selected if no entity is specified.\n" +
               "- /%COMMAND% toggle_ai <entity_name|all/*> <on|off> : Toggles AI for the specified entity in the current build sector.";
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }

    @Override
    public boolean onCommand(PlayerState sender, String[] args) {
        if(args.length >= 1) {
            try {
                String subCommand = args[0].toLowerCase();
                switch(subCommand) {
                    case "enter":
                        if(!DataUtils.isPlayerInAnyBuildSector(sender)) {
                            if(args.length == 1) {
                                if(PlayerUtils.getCurrentControl(sender) instanceof SegmentController) PlayerUtils.sendMessage(sender, "You can't do this while in an entity.");
                                else DataUtils.movePlayerToBuildSector(sender, DataUtils.getBuildSector(sender.getName()));
                                return true;
                            } else if(args.length == 2) {
                                if(DataUtils.playerExists(args[1])) {
                                    BuildSectorData sectorData = DataUtils.getBuildSector(args[1]);
                                    if(sectorData.hasPermission(sender.getName(), "ENTER")) {
                                        if(PlayerUtils.getCurrentControl(sender) instanceof SegmentController) PlayerUtils.sendMessage(sender, "You can't do this while in an entity.");
                                        else DataUtils.movePlayerToBuildSector(sender, sectorData);
                                    } else PlayerUtils.sendMessage(sender, "You don't have permission to do this.");
                                } else PlayerUtils.sendMessage(sender, "Player \"" + args[1] + "\" doesn't exist.");
                            } else return false;
                        } else PlayerUtils.sendMessage(sender, "You are already in a build sector right now.");
                        return true;
                    case "leave":
                        if(DataUtils.isPlayerInAnyBuildSector(sender)) {
                            if(PlayerUtils.getCurrentControl(sender) instanceof SegmentController) PlayerUtils.sendMessage(sender, "You can't do this while in an entity.");
                            else DataUtils.movePlayerFromBuildSector(sender);
                        } else PlayerUtils.sendMessage(sender, "You aren't in a build sector right now.");
                        return true;
                    case "invite":
                        if(args.length == 2) {
                            PlayerState target = ServerUtils.getPlayerByName(args[1]);
                            if(target != null) {
                                if(target.equals(sender)) PlayerUtils.sendMessage(sender, "You can't invite yourself!");
                                else {
                                    BuildSectorData sectorData = DataUtils.getBuildSector(sender.getName());
                                    sectorData.addPlayer(target.getName());
                                    PlayerUtils.sendMessage(sender, "Successfully added \"" + target.getName() + "\" to your build sector.");
                                }
                            } else PlayerUtils.sendMessage(sender, "Player \"" + args[1] + "\" doesn't exist.");
                        } else return false;
                        return true;
                    case "remove":
                        if(args.length == 2) {
                            PlayerState target = ServerUtils.getPlayerByName(args[1]);
                            if(target != null) {
                                if(target.equals(sender)) PlayerUtils.sendMessage(sender, "You can't remove yourself from your own build sector!");
                                else {
                                    BuildSectorData sectorData = DataUtils.getBuildSector(sender.getName());
                                    sectorData.removePlayer(target.getName());
                                    if(DataUtils.getPlayerCurrentBuildSector(target) == sectorData) DataUtils.movePlayerFromBuildSector(target);
                                    PlayerUtils.sendMessage(sender, "Successfully removed \"" + target.getName() + "\" from your build sector.");
                                }
                            } else PlayerUtils.sendMessage(sender, "Player \"" + args[1] + "\" doesn't exist.");
                        } else return false;
                        return true;
                    case "list":
                        if(args.length == 1) {
                            BuildSectorData sectorData = DataUtils.getBuildSector(sender.getName());
                            StringBuilder builder = new StringBuilder();
                            builder.append("Current Players:\n");
                            for(String playerName : sectorData.getAllowedPlayersByName()) builder.append(playerName).append("\n");
                            PlayerUtils.sendMessage(sender, builder.toString().trim());
                        } else return false;
                        return true;
                    case "allow":
                        if(args.length == 3) {
                            PlayerState target = ServerUtils.getPlayerByName(args[1]);
                            if(target != null) {
                                if(target.equals(sender)) PlayerUtils.sendMessage(sender, "You can't edit your own permissions!");
                                else {
                                    BuildSectorData sectorData = DataUtils.getBuildSector(sender.getName());
                                    sectorData.allowPermission(target.getName(), args[2]);
                                    PlayerUtils.sendMessage(sender, "Successfully set permission \"" + args[2] + "\" for \"" + target.getName() + "\" to ALLOW.");
                                }
                            } else PlayerUtils.sendMessage(sender, "Player \"" + args[1] + "\" doesn't exist.");
                        } else return false;
                        return true;
                    case "deny":
                        if(args.length == 3) {
                            PlayerState target = ServerUtils.getPlayerByName(args[1]);
                            if(target != null) {
                                if(target.equals(sender)) PlayerUtils.sendMessage(sender, "You can't edit your own permissions!");
                                else {
                                    BuildSectorData sectorData = DataUtils.getBuildSector(sender.getName());
                                    sectorData.denyPermission(target.getName(), args[2]);
                                    PlayerUtils.sendMessage(sender, "Successfully set permission \"" + args[2] + "\" for \"" + target.getName() + "\" to DENY.");
                                }
                              } else PlayerUtils.sendMessage(sender, "Player \"" + args[1] + "\" doesn't exist.");
                        } else return false;
                        return true;
                    case "permissions":
                        if(args.length == 2) {
                            PlayerState target = ServerUtils.getPlayerByName(args[1]);
                            if(target != null) {
                                BuildSectorData sectorData = DataUtils.getBuildSector(sender.getName());
                                StringBuilder builder = new StringBuilder();
                                builder.append("Current Permissions for \"").append(target.getName()).append("\":\n");
                                for(Map.Entry<String, Boolean> entry : sectorData.getPermissions(target.getName()).entrySet()) builder.append(entry.getKey()).append(" : ").append(entry.getValue().toString().toUpperCase()).append("\n");
                                PlayerUtils.sendMessage(sender, builder.toString().trim());
                            } else PlayerUtils.sendMessage(sender, "Player \"" + args[1] + "\" doesn't exist.");
                        } else return false;
                        return true;
                    case "spawn":
                        if(args.length == 2) {
                            if(!DataUtils.isPlayerInAnyBuildSector(sender)) PlayerUtils.sendMessage(sender, "You must be in a build sector to perform this command.");
                            else {
                                BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(sender);
                                if(sectorData.hasPermission(sender.getName(), "SPAWN")) {
                                    BlueprintEntry entry = BuildSectorUtils.getEntry(sender, args[1]);
                                    if(entry != null) {
                                        BuildSectorUtils.spawnEntry(sender, entry, !sectorData.allAIDisabled);
                                        PlayerUtils.sendMessage(sender, "Successfully spawned entity \"" + entry.getName() + "\".");
                                    } else PlayerUtils.sendMessage(sender, "Either you don't have catalog access to \"" + args[1] + "\" or the entry doesn't exist!");
                                } else PlayerUtils.sendMessage(sender, "You don't have permission to do this.");
                            }
                        } else return false;
                        return true;
                    case "spawn_enemy":
                        if(args.length == 2) {
                            if(!DataUtils.isPlayerInAnyBuildSector(sender)) PlayerUtils.sendMessage(sender, "You must be in a build sector to perform this command.");
                            else {
                                BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(sender);
                                if(sectorData.hasPermission(sender.getName(), "SPAWN_ENEMIES")) {
                                    BlueprintEntry entry = BuildSectorUtils.getEntry(sender, args[1]);
                                    if(entry != null) {
                                        BuildSectorUtils.spawnEnemy(sender, entry, !sectorData.allAIDisabled);
                                        PlayerUtils.sendMessage(sender, "Successfully spawned entity \"" + entry.getName() + "\" as an enemy.");
                                    } else PlayerUtils.sendMessage(sender, "Either you don't have catalog access to \"" + args[1] + "\" or the entry doesn't exist!");
                                } else PlayerUtils.sendMessage(sender, "You don't have permission to do this.");
                            }
                        } else return false;
                        return true;
                    case "delete":
                        if(args.length != 1 && args.length != 2) return false;
                        if(!DataUtils.isPlayerInAnyBuildSector(sender)) PlayerUtils.sendMessage(sender, "You must be in a build sector to perform this command.");
                        else {
                            BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(sender);
                            assert sectorData != null;
                            if(sectorData.hasPermission(sender.getName(), "DELETE")) {
                                if(args.length == 1) {
                                    if(GameCommon.getGameObject(sender.getSelectedEntityId()) != null && GameCommon.getGameObject(sender.getSelectedEntityId()) instanceof SegmentController) {
                                        SegmentController entity = (SegmentController) GameCommon.getGameObject(sender.getSelectedEntityId());
                                        PlayerUtils.sendMessage(sender, "Successfully deleted entity \"" + entity.getRealName() + "\".");
                                        DataUtils.destroyEntity(entity);
                                    } else PlayerUtils.sendMessage(sender, "You must either specify a valid entity by name or have one selected to perform this command.");
                                } else {
                                    SegmentController entity = BuildSectorUtils.getEntityByName(sender, args[1]);
                                    if(entity != null) {
                                        PlayerUtils.sendMessage(sender, "Successfully deleted entity \"" + entity.getRealName() + "\".");
                                        DataUtils.destroyEntity(entity);
                                    } else PlayerUtils.sendMessage(sender, "You must either specify a valid entity by name or have one selected to perform this command.");
                                }
                            } else PlayerUtils.sendMessage(sender, "You don't have permission to do this.");
                        }
                        return true;
                    case "toggle_ai":
                        if(args.length == 3) {
                            if(!DataUtils.isPlayerInAnyBuildSector(sender)) PlayerUtils.sendMessage(sender, "You must be in a build sector to perform this command.");
                            else {
                                BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(sender);
                                if(sectorData.hasPermission(sender.getName(), "TOGGLE_AI")) {
                                    String state = args[2].toLowerCase();
                                    if(state.equals("on") || state.equals("true") || state.equals("enabled") || state.equals("enable") || state.equals("activated") || state.equals("activate")) {
                                        if(args[1].toLowerCase().equals("*") || args[1].toLowerCase().equals("all")) {
                                            ArrayList<SegmentController> entityList = DataUtils.getEntitiesInBuildSector(sectorData);
                                            for(SegmentController entity : entityList) BuildSectorUtils.toggleAI(entity, true);
                                            PlayerUtils.sendMessage(sender, "Successfully activated all AI in build sector.");
                                        } else {
                                            SegmentController entity = BuildSectorUtils.getEntityByName(sender, args[1]);
                                            if(entity != null) {
                                                BuildSectorUtils.toggleAI(entity, true);
                                                PlayerUtils.sendMessage(sender, "Successfully activated AI for entity \"" + entity.getName() + "\".");
                                            } else PlayerUtils.sendMessage(sender, "There is no entity by the name of \"" + args[1] + "\" in the current build sector.");
                                        }
                                    } else if(state.equals("off") || state.equals("false") || state.equals("disabled") || state.equals("disable") || state.equals("deactivated") || state.equals("deactivate")) {
                                        if(args[1].toLowerCase().equals("*") || args[1].toLowerCase().equals("all")) {
                                            ArrayList<SegmentController> entityList = DataUtils.getEntitiesInBuildSector(sectorData);
                                            for(SegmentController entity : entityList) BuildSectorUtils.toggleAI(entity, false);
                                            PlayerUtils.sendMessage(sender, "Successfully deactivated all AI in build sector.");
                                        } else {
                                            SegmentController entity = BuildSectorUtils.getEntityByName(sender, args[1]);
                                            if(entity != null) {
                                                BuildSectorUtils.toggleAI(entity, false);
                                                PlayerUtils.sendMessage(sender, "Successfully deactivated AI for entity \"" + entity.getName() + "\".");
                                            } else PlayerUtils.sendMessage(sender, "There is no entity by the name of \"" + args[1] + "\" in the current build sector.");
                                        }
                                    } else return false;
                                } else PlayerUtils.sendMessage(sender, "You don't have permission to do this.");
                            }
                        } else return false;
                        return true;
                }
            } catch(Exception exception) {
                LogManager.logException("An exception occurred while trying to parse build_sector command", exception);
            }
        }
        return false;
    }

    @Override
    public void serverAction(@Nullable PlayerState sender, String[] args) {

    }

    @Override
    public StarMod getMod() {
        return EdenCore.getInstance();
    }
}
