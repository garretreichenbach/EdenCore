package thederpgamer.edencore.data.exchangedata;

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
public class ExchangeDataManager extends DataManager<ExchangeData> {

	private final Set<ExchangeData> clientCache = new HashSet<>();
	private static ExchangeDataManager instance;
	public static ExchangeDataManager getInstance() {
		return instance;
	}

	public static void initialize(boolean client) {
		instance = new ExchangeDataManager();
		if(client) instance.requestFromServer();
	}
	
	@Override
	public Set<ExchangeData> getServerCache() {
		List<Object> objects = PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), ExchangeData.class);
		Set<ExchangeData> data = new HashSet<>();
		for(Object object : objects) data.add((ExchangeData) object);
		return data;
	}

	@Override
	public Set<ExchangeData> getClientCache() {
		return Collections.unmodifiableSet(clientCache);
	}

	@Override
	public void addToClientCache(ExchangeData data) {
		clientCache.add(data);
	}

	@Override
	public void removeFromClientCache(ExchangeData data) {
		clientCache.remove(data);
	}

	@Override
	public void updateClientCache(ExchangeData data) {
		clientCache.remove(data);
		clientCache.add(data);
	}
}
