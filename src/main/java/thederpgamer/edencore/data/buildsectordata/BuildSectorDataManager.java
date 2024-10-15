package thederpgamer.edencore.data.buildsectordata;

import api.mod.config.PersistentObjectUtil;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.DataManager;
import thederpgamer.edencore.manager.ConfigManager;

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

	public boolean isPlayerInAnyBuildSector(PlayerState playerState) {
		return getCurrentBuildSector(playerState) != null;
	}
	
	public BuildSectorData getCurrentBuildSector(PlayerState playerState) {
		for(BuildSectorData data : clientCache) {
			if(data.getSector().equals(playerState.getCurrentSector())) return data;
		}
		return null;
	}
	
	public static Vector3i calculateRandomSector() {
		int baseOffset = ConfigManager.getMainConfig().getInt("build_sector_distance_offset");
		Random random = new Random();
		int x = (int) (random.nextGaussian() * baseOffset) * (random.nextBoolean() ? 1 : -1);
		int y = (int) (random.nextGaussian() * baseOffset) * (random.nextBoolean() ? 1 : -1);
		int z = (int) (random.nextGaussian() * baseOffset) * (random.nextBoolean() ? 1 : -1);
		return new Vector3i(x, y, z);
	}
}
