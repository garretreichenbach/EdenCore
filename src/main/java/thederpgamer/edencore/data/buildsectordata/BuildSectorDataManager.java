package thederpgamer.edencore.data.buildsectordata;

import api.mod.config.PersistentObjectUtil;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.DataManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		for(BuildSectorData data : clientCache) {
			if(data.isPlayerInBuildSector(playerState)) return true;
		}
		return false;
	}
}
