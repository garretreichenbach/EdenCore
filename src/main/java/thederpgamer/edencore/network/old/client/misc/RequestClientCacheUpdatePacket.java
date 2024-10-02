package thederpgamer.edencore.network.old.client.misc;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.network.old.server.SendCacheUpdatePacket;

import java.io.IOException;

/**
 * Sends a request to the server to updateClients the client cache data.
 * <p>[CLIENT -> SERVER]</p>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/28/2021]
 */
public class RequestClientCacheUpdatePacket extends Packet {
	public RequestClientCacheUpdatePacket() {
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		packetReadBuffer.readInt();
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeInt(1);
	}

	@Override
	public void processPacketOnClient() {
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		PacketUtil.sendPacket(playerState, new SendCacheUpdatePacket(playerState));
	}
}
