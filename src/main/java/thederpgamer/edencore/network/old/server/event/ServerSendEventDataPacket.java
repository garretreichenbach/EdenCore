package thederpgamer.edencore.network.old.server.event;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.manager.ClientCacheManager;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class ServerSendEventDataPacket extends Packet {
	private EventData eventData;

	public ServerSendEventDataPacket() {
	}

	public ServerSendEventDataPacket(EventData eventData) {
		this.eventData = eventData;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		eventData = EventData.fromPacket(packetReadBuffer);
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		eventData.serialize(packetWriteBuffer);
	}

	@Override
	public void processPacketOnClient() {
		ClientCacheManager.updateCache(ClientCacheManager.EVENT_DATA, eventData);
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
	}
}
