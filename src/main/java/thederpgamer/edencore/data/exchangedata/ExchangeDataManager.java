package thederpgamer.edencore.data.exchangedata;

import api.mod.config.PersistentObjectUtil;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.DataManager;
import thederpgamer.edencore.data.SerializableData;

import java.util.*;

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
	public SerializableData.DataType getDataType() {
		return SerializableData.DataType.EXCHANGE_DATA;
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

	@Override
	public void createMissingData(Object... args) {
		//Exchange data doesn't need to be created on client login and isn't critical to the basic mod functions, so we can leave this empty
	}

	public static Set<ExchangeData> getCategory(ExchangeData.ExchangeDataCategory category) {
		Set<ExchangeData> data = new HashSet<>();
		for(ExchangeData exchangeData : instance.clientCache) {
			if(exchangeData.getCategory() == category) data.add(exchangeData);
		}
		return data;
	}

	public boolean existsName(String uid) {
		for(ExchangeData data : clientCache) {
			if(data.getName().toLowerCase(Locale.ENGLISH).trim().equals(uid.toLowerCase(Locale.ENGLISH).trim())) return true;
		}
		return false;
	}
}
