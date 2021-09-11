package thederpgamer.edencore.utils;

import api.common.GameCommon;
import api.common.GameServer;
import api.mod.ModSkeleton;
import api.mod.config.PersistentObjectUtil;
import api.utils.StarRunnable;
import api.utils.game.PlayerUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.FactionManager;
import org.schema.game.common.data.player.faction.FactionRelation;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.data.player.inventory.InventorySlot;
import org.schema.game.common.data.player.inventory.VirtualCreativeModeInventory;
import org.schema.game.common.data.world.Sector;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.server.controller.SectorSwitch;
import org.schema.game.server.data.ServerConfig;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.BuildSectorData;
import thederpgamer.edencore.data.PlayerData;
import thederpgamer.edencore.manager.LogManager;

import javax.vecmath.Vector3f;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Contains misc mod data utilities.
 *
 * @author TheDerpGamer
 * @version 2.0 - [09/08/2021]
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

    public static void removeExistingData(Object data) {
        if(data instanceof PlayerData) {
            ArrayList<PlayerData> removeList = new ArrayList<>();
            for(Object obj : PersistentObjectUtil.getObjects(instance, PlayerData.class)) {
                if(obj.equals(data)) removeList.add((PlayerData) obj);
            }
            for(PlayerData comparableData : removeList) PersistentObjectUtil.removeObject(instance, comparableData);
        } else if(data instanceof BuildSectorData) {
            ArrayList<BuildSectorData> removeList = new ArrayList<>();
            for(Object obj : PersistentObjectUtil.getObjects(instance, BuildSectorData.class)) {
                if(obj.equals(data)) removeList.add((BuildSectorData) obj);
            }
            for(BuildSectorData comparableData : removeList) {
                PersistentObjectUtil.removeObject(instance, comparableData);
            }
        }
    }

    public static boolean canTeleportPlayer(PlayerState playerState) throws IOException {
        Sector sector = GameServer.getUniverse().getSector(playerState.getCurrentSector());
        if(!isPlayerInAnyBuildSector(playerState)) {
            if(!Objects.requireNonNull(GameCommon.getGameState()).getFactionManager().getRelation(playerState.getFactionId(), sector.getFactionId()).equals(FactionRelation.RType.ENEMY)) {
                for(SimpleTransformableSendableObject<?> entity : sector.getEntities()) {
                    if(GameCommon.getGameState().getFactionManager().getRelation(playerState.getFactionId(), entity.getFactionId()).equals(FactionRelation.RType.ENEMY))
                        return false;
                }
                return true;
            }
        } else
            LogManager.logWarning("Player \"" + playerState.getName() + "\" attempted to teleport to their build sector while already inside it.", null);
        return false;
    }

    public static void movePlayerToBuildSector(final PlayerState playerState, final BuildSectorData sectorData) throws IOException, SQLException, NullPointerException {
        if(GameServer.getServerState() == null || !playerState.isOnServer() || DataUtils.isPlayerInAnyBuildSector(playerState) || playerState.isInTestSector() || playerState.isInPersonalSector() || playerState.isInTutorial()) return; //Make sure only the server executes this code
        playerState.getControllerState().forcePlayerOutOfSegmentControllers();
        final PlayerData playerData = getPlayerData(playerState);
        Vector3f lastRealSectorPos = new Vector3f(playerState.getFirstControlledTransformableWOExc().getWorldTransform().origin);
        playerData.lastRealSectorPos.set(lastRealSectorPos);
        if(playerState.getCurrentSector().length() > 1000000 || playerData.lastRealSector.equals(sectorData.sector)) return;
        else playerData.lastRealSector.set(playerState.getCurrentSector());
        if(playerData.lastRealSector.length() > 1000000 || playerData.lastRealSector.equals(sectorData.sector)) {
            Vector3i defaultSector = (playerState.getFactionId() != 0) ? GameCommon.getGameState().getFactionManager().getFaction(playerState.getFactionId()).getHomeSector() : new Vector3i(2, 2, 2);
            playerData.lastRealSector.set(defaultSector);
            PlayerUtils.sendMessage(playerState, "An error occurred while trying to save your last position, so it has been reset to prevent future issues.");
        }

        GameServer.getServerState().getUniverse().getStellarSystemFromSecPos(sectorData.sector); //Make sure system is generated
        if(!GameServer.getUniverse().isSectorLoaded(sectorData.sector)) GameServer.getServerState().getUniverse().loadOrGenerateSector(sectorData.sector);
        final Sector sector = GameServer.getUniverse().getSector(sectorData.sector);

        sector.noEnter(false);
        sector.noExit(false);

        SectorSwitch sectorSwitch = new SectorSwitch(playerState.getAssingedPlayerCharacter(), sectorData.sector, SectorSwitch.TRANS_JUMP);
        sectorSwitch.makeCopy = false;
        sectorSwitch.jumpSpawnPos = playerData.lastBuildSectorPos;
        sectorSwitch.executionGraphicsEffect = (byte) 2;
        sectorSwitch.keepJumpBasisWithJumpPos = true;
        sectorSwitch.delay = System.currentTimeMillis();
        GameServer.getServerState().getSectorSwitches().add(sectorSwitch);
        playerState.getControllerState().forcePlayerOutOfSegmentControllers();
        playerState.setCurrentSector(sectorData.sector);
        playerState.setCurrentSectorId(sector.getSectorId());

        sector.noEnter(true);
        sector.noExit(true);
        deleteEnemies(sectorData, 60);

        playerState.instantiateInventoryServer(true); //Backup and clear the player's inventory
        playerState.setInventory(new VirtualCreativeModeInventory(playerState, PlayerState.NORM_INV));
    }

    public static void movePlayerFromBuildSector(final PlayerState playerState) throws IOException, SQLException, NullPointerException {
        if(GameServer.getServerState() == null || !playerState.isOnServer()) return; //Make sure only the server executes this code
        playerState.getControllerState().forcePlayerOutOfSegmentControllers();
        final BuildSectorData sectorData = getPlayerCurrentBuildSector(playerState);
        final PlayerData playerData = getPlayerData(playerState);
        Vector3f lastBuildSectorPos = new Vector3f(playerState.getFirstControlledTransformableWOExc().getWorldTransform().origin);
        playerData.lastBuildSectorPos.set(lastBuildSectorPos);
        if(playerData.lastRealSector.length() > 1000000 || (sectorData != null && playerData.lastRealSector.equals(sectorData.sector))) {
            Vector3i defaultSector = (playerState.getFactionId() != 0) ? GameCommon.getGameState().getFactionManager().getFaction(playerState.getFactionId()).getHomeSector() : new Vector3i(2, 2, 2);
            playerData.lastRealSector.set(defaultSector);
            PlayerUtils.sendMessage(playerState, "An error occurred while trying to get your last position, so it has been reset to prevent future issues.");
        }

        GameServer.getServerState().getUniverse().getStellarSystemFromSecPos(playerData.lastRealSector); //Make sure system is generated
        if(!GameServer.getUniverse().isSectorLoaded(playerData.lastRealSector)) GameServer.getServerState().getUniverse().loadOrGenerateSector(playerData.lastRealSector);

        if(sectorData != null) {
            GameServer.getServerState().getUniverse().getSector(sectorData.sector).noEnter(false);
            GameServer.getServerState().getUniverse().getSector(sectorData.sector).noExit(false);
        }

        clearInventory(playerState);

        SectorSwitch sectorSwitch = new SectorSwitch(playerState.getAssingedPlayerCharacter(), playerData.lastRealSector, SectorSwitch.TRANS_JUMP);
        sectorSwitch.makeCopy = false;
        sectorSwitch.jumpSpawnPos = playerData.lastRealSectorPos;
        sectorSwitch.executionGraphicsEffect = (byte) 2;
        sectorSwitch.keepJumpBasisWithJumpPos = true;
        sectorSwitch.delay = System.currentTimeMillis();
        GameServer.getServerState().getSectorSwitches().add(sectorSwitch);
        playerState.getControllerState().forcePlayerOutOfSegmentControllers();
        playerState.setCurrentSector(playerData.lastRealSector);
        playerState.setCurrentSectorId(GameServer.getUniverse().getSector(playerData.lastRealSector).getSectorId());

        if(sectorData != null) {
            GameServer.getServerState().getUniverse().getSector(sectorData.sector).noEnter(true);
            GameServer.getServerState().getUniverse().getSector(sectorData.sector).noExit(true);
            deleteEnemies(sectorData, 60);
        }

        playerState.loadInventoryBackupServer(); //Load the player's inventory from backup
        runInventoryCheck(playerState);
    }

    public static void clearInventory(PlayerState playerState) {
        {
            IntOpenHashSet changed = new IntOpenHashSet();
            playerState.getInventory().clear(changed);
            playerState.getInventory().sendInventoryModification(changed);
        }

        {
            IntOpenHashSet changed = new IntOpenHashSet();
            playerState.getPersonalFactoryInventoryCapsule().clear(changed);
            playerState.getPersonalFactoryInventoryCapsule().sendInventoryModification(changed);
        }

        {
            IntOpenHashSet changed = new IntOpenHashSet();
            playerState.getPersonalFactoryInventoryMicro().clear(changed);
            playerState.getPersonalFactoryInventoryMicro().sendInventoryModification(changed);
        }

        {
            IntOpenHashSet changed = new IntOpenHashSet();
            playerState.getPersonalFactoryInventoryMacroBlock().clear(changed);
            playerState.getPersonalFactoryInventoryMacroBlock().sendInventoryModification(changed);
        }
    }

    /**
     * Removes any infinite items in a player's inventory to prevent potential exploits.
     *
     * @param playerState The playerState
     */
    public static void runInventoryCheck(PlayerState playerState) {
        if(playerState.isHasCreativeMode() || playerState.isUseCreativeMode() || playerState.getInventory().isInfinite()) return;
        boolean invalid = false;
        StringBuilder builder = new StringBuilder();
        builder.append("Found and removed the following invalid items in ").append(playerState.getName()).append("'s inventory:\n");
        Inventory inventory = playerState.getInventory();
        for(Map.Entry<Integer, InventorySlot> entry : inventory.getMap().entrySet()) {
            if((entry.getValue().isInfinite() || entry.getValue().count() >= 9999999)) {
                invalid = true;
                try {
                    ElementInformation info = ElementKeyMap.getInfo(entry.getValue().getType());
                    builder.append("- [").append(entry.getKey().toString()).append("]: ").append(info.getName()).append("(").append(info.getId()).append(") x").append(entry.getValue().count()).append("\n");
                } catch(Exception ignored) { }
                inventory.removeSlot(entry.getKey(), true);
            }
        }
        if(invalid) LogManager.logWarning(builder.toString().trim(), null);
    }

    public static void deleteEnemies(final BuildSectorData sectorData, long delay) {
        new StarRunnable() {
            @Override
            public void run() {
                if(getPlayersInBuildSector(sectorData).isEmpty()) {
                    for(SegmentController entity : getEntitiesInBuildSector(sectorData)) {
                        if(entity.getFactionId() == FactionManager.PIRATES_ID) entity.destroy();
                    }
                }
            }
        }.runLater(EdenCore.getInstance(), delay);
    }

    public static void deleteAllEntities(final BuildSectorData sectorData, long delay) {
        new StarRunnable() {
            @Override
            public void run() {
                try {
                    for(SegmentController entity : getEntitiesInBuildSector(sectorData)) entity.destroy();
                    GameServer.getUniverse().getSector(sectorData.sector).clearVicinity();
                } catch(IOException exception) {
                    exception.printStackTrace();
                }
            }
        }.runLater(EdenCore.getInstance(), delay);
    }

    public static PlayerData getPlayerData(PlayerState playerState) {
        for(Object obj : PersistentObjectUtil.getObjects(instance, PlayerData.class)) {
            PlayerData playerData = (PlayerData) obj;
            if(playerData.playerName.equals(playerState.getName())) return playerData;
        }
        return createNewPlayerData(playerState);
    }

    public static PlayerData createNewPlayerData(PlayerState playerState) {
        PlayerData playerData = new PlayerData(playerState);
        PersistentObjectUtil.addObject(instance, playerData);
        return playerData;
    }

    public static ArrayList<BuildSectorData> getAllBuildSectors() {
        ArrayList<BuildSectorData> dataList = new ArrayList<>();
        for(Object obj : PersistentObjectUtil.getObjects(instance, BuildSectorData.class)) {
            dataList.add((BuildSectorData) obj);
        }
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
        return playerState.getCurrentSector().length() > 1000000 || getPlayerCurrentBuildSector(playerState) != null;
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
            if(data.ownerName.equals(playerState.getName())) return data;
        }
        return createNewBuildSector(playerState);
    }

    private static BuildSectorData createNewBuildSector(PlayerState playerState) {
        String playerName = playerState.getName();
        Vector3i sector = getRandomSector(100000000);
        BuildSectorData data = new BuildSectorData(playerName, sector, new HashMap<String, HashMap<String, Boolean>>());
        PersistentObjectUtil.addObject(instance, data);
        try {
            GameServer.getServerState().getUniverse().getStellarSystemFromSecPos(sector); //Make sure system is generated
            GameServer.getServerState().getUniverse().loadOrGenerateSector(sector);
            deleteAllEntities(data, 0);
        } catch(IOException | SQLException exception) {
            exception.printStackTrace();
        }
        return data;
    }

    public static Vector3i getRandomSector(int offset) {
        Random random = new Random();
        int x = random.nextInt(offset) + 694200;
        int y = random.nextInt(offset) + 694200;
        int z = random.nextInt(offset) + 694200;
        return new Vector3i(x, y, z);
    }
}