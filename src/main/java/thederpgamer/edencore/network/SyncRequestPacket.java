package thederpgamer.edencore.network;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.DataManager;
import thederpgamer.edencore.data.SerializableData;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class SyncRequestPacket extends Packet {

	private SerializableData.DataType dataType;
	
	public SyncRequestPacket() {}
	
	public SyncRequestPacket(SerializableData.DataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		dataType = SerializableData.DataType.values()[packetReadBuffer.readInt()];
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeInt(dataType.ordinal());
	}

	@Override
	public void processPacketOnClient() {
		
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		if(dataType.getDataManagerClass() != null) {
			DataManager<?> dataManager = DataManager.getDataManager(dataType.getDataManagerClass());
			assert dataManager != null;
			for(SerializableData data : dataManager.getCache(true)) dataManager.sendDataToPlayer(playerState, data, DataManager.ADD_DATA);
		}
	}
}
