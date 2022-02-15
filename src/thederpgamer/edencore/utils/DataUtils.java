package thederpgamer.edencore.utils;

import api.common.GameCommon;
import api.common.GameServer;
import api.mod.ModSkeleton;
import api.mod.config.PersistentObjectUtil;
import api.network.packets.PacketUtil;
import api.utils.StarRunnable;
import api.utils.game.PlayerUtils;
import api.utils.game.SegmentControllerUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import javax.vecmath.Vector3f;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.element.ElementDocking;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.FactionManager;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.data.player.inventory.InventorySlot;
import org.schema.game.common.data.world.Sector;
import org.schema.game.server.controller.SectorSwitch;
import org.schema.game.server.data.ServerConfig;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.data.other.PlayerData;
import thederpgamer.edencore.manager.ClientCacheManager;
import thederpgamer.edencore.manager.DataManager;
import thederpgamer.edencore.manager.LogManager;
import thederpgamer.edencore.network.client.RequestClientCacheUpdatePacket;

/**
 * Contains misc mod data utilities.
 *
 * @author TheDerpGamer
 * @version 2.0 - [09/08/2021]
 */
public class DataUtils {

  private static final ModSkeleton instance = EdenCore.getInstance().getSkeleton();

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

  /**
   * Clears potentially dangerous player data such as selected entities before moving them.
   *
   * @param playerState The player's state
   */
  public static void clearPlayerSelections(PlayerState playerState) {
    playerState.getNetworkObject().selectedAITargetId.set(-1);
    playerState.getNetworkObject().selectedEntityId.set(-1);

    playerState.getNetworkObject().selectedAITargetId.forceClientUpdates();
    playerState.getNetworkObject().selectedEntityId.forceClientUpdates();

    playerState.getControllerState().forcePlayerOutOfSegmentControllers();
  }

  /**
   * Saves an updated version of BuildSector data sent by it's owner to local storage and sends
   * updated data to other players.
   *
   * @param sectorData The updated sector data sent by the client that owns it
   */
  public static void updateBuildSector(BuildSectorData sectorData) {
    ArrayList<BuildSectorData> toRemove = new ArrayList<>();
    for (Object obj : PersistentObjectUtil.getObjects(instance, BuildSectorData.class)) {
      BuildSectorData temp = (BuildSectorData) obj;
      if (temp.ownerName.equals(sectorData.ownerName)) toRemove.add(temp);
    }

    for (BuildSectorData remove : toRemove) PersistentObjectUtil.removeObject(instance, remove);
    PersistentObjectUtil.addObject(instance, sectorData);
    PersistentObjectUtil.save(instance);
    EdenCore.getInstance().updateClientCacheData();
  }

  public static void movePlayerToBuildSector(
      final PlayerState playerState, final BuildSectorData sectorData)
      throws IOException, SQLException, NullPointerException {
    if (GameServer.getServerState() == null
        || !playerState.isOnServer()
        || DataUtils.isPlayerInAnyBuildSector(playerState)
        || playerState.isInTestSector()
        || playerState.isInPersonalSector()
        || playerState.isInTutorial()) return; // Make sure only the server executes this code
    clearPlayerSelections(playerState);

    final PlayerData playerData = getPlayerData(playerState);
    Vector3f lastRealSectorPos =
        new Vector3f(playerState.getFirstControlledTransformableWOExc().getWorldTransform().origin);
    playerData.lastRealSectorPos.set(lastRealSectorPos);
    playerData.lastRealSector.set(playerState.getCurrentSector());
    if (isBuildSector(playerData.lastRealSector)) {
      Vector3i defaultSector =
          (playerState.getFactionId() != 0)
              ? GameCommon.getGameState()
                  .getFactionManager()
                  .getFaction(playerState.getFactionId())
                  .getHomeSector()
              : getSpawnSector();
      playerData.lastRealSector.set(defaultSector);
      PlayerUtils.sendMessage(
          playerState,
          "An error occurred while trying to save your last position, so it has been reset to"
              + " prevent future issues.");
    }

    GameServer.getServerState()
        .getUniverse()
        .getStellarSystemFromSecPos(sectorData.sector); // Make sure system is generated
    if (!GameServer.getUniverse().isSectorLoaded(sectorData.sector))
      GameServer.getServerState().getUniverse().loadOrGenerateSector(sectorData.sector);
    final Sector sector = GameServer.getUniverse().getSector(sectorData.sector);

    sector.noEnter(false);
    sector.noExit(false);
    clearInventory(playerState, true);

    SectorSwitch sectorSwitch =
        new SectorSwitch(
            playerState.getAssingedPlayerCharacter(), sectorData.sector, SectorSwitch.TRANS_JUMP);
    sectorSwitch.makeCopy = false;
    sectorSwitch.jumpSpawnPos = playerData.lastBuildSectorPos;
    sectorSwitch.executionGraphicsEffect = (byte) 2;
    sectorSwitch.keepJumpBasisWithJumpPos = false;
    sectorSwitch.delay = System.currentTimeMillis();
    GameServer.getServerState().getSectorSwitches().add(sectorSwitch);
    playerState.getControllerState().forcePlayerOutOfSegmentControllers();
    playerState.setCurrentSector(sectorData.sector);
    playerState.setCurrentSectorId(sector.getSectorId());

    BuildSectorUtils.setPeace(sectorData, true);
    sector.noEnter(true);
    sector.noExit(true);
    // deleteEnemies(sectorData, 0);

    playerState.getInventory().sendAll();
    playerState.updateInventory();
  }

