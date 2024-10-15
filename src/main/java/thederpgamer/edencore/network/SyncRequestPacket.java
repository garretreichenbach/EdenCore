package thederpgamer.edencore.network;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class SyncRequestPacket extends Packet {

	public SyncRequestPacket() {}

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
