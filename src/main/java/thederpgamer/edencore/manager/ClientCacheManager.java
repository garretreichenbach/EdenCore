package thederpgamer.edencore.manager;

import thederpgamer.edencore.data.exchangedata.ExchangeData;
import thederpgamer.edencore.data.other.BuildSectorData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Stores client cache data for periodical updates from server.
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/18/2021]
 */
public class ClientCacheManager {

	public enum ClientActionType {
		ADD,
		REMOVE,
		UPDATE
	}

	public static final int EXCHANGE_DATA = 0;
	public static final int BUILD_SECTOR_DATA = 1;

	private static final HashMap<String, ExchangeData> exchangeItems = new HashMap<>();
	private static final HashMap<String, BuildSectorData> buildSectors = new HashMap<>();

	public static void processAction(ClientActionType actionType, String... args) {
		switch(actionType) {
			case ADD:
				addItem(Integer.parseInt(args[0]), args[1]);
				break;
			case REMOVE:
				removeItem(Integer.parseInt(args[0]), args[1]);
				break;
			case UPDATE:
				updateItem(Integer.parseInt(args[0]), args[1]);
				break;
			default:
				throw new IllegalArgumentException("Invalid action type: " + actionType);
		}
	}

	private static void addItem(int type, Object data) {
		switch(type) {
			case EXCHANGE_DATA:
				exchangeItems.put(((ExchangeData) data).getUID(), (ExchangeData) data);
				break;
			case BUILD_SECTOR_DATA:
				buildSectors.put(((BuildSectorData) data).getUID(), (BuildSectorData) data);
				break;
			default:
				throw new IllegalArgumentException("Invalid cache type: " + type);
		}
	}

	private static void removeItem(int type, String itemUID) {
		switch(type) {
			case EXCHANGE_DATA:
				exchangeItems.remove(itemUID);
				break;
			case BUILD_SECTOR_DATA:
				buildSectors.remove(itemUID);
				break;
			default:
				throw new IllegalArgumentException("Invalid cache type: " + type);
		}
	}

	private static void updateItem(int type, Object data) {
		switch(type) {
			case EXCHANGE_DATA:
				exchangeItems.put(((ExchangeData) data).getUID(), (ExchangeData) data);
				break;
			case BUILD_SECTOR_DATA:
				buildSectors.put(((BuildSectorData) data).getUID(), (BuildSectorData) data);
				break;
			default:
				throw new IllegalArgumentException("Invalid cache type: " + type);
		}
	}

	public static Set<ExchangeData> getExchangeItems() {
		return new HashSet<>(exchangeItems.values());
	}

	public static Set<BuildSectorData> getBuildSectors() {
		return new HashSet<>(buildSectors.values());
	}
}
