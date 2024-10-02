package thederpgamer.edencore.manager;

import api.common.GameServer;
import api.mod.config.PersistentObjectUtil;
import api.network.packets.PacketUtil;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.ExchangeItem;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.network.ClientCacheCommandPacket;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ServerCacheManager {

	public static void addItem(int type, String itemUID, Object item) {
		switch(type) {
			case ClientCacheManager.EXCHANGE_DATA:
				if(item instanceof ExchangeItem) {
					ExchangeItem exchangeItem = (ExchangeItem) item;
					PersistentObjectUtil.addObject(EdenCore.getInstance().getSkeleton(), exchangeItem);
					PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
					sendCommand(ClientCacheManager.ClientActionType.ADD, itemUID);
				}
				break;
			case ClientCacheManager.BUILD_SECTOR_DATA:
				if(item instanceof BuildSectorData) {
					BuildSectorData buildSectorData = (BuildSectorData) item;
					PersistentObjectUtil.addObject(EdenCore.getInstance().getSkeleton(), buildSectorData);
					PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
					sendCommand(ClientCacheManager.ClientActionType.ADD, itemUID);
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid cache type: " + type);
		}
	}

	public static void removeItem(int type, String itemUID) {
		switch(type) {
			case ClientCacheManager.EXCHANGE_DATA:
				for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), ExchangeItem.class)) {
					ExchangeItem exchangeItem = (ExchangeItem) obj;
					if(exchangeItem.getUID().equals(itemUID)) {
						PersistentObjectUtil.removeObject(EdenCore.getInstance().getSkeleton(), exchangeItem);
						PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
						sendCommand(ClientCacheManager.ClientActionType.REMOVE, itemUID);
						break;
					}
				}
				break;
			case ClientCacheManager.BUILD_SECTOR_DATA:
				for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), BuildSectorData.class)) {
					BuildSectorData buildSectorData = (BuildSectorData) obj;
					if(buildSectorData.getUID().equals(itemUID)) {
						PersistentObjectUtil.removeObject(EdenCore.getInstance().getSkeleton(), buildSectorData);
						PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
						sendCommand(ClientCacheManager.ClientActionType.REMOVE, itemUID);
						break;
					}
				}
			default:
				throw new IllegalArgumentException("Invalid cache type: " + type);
		}
	}

	public static void updateItem(int type, String itemUID) {
		switch(type) {
			case ClientCacheManager.EXCHANGE_DATA:
				for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), ExchangeItem.class)) {
					ExchangeItem exchangeItem = (ExchangeItem) obj;
					if(exchangeItem.getUID().equals(itemUID)) {
						PersistentObjectUtil.removeObject(EdenCore.getInstance().getSkeleton(), exchangeItem);
						PersistentObjectUtil.addObject(EdenCore.getInstance().getSkeleton(), exchangeItem);
						PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
						sendCommand(ClientCacheManager.ClientActionType.UPDATE, itemUID);
						break;
					}
				}
				break;
			case ClientCacheManager.BUILD_SECTOR_DATA:
				for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), BuildSectorData.class)) {
					BuildSectorData buildSectorData = (BuildSectorData) obj;
					if(buildSectorData.getUID().equals(itemUID)) {
						PersistentObjectUtil.removeObject(EdenCore.getInstance().getSkeleton(), buildSectorData);
						PersistentObjectUtil.addObject(EdenCore.getInstance().getSkeleton(), buildSectorData);
						PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
						sendCommand(ClientCacheManager.ClientActionType.UPDATE, itemUID);
						break;
					}
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid cache type: " + type);
		}
	}

	private static void sendCommand(ClientCacheManager.ClientActionType actionType, String... args) {
		for(PlayerState playerState : GameServer.getServerState().getPlayerStatesByName().values()) {
			PacketUtil.sendPacket(playerState, new ClientCacheCommandPacket(actionType, args));
		}
	}
}
