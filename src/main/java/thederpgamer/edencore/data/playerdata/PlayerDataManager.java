package thederpgamer.edencore.data.playerdata;

import api.mod.config.PersistentObjectUtil;
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
public class PlayerDataManager extends DataManager<PlayerData> {

	private final Set<PlayerData> clientCache = new HashSet<>();
	private static PlayerDataManager instance;
	public static PlayerDataManager getInstance() {
		return instance;
	}

	public static void initialize(boolean client) {
		instance = new PlayerDataManager();
		if(client) instance.requestFromServer();
	}
	
	@Override
	public Set<PlayerData> getServerCache() {
		List<Object> objects = PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), PlayerData.class);
		Set<PlayerData> data = new HashSet<>();
		for(Object object : objects) data.add((PlayerData) object);
		return data;
	}

	@Override
	public Set<PlayerData> getClientCache() {
		return Collections.unmodifiableSet(clientCache);
	}

	@Override
	public void addToClientCache(PlayerData data) {
		clientCache.add(data);
	}

	@Override
	public void removeFromClientCache(PlayerData data) {
		clientCache.remove(data);
	}

	@Override
	public void updateClientCache(PlayerData data) {
		clientCache.remove(data);
		clientCache.add(data);
	}
}
