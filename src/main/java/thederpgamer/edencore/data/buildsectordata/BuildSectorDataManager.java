package thederpgamer.edencore.data.buildsectordata;

import api.common.GameServer;
import api.mod.config.PersistentObjectUtil;
import api.network.packets.PacketUtil;
import api.utils.game.PlayerUtils;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.data.ServerConfig;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.DataManager;
import thederpgamer.edencore.data.SerializableData;
import thederpgamer.edencore.manager.ConfigManager;
import thederpgamer.edencore.manager.PlayerActionManager;
import thederpgamer.edencore.network.PlayerActionCommandPacket;

import java.util.*;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class BuildSectorDataManager extends DataManager<BuildSectorData> {

	private final Set<BuildSectorData> clientCache = new HashSet<>();
	private static BuildSectorDataManager instance;

	public static BuildSectorDataManager getInstance() {
		return instance;
	}

	public static void initialize(boolean client) {
		instance = new BuildSectorDataManager();
		if(client) instance.requestFromServer();
	}

	@Override
	public Set<BuildSectorData> getServerCache() {
		List<Object> objects = PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), BuildSectorData.class);
		Set<BuildSectorData> data = new HashSet<>();
		for(Object object : objects) data.add((BuildSectorData) object);
		return data;
	}

	@Override
	public SerializableData.DataType getDataType() {
		return SerializableData.DataType.BUILD_SECTOR_DATA;
	}

	@Override
	public Set<BuildSectorData> getClientCache() {
		return Collections.unmodifiableSet(clientCache);
	}

	@Override
	public void addToClientCache(BuildSectorData data) {
		clientCache.add(data);
	}

	@Override
	public void removeFromClientCache(BuildSectorData data) {
		clientCache.remove(data);
	}

	@Override
	public void updateClientCache(BuildSectorData data) {
		clientCache.remove(data);
		clientCache.add(data);
	}

	@Override
	public void createMissingData(Object... args) {
		try {
			String playerName = args[0].toString();
			if(getFromPlayerName(playerName, true) == null) addData(new BuildSectorData(playerName), true);
		} catch(Exception exception) {
			EdenCore.getInstance().logException("An error occurred while initializing build sector data", exception);
		}
	}

	public boolean isPlayerInAnyBuildSector(PlayerState playerState) {
		doBoundsCheck(playerState);
		return getCurrentBuildSector(playerState) != null;
	}

	private void doBoundsCheck(PlayerState playerState) {
		if(playerState.isOnServer()) {
			Vector3i playerSector = playerState.getCurrentSector();
			int distanceMod = ConfigManager.getMainConfig().getInt("build_sector_distance_offset");
			int xAbs = Math.abs(playerSector.x);
			int yAbs = Math.abs(playerSector.y);
			int zAbs = Math.abs(playerSector.z);
			if(xAbs >= distanceMod || yAbs >= distanceMod || zAbs >= distanceMod) {
				EdenCore.getInstance().logWarning("Player " + playerState.getName() + " is not in a build sector, yet is outside the universe bounds. Teleporting to spawn...");
				playerState.getControllerState().forcePlayerOutOfSegmentControllers();
				playerState.getControllerState().forcePlayerOutOfShips();
				int spawnX = (int) ServerConfig.DEFAULT_SPAWN_SECTOR_X.getCurrentState();
				int spawnY = (int) ServerConfig.DEFAULT_SPAWN_SECTOR_Y.getCurrentState();
				int spawnZ = (int) ServerConfig.DEFAULT_SPAWN_SECTOR_Z.getCurrentState();
				GameServer.executeAdminCommand("change_sector " + spawnX + " " + spawnY + " " + spawnZ);
				PlayerUtils.sendMessage(playerState, "[WARNING] You are outside the universe bounds, yet there is no Build Sector at your current location. You have been teleported to spawn to prevent errors.");
			}
		}
	}

	public BuildSectorData getCurrentBuildSector(PlayerState playerState) {
		doBoundsCheck(playerState);
		for(BuildSectorData data : clientCache) {
			if(data.getSector().equals(playerState.getCurrentSector())) return data;
		}
		return null;
	}

	public boolean isBuildSector(Vector3i sector) {
		for(BuildSectorData data : clientCache) {
			if(data.getSector().equals(sector)) return true;
		}
		return false;
	}

	public static Vector3i calculateRandomSector() {
		int distanceMod = ConfigManager.getMainConfig().getInt("build_sector_distance_offset");
		Random random = new Random();
		int x = random.nextInt(distanceMod * 2) - distanceMod;
		int y = random.nextInt(distanceMod * 2) - distanceMod;
		int z = random.nextInt(distanceMod * 2) - distanceMod;
		return new Vector3i(x, y, z);
	}

	public Set<BuildSectorData> getAccessibleSectors(PlayerState playerState) {
		Set<BuildSectorData> accessibleSectors = new HashSet<>();
		for(BuildSectorData data : getCache(playerState.isOnServer())) {
			if(data.getPermissionsForUser(playerState.getName()) != null) accessibleSectors.add(data);
		}
		return accessibleSectors;
	}

	public BuildSectorData getFromPlayer(PlayerState player) {
		for(BuildSectorData data : getCache(player.isOnServer())) {
			if(data.getOwner().equals(player.getName())) return data;
		}
		return null;
	}
	
	public BuildSectorData getFromPlayerName(String playerName, boolean server) {
		for(BuildSectorData data : getCache(server)) {
			if(data.getOwner().equals(playerName)) return data;
		}
		return null;
	}

	public void enterBuildSector(PlayerState playerState, BuildSectorData buildSectorData) {
		if(playerState.isOnServer()) PacketUtil.sendPacket(playerState, new PlayerActionCommandPacket(PlayerActionManager.ENTER_BUILD_SECTOR, playerState.getName(), buildSectorData.getUUID()));
		else PacketUtil.sendPacketToServer(new PlayerActionCommandPacket(PlayerActionManager.ENTER_BUILD_SECTOR, playerState.getName(), buildSectorData.getUUID()));
	}
	
	public void leaveBuildSector(PlayerState playerState) {
		if(!playerState.isOnServer()) PacketUtil.sendPacket(playerState, new PlayerActionCommandPacket(PlayerActionManager.LEAVE_BUILD_SECTOR, playerState.getName()));
		else PacketUtil.sendPacketToServer(new PlayerActionCommandPacket(PlayerActionManager.LEAVE_BUILD_SECTOR, playerState.getName()));
	}
}
