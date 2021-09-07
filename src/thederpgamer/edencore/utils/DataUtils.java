package thederpgamer.edencore.utils;

import api.common.GameCommon;
import api.common.GameServer;
import api.mod.ModSkeleton;
import api.mod.config.PersistentObjectUtil;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.FactionRelation;
import org.schema.game.common.data.world.Sector;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.server.data.ServerConfig;
import org.schema.game.server.data.admin.AdminCommands;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.BuildSectorData;
import thederpgamer.edencore.data.ComparableData;
import thederpgamer.edencore.data.PlayerData;
import thederpgamer.edencore.manager.LogManager;

import java.io.IOException;
import java.util.*;

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
        return getResourcesPath() + "/data/" + GameCommon.getUniqueContextId();
    }

    public static Vector3i getSpawnSector() {
        int x = (int) ServerConfig.DEFAULT_SPAWN_SECTOR_X.getCurrentState();
        int y = (int) ServerConfig.DEFAULT_SPAWN_SECTOR_Y.getCurrentState();
        int z = (int) ServerConfig.DEFAULT_SPAWN_SECTOR_Z.getCurrentState();
        return new Vector3i(x, y, z);
    }

    public static String getEntityNameFormatted(SegmentController entity) {
        return entity.getRealName();
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

    public static boolean canTeleportPlayer(PlayerState playerState) throws IOException {
        Sector sector = GameServer.getUniverse().getSector(playerState.getCurrentSector());
        if(!isPlayerInAnyBuildSector(playerState)) {
            if(!Objects.requireNonNull(GameCommon.getGameState()).getFactionManager().getRelation(playerState.getFactionId(), sector.getFactionId()).equals(FactionRelation.RType.ENEMY)) {
                for(SimpleTransformableSendableObject<?> entity : sector.getEntities()) {
                    if(GameCommon.getGameState().getFactionManager().getRelation(playerState.getFactionId(), entity.getFactionId()).equals(FactionRelation.RType.ENEMY)) return false;
                }
                return true;
            }
        } else LogManager.logWarning("Player \"" + playerState.getName() + "\" attempted to teleport to their build sector while already inside it.", null);
        return false;
    }

    public static void movePlayerToBuildSector(final PlayerState playerState, final BuildSectorData sectorData) throws IOException {
        PlayerData playerData = getPlayerData(playerState);
        if(isPlayerInAnyBuildSector(playerState)) playerData.lastRealSector.set(2, 2, 2);
        else playerData.lastRealSector.set(new Vector3i(playerState.getCurrentSector()));
        removeExistingData(playerData);
        PersistentObjectUtil.addObject(instance, playerData);
        saveData();

        playerState.getControllerState().forcePlayerOutOfSegmentControllers();
        GameServer.getServerState().getController().enqueueAdminCommand(GameServer.getServerClient(playerState), AdminCommands.CHANGE_SECTOR, new Object[] {sectorData.sector.x, sectorData.sector.y, sectorData.sector.z});
        playerState.updateInventory();
        GameServer.getUniverse().getSector(sectorData.sector).noEnter(true);
        GameServer.getUniverse().getSector(sectorData.sector).noExit(true);
        LogManager.logDebug(playerState.getName() + " teleported to a build sector in " + sectorData.sector.toString());
    }

    public static void movePlayerFromBuildSector(final PlayerState playerState) throws IOException {
        PlayerData playerData = getPlayerData(playerState);
        final Vector3i pos = new Vector3i(playerState.getCurrentSector());
        GameServer.getUniverse().getSector(pos).noExit(false);
        GameServer.getUniverse().getSector(pos).noEnter(false);
        playerState.getControllerState().forcePlayerOutOfSegmentControllers();
        GameServer.getServerState().getController().enqueueAdminCommand(GameServer.getServerClient(playerState), AdminCommands.CHANGE_SECTOR, new Object[] {playerData.lastRealSector.x, playerData.lastRealSector.y, playerData.lastRealSector.z});
        playerState.updateInventory();
        playerState.setGodMode(false);
        GameServer.getUniverse().getSector(pos).noEnter(true);
        GameServer.getUniverse().getSector(pos).noExit(true);
        LogManager.logDebug(playerState.getName() + " teleported from a build sector in " + pos.toString());
    }
    
    public static void movePlayerToLastRealSector(final PlayerState playerState) throws IOException {
        final Vector3i pos = new Vector3i(playerState.getCurrentSector());
        boolean wasInBuildSector = isPlayerInAnyBuildSector(playerState);
        if(wasInBuildSector) {
            GameServer.getUniverse().getSector(pos).noExit(false);
            GameServer.getUniverse().getSector(pos).noEnter(false);
        }
        playerState.getControllerState().forcePlayerOutOfSegmentControllers();
        PlayerData playerData = getPlayerData(playerState);
        GameServer.getServerState().getController().enqueueAdminCommand(GameServer.getServerClient(playerState), AdminCommands.CHANGE_SECTOR, new Object[] {playerData.lastRealSector.x, playerData.lastRealSector.y, playerData.lastRealSector.z});
        playerState.updateInventory();
        playerState.setGodMode(false);
        if(wasInBuildSector) {
            GameServer.getUniverse().getSector(pos).noEnter(true);
            GameServer.getUniverse().getSector(pos).noExit(true);
        }
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
            if(playerState.getCurrentSector().equals(sectorData.sector)) return sectorData;
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
            for(PlayerState playerState : GameServer.getUniverse().getSector(sectorData.sector).getRemoteSector()._getCurrentPlayers()) {
                if(playerState.getCurrentSector().equals(sectorData.sector)) playerList.add(playerState);
            }
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
        BuildSectorData data = new BuildSectorData(playerName, playerId, sector, new HashMap<String, HashMap<String, Boolean>>());
        removeExistingData(data);
        PersistentObjectUtil.addObject(instance, data);
        saveData();
        return data;
    }

    public static Vector3i getRandomSector(int offset) {
        Random random = new Random();
        int x = (int) (random.nextInt(offset) + (System.currentTimeMillis() / 1000));
        int y = (int) (random.nextInt(offset) + (System.currentTimeMillis() / 1000));
        int z = (int) (random.nextInt(offset) + (System.currentTimeMillis() / 1000));
        return new Vector3i(x, y, z);
    }
}