package thederpgamer.edencore.utils;

import api.common.GameClient;
import api.common.GameCommon;
import api.common.GameServer;
import api.mod.ModSkeleton;
import api.mod.config.PersistentObjectUtil;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.Sector;
import org.schema.game.server.controller.SectorSwitch;
import org.schema.game.server.data.ServerConfig;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.BuildSectorData;
import thederpgamer.edencore.data.ComparableData;
import thederpgamer.edencore.data.PlayerData;
import thederpgamer.edencore.manager.LogManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

/**
 * Contains misc mod data utilities.
 *
 * @author TheDerpGamer
 * @since 06/27/2021
 */
public class DataUtils {

    private static final ModSkeleton instance = EdenCore.getInstance().getSkeleton();

    public static void saveData() {
        PersistentObjectUtil.save(instance);
    }

    public static String getResourcesPath() {
        return instance.getResourcesFolder().getPath().replace('\\', '/');
    }

    public static String getWorldDataPath() {
        String universeName = GameCommon.getUniqueContextId();
        try {
            if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) return getResourcesPath() + "/data/" + universeName;
            else throw new IllegalAccessException("Cannot access server world data as a client.");
        } catch(IllegalAccessException exception) {
            LogManager.logException("Client " + GameClient.getClientPlayerState().getName() + " attempted to illegally access server data.", exception);
        }
        return null;
    }

    public static Vector3i getSpawnSector() {
        int x = (int) ServerConfig.DEFAULT_SPAWN_SECTOR_X.getCurrentState();
        int y = (int) ServerConfig.DEFAULT_SPAWN_SECTOR_Y.getCurrentState();
        int z = (int) ServerConfig.DEFAULT_SPAWN_SECTOR_Z.getCurrentState();
        return new Vector3i(x, y, z);
    }

    public static void removeExistingData(ComparableData data) {
        if(data instanceof PlayerData) {
            ArrayList<PlayerData> removeList = new ArrayList<>();
            for(Object obj : PersistentObjectUtil.getObjects(instance, PlayerData.class)) {
                if(obj instanceof PlayerData) {
                    PlayerData comp = (PlayerData) obj;
                    if(comp.equalTo(data)) removeList.add(comp);
                }
            }
            for(PlayerData comparableData : removeList) PersistentObjectUtil.removeObject(instance, comparableData);
        } else if(data instanceof BuildSectorData) {
            ArrayList<BuildSectorData> removeList = new ArrayList<>();
            for(Object obj : PersistentObjectUtil.getObjects(instance, BuildSectorData.class)) {
                if(obj instanceof BuildSectorData) {
                    BuildSectorData comp = (BuildSectorData) obj;
                    if(comp.equalTo(data)) removeList.add(comp);
                }
            }
            for(BuildSectorData comparableData : removeList) PersistentObjectUtil.removeObject(instance, comparableData);
        }
    }

    public static void movePlayerToBuildSector(final PlayerState playerState, final BuildSectorData sectorData) throws IOException {
        PlayerData playerData = getPlayerData(playerState);
        playerData.lastRealSector.set(playerState.getCurrentSector());
        removeExistingData(playerData);
        PersistentObjectUtil.addObject(instance, playerData);
        saveData();

        Sector sector = GameServer.getUniverse().getSector(sectorData.sector);
        sector.setActive(true);
        playerState.getControllerState().forcePlayerOutOfSegmentControllers();
        (GameServer.getServerState().getController().queueSectorSwitch(playerState.getAssingedPlayerCharacter(), sectorData.sector, SectorSwitch.TRANS_JUMP, false, true, true)).execute(GameServer.getServerState());
        playerState.updateInventory();
        LogManager.logDebug(playerState.getName() + " teleported to a build sector in " + sectorData.sector.toString());
    }

    public static void movePlayerFromBuildSector(final PlayerState playerState) throws IOException {
        PlayerData playerData = getPlayerData(playerState);
        final Vector3i pos = new Vector3i(playerState.getCurrentSector());
        GameServer.getUniverse().getSector(pos).noExit(true);
        GameServer.getUniverse().getSector(pos).noEnter(true);
        playerState.getControllerState().forcePlayerOutOfSegmentControllers();
        (GameServer.getServerState().getController().queueSectorSwitch(playerState.getAssingedPlayerCharacter(), playerData.lastRealSector, SectorSwitch.TRANS_JUMP, false, true, true)).execute(GameServer.getServerState());
        playerState.updateInventory();
        if(playerState.isGodMode() && !playerState.isAdmin()) playerState.setGodMode(false);
        LogManager.logDebug(playerState.getName() + " teleported from a build sector in " + pos.toString());
    }

    public static boolean isInEntity() {
        return GameClient.getClientPlayerState().isControllingCore() || GameClient.getClientState().isInFlightMode() || GameClient.getClientState().isInAnyStructureBuildMode() || GameClient.getClientState().isInFlightMode() || !GameClient.getClientState().isInCharacterBuildMode() || GameClient.getCurrentControl() instanceof SegmentController;
    }

    public static PlayerData getPlayerData(PlayerState playerState) {
        for(Object obj : PersistentObjectUtil.getObjects(instance, PlayerData.class)) {
            PlayerData playerData = (PlayerData) obj;
            if(playerData.playerId.equals(playerState.getUniqueIdentifier()) && playerData.playerName.equals(playerState.getName())) return playerData;
        }
        return createNewPlayerData(playerState);
    }

    public static PlayerData createNewPlayerData(PlayerState playerState) {
        String playerName = playerState.getName();
        String playerId = playerState.getUniqueIdentifier();
        Vector3i lastRealSector = (!playerState.isInPersonalSector() && !playerState.isInTestSector()) ? playerState.getCurrentSector() : getSpawnSector();
        PlayerData playerData = new PlayerData(playerName, playerId, lastRealSector);
        removeExistingData(playerData);
        PersistentObjectUtil.addObject(instance, playerData);
        saveData();
        return playerData;
    }

    public static ArrayList<BuildSectorData> getAllBuildSectors() {
        ArrayList<BuildSectorData> dataList = new ArrayList<>();
        for(Object obj : PersistentObjectUtil.getObjects(instance, BuildSectorData.class)) dataList.add((BuildSectorData) obj);
        return dataList;
    }

    public static BuildSectorData getPlayerCurrentBuildSector(PlayerState playerState) {
        ArrayList<BuildSectorData> dataList = getAllBuildSectors();
        for(BuildSectorData sectorData : dataList) {
            if(getPlayersInBuildSector(sectorData).contains(playerState)) return sectorData;
        }
        return null;
    }

    public static boolean isPlayerInAnyBuildSector(PlayerState playerState) {
        return getPlayerCurrentBuildSector(playerState) != null;
    }

    public static ArrayList<SegmentController> getEntitiesInBuildSector(BuildSectorData sectorData) {
        ArrayList<SegmentController> entityList = new ArrayList<>();
        try {
            Set set = GameServer.getUniverse().getSector(sectorData.sector).getEntities();
            for(Object obj : set) if(obj instanceof SegmentController) entityList.add((SegmentController) obj);
        } catch(IOException exception) {
            exception.printStackTrace();
        }
        return entityList;
    }

    public static ArrayList<PlayerState> getPlayersInBuildSector(BuildSectorData sectorData) {
        ArrayList<PlayerState> playerList = new ArrayList<>();
        try {
            playerList.addAll(GameServer.getUniverse().getSector(sectorData.sector).getRemoteSector()._getCurrentPlayers());
        } catch(IOException exception) {
            exception.printStackTrace();
        }
        return playerList;
    }

    public static BuildSectorData getBuildSector(PlayerState playerState) {
        for(Object object : PersistentObjectUtil.getObjects(instance, BuildSectorData.class)) {
            BuildSectorData data = (BuildSectorData) object;
            if(data.ownerId.equals(playerState.getUniqueIdentifier())) return data;
        }
        return createNewBuildSector(playerState);
    }

    private static BuildSectorData createNewBuildSector(PlayerState playerState) {
        String playerName = playerState.getName();
        String playerId = playerState.getUniqueIdentifier();
        Vector3i sector = getRandomSector(69420);
        String[] permissions = genDefaultOwnerPermissions(playerName);
        BuildSectorData data = new BuildSectorData(playerName, playerId, sector, permissions);
        removeExistingData(data);
        PersistentObjectUtil.addObject(instance, data);
        saveData();
        return data;
    }

    public static Vector3i getRandomSector(int offset) {
        Random random = new Random();
        int x = (int) (random.nextInt(offset) + (System.currentTimeMillis() / 10000));
        int y = (int) (random.nextInt(offset) + (System.currentTimeMillis() / 10000));
        int z = (int) (random.nextInt(offset) + (System.currentTimeMillis() / 10000));
        return new Vector3i(x, y, z);
    }

    private static String[] genDefaultOwnerPermissions(String ownerName) {
        return new String[] {ownerName + ":{place_blocks=true,remove_blocks=true,spawn_entities=true,delete_entities=true,use_weapons=true," +
                "invite_others=true,remove_others=true,use_god_mode=true,use_creative_mode=true,activate_blocks=true" +
                "enter_ships=true,exit_ships=true,faction_override=true,teleport_enter=true,}"};
    }
}