  public static void movePlayerFromBuildSector(final PlayerState playerState)
      throws IOException, SQLException, NullPointerException {
    if (GameServer.getServerState() == null || !playerState.isOnServer())
      return; // Make sure only the server executes this code
    clearPlayerSelections(playerState);

    Vector3f lastBuildSectorPos =
        new Vector3f(playerState.getFirstControlledTransformableWOExc().getWorldTransform().origin);
    playerState.getControllerState().forcePlayerOutOfSegmentControllers();
    final BuildSectorData sectorData = getPlayerCurrentBuildSector(playerState);
    final PlayerData playerData = getPlayerData(playerState);
    playerData.lastBuildSectorPos.set(lastBuildSectorPos);
    if (isBuildSector(playerData.lastRealSector)) {
      Vector3i defaultSector =
          (playerState.getFactionId() != 0)
              ? GameCommon.getGameState()
                  .getFactionManager()
                  .getFaction(playerState.getFactionId())
                  .getHomeSector()
              : getSpawnSector();
      playerData.lastRealSector.set(defaultSector);
      PlayerUtils.sendMessage(
          playerState,
          "An error occurred while trying to get your last position, so it has been reset to"
              + " prevent future issues.");
    }

    GameServer.getServerState()
        .getUniverse()
        .getStellarSystemFromSecPos(playerData.lastRealSector); // Make sure system is generated
    if (!GameServer.getUniverse().isSectorLoaded(playerData.lastRealSector))
      GameServer.getServerState().getUniverse().loadOrGenerateSector(playerData.lastRealSector);

    if (sectorData != null) {
      GameServer.getServerState().getUniverse().getSector(sectorData.sector).noEnter(false);
      GameServer.getServerState().getUniverse().getSector(sectorData.sector).noExit(false);
      clearInventory(playerState, true);
    }

    SectorSwitch sectorSwitch =
        new SectorSwitch(
            playerState.getAssingedPlayerCharacter(),
            playerData.lastRealSector,
            SectorSwitch.TRANS_JUMP);
    sectorSwitch.makeCopy = false;
    sectorSwitch.jumpSpawnPos = playerData.lastRealSectorPos;
    sectorSwitch.executionGraphicsEffect = (byte) 2;
    sectorSwitch.keepJumpBasisWithJumpPos = false;
    sectorSwitch.delay = System.currentTimeMillis();
    GameServer.getServerState().getSectorSwitches().add(sectorSwitch);
    playerState.getControllerState().forcePlayerOutOfSegmentControllers();
    playerState.setCurrentSector(playerData.lastRealSector);
    playerState.setCurrentSectorId(
        GameServer.getUniverse().getSector(playerData.lastRealSector).getSectorId());

    if (sectorData != null) {
      BuildSectorUtils.setPeace(sectorData, true);
      GameServer.getServerState().getUniverse().getSector(sectorData.sector).noEnter(true);
      GameServer.getServerState().getUniverse().getSector(sectorData.sector).noExit(true);
      // deleteEnemies(sectorData, 60);
    }
    runInventoryCheck(playerState);
  }

