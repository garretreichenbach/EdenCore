package thederpgamer.edencore.commands;

import api.common.GameCommon;
import api.common.GameServer;
import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.catalog.CatalogPermission;
import org.schema.game.common.data.player.faction.FactionManager;
import org.schema.game.server.ai.SegmentControllerAIEntity;
import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.controller.EntityAlreadyExistsException;
import org.schema.game.server.controller.EntityNotFountException;
import org.schema.game.server.data.GameServerState;
import org.schema.game.server.data.blueprint.ChildStats;
import org.schema.game.server.data.blueprint.SegmentControllerOutline;
import org.schema.game.server.data.blueprint.SegmentControllerSpawnCallbackDirect;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.core.settings.StateParameterNotFoundException;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.manager.LogManager;
import thederpgamer.edencore.utils.DataUtils;
import thederpgamer.edencore.utils.ServerDatabase;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.io.IOException;
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
                                else DataUtils.movePlayerToBuildSector(sender, DataUtils.getBuildSector(sender));
                                return true;
                            } else if(args.length == 2) {
                                PlayerState target = ServerDatabase.getPlayerByName(args[1]);
                                if(target != null) {
                                    BuildSectorData sectorData = DataUtils.getBuildSector(target);
                                    if(sectorData.hasPermission(sender.getName(), "ENTER")) {
                                        if(PlayerUtils.getCurrentControl(sender) instanceof SegmentController) PlayerUtils.sendMessage(sender, "You can't do this while in an entity.");
                                        else DataUtils.movePlayerToBuildSector(sender, DataUtils.getBuildSector(sender));
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
                            PlayerState target = ServerDatabase.getPlayerByName(args[1]);
                            if(target != null) {
                                if(target.equals(sender)) PlayerUtils.sendMessage(sender, "You can't invite yourself!");
                                else {
                                    BuildSectorData sectorData = DataUtils.getBuildSector(sender);
                                    sectorData.addPlayer(target.getName());
                                    PlayerUtils.sendMessage(sender, "Successfully added \"" + target.getName() + "\" to your build sector.");
                                }
                            } else PlayerUtils.sendMessage(sender, "Player \"" + args[1] + "\" doesn't exist.");
                        } else return false;
                        return true;
                    case "remove":
                        if(args.length == 2) {
                            PlayerState target = ServerDatabase.getPlayerByName(args[1]);
                            if(target != null) {
                                if(target.equals(sender)) PlayerUtils.sendMessage(sender, "You can't remove yourself from your own build sector!");
                                else {
                                    BuildSectorData sectorData = DataUtils.getBuildSector(sender);
                                    sectorData.removePlayer(target.getName());
                                    if(DataUtils.getPlayerCurrentBuildSector(target) == sectorData) DataUtils.movePlayerFromBuildSector(target);
                                    PlayerUtils.sendMessage(sender, "Successfully removed \"" + target.getName() + "\" from your build sector.");
                                }
                            } else PlayerUtils.sendMessage(sender, "Player \"" + args[1] + "\" doesn't exist.");
                        } else return false;
                        return true;
                    case "list":
                        if(args.length == 1) {
                            BuildSectorData sectorData = DataUtils.getBuildSector(sender);
                            StringBuilder builder = new StringBuilder();
                            builder.append("Current Players:\n");
                            for(String playerName : sectorData.getAllowedPlayersByName()) builder.append(playerName).append("\n");
                            PlayerUtils.sendMessage(sender, builder.toString().trim());
                        } else return false;
                        return true;
                    case "allow":
                        if(args.length == 3) {
                            PlayerState target = ServerDatabase.getPlayerByName(args[1]);
                            if(target != null) {
                                if(target.equals(sender)) PlayerUtils.sendMessage(sender, "You can't edit your own permissions!");
                                else {
                                    BuildSectorData sectorData = DataUtils.getBuildSector(sender);
                                    sectorData.allowPermission(target.getName(), args[2]);
                                    PlayerUtils.sendMessage(sender, "Successfully set permission \"" + args[2] + "\" for \"" + target.getName() + "\" to ALLOW.");
                                }
                            } else PlayerUtils.sendMessage(sender, "Player \"" + args[1] + "\" doesn't exist.");
                        } else return false;
                        return true;
                    case "deny":
                        if(args.length == 3) {
                            PlayerState target = ServerDatabase.getPlayerByName(args[1]);
                            if(target != null) {
                                if(target.equals(sender)) PlayerUtils.sendMessage(sender, "You can't edit your own permissions!");
                                else {
                                    BuildSectorData sectorData = DataUtils.getBuildSector(sender);
                                    sectorData.denyPermission(target.getName(), args[2]);
                                    PlayerUtils.sendMessage(sender, "Successfully set permission \"" + args[2] + "\" for \"" + target.getName() + "\" to DENY.");
                                }
                              } else PlayerUtils.sendMessage(sender, "Player \"" + args[1] + "\" doesn't exist.");
                        } else return false;
                        return true;
                    case "permissions":
                        if(args.length == 2) {
                            PlayerState target = ServerDatabase.getPlayerByName(args[1]);
                            if(target != null) {
                                BuildSectorData sectorData = DataUtils.getBuildSector(sender);
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
                                    BlueprintEntry entry = getEntry(sender, args[1]);
                                    if(entry != null) {
                                        spawnEntry(sender, entry, !sectorData.allAIDisabled);
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
                                    BlueprintEntry entry = getEntry(sender, args[1]);
                                    if(entry != null) {
                                        spawnEnemy(sender, entry, !sectorData.allAIDisabled);
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
                                    SegmentController entity = getEntityByName(sender, args[1]);
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
                                            for(SegmentController entity : entityList) toggleAI(entity, true);
                                            PlayerUtils.sendMessage(sender, "Successfully activated all AI in build sector.");
                                        } else {
                                            SegmentController entity = getEntityByName(sender, args[1]);
                                            if(entity != null) {
                                                toggleAI(entity, true);
                                                PlayerUtils.sendMessage(sender, "Successfully activated AI for entity \"" + entity.getName() + "\".");
                                            } else PlayerUtils.sendMessage(sender, "There is no entity by the name of \"" + args[1] + "\" in the current build sector.");
                                        }
                                    } else if(state.equals("off") || state.equals("false") || state.equals("disabled") || state.equals("disable") || state.equals("deactivated") || state.equals("deactivate")) {
                                        if(args[1].toLowerCase().equals("*") || args[1].toLowerCase().equals("all")) {
                                            ArrayList<SegmentController> entityList = DataUtils.getEntitiesInBuildSector(sectorData);
                                            for(SegmentController entity : entityList) toggleAI(entity, false);
                                            PlayerUtils.sendMessage(sender, "Successfully deactivated all AI in build sector.");
                                        } else {
                                            SegmentController entity = getEntityByName(sender, args[1]);
                                            if(entity != null) {
                                                toggleAI(entity, false);
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

    private BlueprintEntry getEntry(PlayerState sender, String entryName) {
        for(CatalogPermission permission : sender.getCatalog().getAvailableCatalog()) {
            try {
                BlueprintEntry blueprintEntry = BluePrintController.active.getBlueprint(permission.getUid());
                if(blueprintEntry.getName().toLowerCase().contains(entryName.toLowerCase())) return blueprintEntry;
            } catch(EntityNotFountException ignored) { }
        }
        return null;
    }

    private void spawnEntry(PlayerState sender, BlueprintEntry entry, boolean aiEnabled) {
        Transform transform = new Transform();
        transform.setIdentity();
        transform.origin.set(sender.getFirstControlledTransformableWOExc().getWorldTransform().origin);
        Vector3f forward = GlUtil.getForwardVector(new Vector3f(), transform);
        Vector3f size = entry.getBb().calculateHalfSize(new Vector3f());
        size.scale(0.5f);
        forward.scaleAdd(1.15f, size);
        transform.origin.set(forward);
        try {
            SegmentControllerOutline<?> outline = BluePrintController.active.loadBluePrint(
                    GameServerState.instance,
                    entry.getName(),
                    entry.getName(),
                    transform,
                    -1,
                    sender.getFactionId(),
                    sender.getCurrentSector(),
                    sender.getName(),
                    PlayerState.buffer,
                    null,
                    false,
                    new ChildStats(false));
            SegmentController entity = outline.spawn(sender.getCurrentSector(), false, new ChildStats(false), new SegmentControllerSpawnCallbackDirect(GameServer.getServerState(), sender.getCurrentSector()) {
                @Override
                public void onNoDocker() {

                }
            });
            toggleAI(entity, aiEnabled);
        } catch(EntityNotFountException | IOException | EntityAlreadyExistsException | StateParameterNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    private void spawnEnemy(PlayerState sender, BlueprintEntry entry, boolean aiEnabled) {
        Transform transform = new Transform();
        transform.setIdentity();
        transform.origin.set(sender.getFirstControlledTransformableWOExc().getWorldTransform().origin);
        Vector3f forward = GlUtil.getForwardVector(new Vector3f(), transform);
        Vector3f size = entry.getBb().calculateHalfSize(new Vector3f());
        size.scale(0.5f);
        forward.scaleAdd(1.15f, size);
        transform.origin.set(forward);
        try {
            SegmentControllerOutline<?> outline = BluePrintController.active.loadBluePrint(
                    GameServerState.instance,
                    entry.getName(),
                    entry.getName(),
                    transform,
                    -1,
                    FactionManager.PIRATES_ID,
                    sender.getCurrentSector(),
                    sender.getName(),
                    PlayerState.buffer,
                    null,
                    false,
                    new ChildStats(false));
            SegmentController entity = outline.spawn(sender.getCurrentSector(), false, new ChildStats(false), new SegmentControllerSpawnCallbackDirect(GameServer.getServerState(), sender.getCurrentSector()) {
                @Override
                public void onNoDocker() {

                }
            });
            toggleAI(entity, aiEnabled);
        } catch(EntityNotFountException | IOException | EntityAlreadyExistsException | StateParameterNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    private SegmentController getEntityByName(PlayerState sender, String entityName) {
        Map<String, SegmentController> entityMap = GameServer.getServerState().getSegmentControllersByName();
        for(Map.Entry<String, SegmentController> entry : entityMap.entrySet()) {
            if(entry.getKey().toLowerCase().contains(entityName.toLowerCase())) {
                if(entry.getValue().getSectorId() == sender.getSectorId()) return entry.getValue();
            }
        }
        return null;
    }

    private SegmentControllerAIEntity<?> getAIEntity(SegmentController entity) {
        try {
            switch(entity.getType()) {
                case SHIP: return ((Ship) entity).getAiConfiguration().getAiEntityState();
                case SPACE_STATION: return ((SpaceStation) entity).getAiConfiguration().getAiEntityState();
                default: throw new IllegalArgumentException("Entity must either be a Ship or Station!");
            }
        } catch(IllegalArgumentException exception) {
            LogManager.logCritical("A critical exception occurred while trying to get an AIEntityState from an invalid or corrupted entity!", exception);
        }
        return null;
    }

    private void toggleAI(SegmentController entity, final boolean toggle) {
        final SegmentControllerAIEntity<?>[] aiEntity = {getAIEntity(entity)};
        if(aiEntity[0] != null) {
            if(!toggle && aiEntity[0].getCurrentProgram() != null) aiEntity[0].getCurrentProgram().suspend(true);
            else if(aiEntity[0].getCurrentProgram() != null) aiEntity[0].getCurrentProgram().suspend(false);
        }
        final ArrayList<SegmentController> dockedList = new ArrayList<SegmentController>();
        entity.railController.getDockedRecusive(dockedList);

        final long time = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(SegmentController docked : dockedList) {
                    long current = System.currentTimeMillis();
                    long diff = current - time;
                    if(diff >= MAX_WAIT_TIME) { //Shitty method to make sure game thread doesn't stall
                        LogManager.logWarning("Toggling ai on entity \"" + docked.getRealName() + "\" took more than " + MAX_WAIT_TIME + " (" + diff + "ms)", null);
                        return;
                    }

                    try {
                        aiEntity[0] = getAIEntity(docked);
                        if(aiEntity[0] != null) {
                            if(!toggle && aiEntity[0].getCurrentProgram() != null) aiEntity[0].getCurrentProgram().suspend(true);
                            else if(aiEntity[0].getCurrentProgram() != null) aiEntity[0].getCurrentProgram().suspend(false);
                        }
                    } catch(Exception exception) {
                        LogManager.logException("Encountered an exception while toggling ai for entity \"" + docked.getRealName() + "\".", exception);
                    }
                }
            }
        });
    }
}
