package thederpgamer.edencore.data;

import api.common.GameServer;
import api.mod.config.PersistentObjectUtil;
import api.network.packets.PacketUtil;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.buildsectordata.BuildSectorDataManager;
import thederpgamer.edencore.data.exchangedata.ExchangeDataManager;
import thederpgamer.edencore.data.playerdata.PlayerDataManager;
import thederpgamer.edencore.network.SendDataPacket;
import thederpgamer.edencore.network.SyncRequestPacket;

import java.util.Set;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public abstract class DataManager<E extends SerializableData> {

	public static final int ADD_DATA = 0;
	public static final int REMOVE_DATA = 1;
	public static final int UPDATE_DATA = 2;
	public static final int BUY = 3;

	public static void initialize(boolean client) {
		PlayerDataManager.initialize(client);
		BuildSectorDataManager.initialize(client);
		ExchangeDataManager.initialize(client);
	}

	public void sendDataToAllPlayers(SerializableData data, int type) {
		for(PlayerState player : GameServer.getServerState().getPlayerStatesByName().values()) sendDataToPlayer(player, data, type);
	}

	public void sendDataToPlayer(PlayerState player, SerializableData data, int type) {
		PacketUtil.sendPacket(player, new SendDataPacket(data, type));
	}

	public void sendAllDataToPlayer(PlayerState player) {
		Set<E> cache = getCache(true);
		for(E data : cache) sendDataToPlayer(player, data, ADD_DATA);
	}

	public void requestFromServer() {
		PacketUtil.sendPacketToServer(new SyncRequestPacket());
	}

	public void sendPacket(SerializableData data, int type, boolean toServer) {
		if(toServer) PacketUtil.sendPacketToServer(new SendDataPacket(data, type));
		else sendDataToAllPlayers(data, type);
	}

	public Set<E> getCache(boolean isServer) {
		return isServer ? getServerCache() : getClientCache();
	}

	public void addData(E data, boolean server) {
		if(server) addToServerCache(data);
		else addToClientCache(data);
	}

	public void removeData(E data, boolean server) {
		if(server) removeFromServerCache(data);
		else removeFromClientCache(data);
	}

	public void updateData(E data, boolean server) {
		if(server) updateServerCache(data);
		else updateClientCache(data);
	}

	public void handlePacket(SerializableData data, int type, boolean server) {
		switch(type) {
			case ADD_DATA:
				addData((E) data, server);
				break;
			case REMOVE_DATA:
				removeData((E) data, server);
				break;
			case UPDATE_DATA:
				updateData((E) data, server);
				break;
		}
	}

	public E getFromUUID(String uuid, boolean server) {
		Set<E> cache = getCache(server);
		for(E data : cache) if(data.getUUID().equals(uuid)) return data;
		return null;
	}
	
	public abstract Set<E> getServerCache();

	public void addToServerCache(E data) {
		PersistentObjectUtil.addObject(EdenCore.getInstance().getSkeleton(), data);
		PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
		sendDataToAllPlayers(data, ADD_DATA);
	}

	public void removeFromServerCache(E data) {
		PersistentObjectUtil.removeObject(EdenCore.getInstance().getSkeleton(), data);
		PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
		sendDataToAllPlayers(data, REMOVE_DATA);
	}

	public void updateServerCache(E data) {
		PersistentObjectUtil.removeObject(EdenCore.getInstance().getSkeleton(), data);
		PersistentObjectUtil.addObject(EdenCore.getInstance().getSkeleton(), data);
		PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
		sendDataToAllPlayers(data, UPDATE_DATA);
	}

	public abstract Set<E> getClientCache();

	public abstract void addToClientCache(E data);

	public abstract void removeFromClientCache(E data);

	public abstract void updateClientCache(E data);
}
