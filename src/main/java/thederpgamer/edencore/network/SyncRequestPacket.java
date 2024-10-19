package thederpgamer.edencore.network;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
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

	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {

	}

	@Override
	public void processPacketOnClient() {

	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
	}
}
