package thederpgamer.edencore.network;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.SerializableData;
import thederpgamer.edencore.data.buildsectordata.BuildSectorDataManager;
import thederpgamer.edencore.data.exchangedata.ExchangeDataManager;
import thederpgamer.edencore.data.playerdata.PlayerDataManager;

import java.io.IOException;

public class SendDataToServerPacket extends Packet {

	private SerializableData.DataType dataType;
	private SerializableData data;
	private int type;

	public SendDataToServerPacket() {
	}

	public SendDataToServerPacket(SerializableData data, int type) {
		this.data = data;
		this.type = type;
		dataType = data.getDataType();
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) {
		try {
			type = packetReadBuffer.readInt();
			dataType = SerializableData.DataType.valueOf(packetReadBuffer.readString());
			data = dataType.getDataClass().getConstructor(PacketReadBuffer.class).newInstance(packetReadBuffer);
		} catch(Exception exception) {
			EdenCore.getInstance().logException("An error occurred while reading data packet", exception);
		}
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeInt(type);
		packetWriteBuffer.writeString(dataType.name());
		data.serializeNetwork(packetWriteBuffer);
	}

	@Override
	public void processPacketOnClient() {

	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		switch(dataType) {
			case PLAYER_DATA:
				PlayerDataManager.getInstance(playerState.isOnServer()).handlePacket(data, type, playerState.isOnServer());
				break;
			case EXCHANGE_DATA:
				ExchangeDataManager.getInstance(playerState.isOnServer()).handlePacket(data, type, playerState.isOnServer());
				break;
			case BUILD_SECTOR_DATA:
				BuildSectorDataManager.getInstance(playerState.isOnServer()).handlePacket(data, type, playerState.isOnServer());
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + dataType);
		}
	}
}