  public static void clearInventory(PlayerState playerState, boolean personalOnly) {
    if (!personalOnly) {
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
    if (playerState.isHasCreativeMode()
        || playerState.isUseCreativeMode()
        || playerState.getInventory().isInfinite()) return;
    boolean invalid = false;
    StringBuilder builder = new StringBuilder();
    builder
        .append("Found and removed the following invalid items in ")
        .append(playerState.getName())
        .append("'s inventory:\n");
    Inventory inventory = playerState.getInventory();
    for (Map.Entry<Integer, InventorySlot> entry : inventory.getMap().entrySet()) {
      if ((entry.getValue().isInfinite() || entry.getValue().count() >= 9999999)) {
        invalid = true;
        try {
          ElementInformation info = ElementKeyMap.getInfo(entry.getValue().getType());
          builder
              .append("- [")
              .append(entry.getKey().toString())
              .append("]: ")
              .append(info.getName())
              .append("(")
              .append(info.getId())
              .append(") x")
              .append(entry.getValue().count())
              .append("\n");
        } catch (Exception ignored) {
        }
        inventory.removeSlot(entry.getKey(), true);
      }
    }
    if (invalid) LogManager.logWarning(builder.toString().trim(), null);
  }

  public static void deleteEnemies(final BuildSectorData sectorData, long delay) {
    if (delay <= 0) {
      for (SegmentController entity : getEntitiesInBuildSector(sectorData)) {
        if (entity.getFactionId() == FactionManager.PIRATES_ID) destroyEntity(entity);
      }
    } else {
      new StarRunnable() {
        @Override
        public void run() {
          if (getPlayersInBuildSector(sectorData).isEmpty()) {
            for (SegmentController entity : getEntitiesInBuildSector(sectorData)) {
              if (entity.getFactionId() == FactionManager.PIRATES_ID) destroyEntity(entity);
            }
          }
        }
      }.runLater(EdenCore.getInstance(), delay);
    }
  }

  public static void deleteAllEntities(final BuildSectorData sectorData, long delay) {
    new StarRunnable() {
      @Override
      public void run() {
        try {
          for (SegmentController entity : getEntitiesInBuildSector(sectorData))
            destroyEntity(entity);
          GameServer.getUniverse().getSector(sectorData.sector).clearVicinity();
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    }.runLater(EdenCore.getInstance(), delay);
  }

  public static void destroyEntity(SegmentController entity) {
    for (PlayerState attached : SegmentControllerUtils.getAttachedPlayers(entity))
      attached.getControllerState().forcePlayerOutOfSegmentControllers();
    entity.railController.destroyDockedRecursive();
    for (ElementDocking dock : entity.getDockingController().getDockedOnThis()) {
      dock.from.getSegment().getSegmentController().markForPermanentDelete(true);
      dock.from.getSegment().getSegmentController().setMarkedForDeleteVolatile(true);
    }
    entity.markForPermanentDelete(true);
    entity.setMarkedForDeleteVolatile(true);
  }

  public static PlayerData getPlayerData(PlayerState playerState) {
    for (Object obj : PersistentObjectUtil.getObjects(instance, PlayerData.class)) {
      PlayerData playerData = (PlayerData) obj;
      if (playerData.playerName.equals(playerState.getName())) return playerData;
    }
    return createNewPlayerData(playerState);
  }

  public static PlayerData createNewPlayerData(PlayerState playerState) {
    PlayerData playerData = new PlayerData(playerState);
    PersistentObjectUtil.addObject(instance, playerData);
    return playerData;
  }

  public static ArrayList<BuildSectorData> getAllBuildSectors() {
    if (DataManager.getGameStateType().equals(DataManager.GameStateType.CLIENT))
      return ClientCacheManager.accessibleSectors;
    else {
      ArrayList<BuildSectorData> dataList = new ArrayList<>();
      for (Object obj : PersistentObjectUtil.getObjects(instance, BuildSectorData.class)) {
        dataList.add((BuildSectorData) obj);
      }
      return dataList;
    }
  }

  public static BuildSectorData getPlayerCurrentBuildSector(PlayerState playerState) {
    ArrayList<BuildSectorData> dataList = getAllBuildSectors();
    for (BuildSectorData sectorData : dataList) {
      try {
        if (sectorData != null && playerState.getCurrentSector().equals(sectorData.sector))
          return sectorData;
      } catch (Exception exception) {
        LogManager.logException(
            "Encountered an exception while trying to get "
                + playerState.getName()
                + "'s current build sector",
            exception);
      }
    }
    return null;
  }

  public static boolean isBuildSector(Vector3i sector) {
    // if(sector.x >= 100000000 || sector.y >= 100000000 || sector.z >= 100000000) return true; This
    // screws up test sectors
    for (Object object : PersistentObjectUtil.getObjects(instance, BuildSectorData.class)) {
      BuildSectorData data = (BuildSectorData) object;
      if (data.sector.equals(sector)) return true;
    }
    return false;
  }

  public static boolean isPlayerInAnyBuildSector(PlayerState playerState) {
    if (playerState == null) return false;
    Vector3i sector = playerState.getCurrentSector();
    // return (sector.x >= 100000000 || sector.y >= 100000000 || sector.z >= 100000000 ||
    // getPlayerCurrentBuildSector(playerState) != null);
    return getPlayerCurrentBuildSector(playerState) != null;
  }

  public static ArrayList<SegmentController> getEntitiesInBuildSector(BuildSectorData sectorData) {
    ArrayList<SegmentController> entityList = new ArrayList<>();
    if (GameCommon.isClientConnectedToServer()) {
      PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
      entityList.addAll(ClientCacheManager.sectorEntities);
    } else if (GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
      try {
        Set set = GameServer.getUniverse().getSector(sectorData.sector).getEntities();
        for (Object obj : set)
          if (obj instanceof SegmentController && !((SegmentController) obj).isVirtualBlueprint())
            entityList.add((SegmentController) obj);
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    }
    return entityList;
  }

  public static ArrayList<PlayerState> getPlayersInBuildSector(BuildSectorData sectorData) {
    ArrayList<PlayerState> playerList = new ArrayList<>();
    try {
      for (PlayerState playerState :
          GameServer.getUniverse()
              .getSector(sectorData.sector)
              .getRemoteSector()
              ._getCurrentPlayers()) {
        if (playerState.getCurrentSector().equals(sectorData.sector)) playerList.add(playerState);
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    return playerList;
  }

  public static BuildSectorData getBuildSector(String playerName) {
    if (GameCommon.isClientConnectedToServer()) {
      PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
      for (BuildSectorData sectorData : ClientCacheManager.accessibleSectors)
        if (sectorData != null
            && sectorData.ownerName != null
            && sectorData.ownerName.equals(playerName)) return sectorData;
    } else if (GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
      for (Object object : PersistentObjectUtil.getObjects(instance, BuildSectorData.class)) {
        BuildSectorData data = (BuildSectorData) object;
        if (data.ownerName.equals(playerName)) return data;
      }
      return createNewBuildSector(playerName);
    }
    return null;
  }

  public static boolean playerExists(String playerName) {
    for (Object object : PersistentObjectUtil.getObjects(instance, BuildSectorData.class)) {
      BuildSectorData data = (BuildSectorData) object;
      if (data.ownerName.equals(playerName)) return true;
    }
    return false;
  }

  private static BuildSectorData createNewBuildSector(String playerName) {
    Vector3i sector = getRandomSector(1000000000);
    BuildSectorData data =
        new BuildSectorData(playerName, sector, new HashMap<String, HashMap<String, Boolean>>());
    PersistentObjectUtil.addObject(instance, data);
    try {
      GameServer.getServerState()
          .getUniverse()
          .getStellarSystemFromSecPos(sector); // Make sure system is generated
      GameServer.getServerState().getUniverse().loadOrGenerateSector(sector);
      deleteAllEntities(data, 0);
    } catch (IOException | SQLException exception) {
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
