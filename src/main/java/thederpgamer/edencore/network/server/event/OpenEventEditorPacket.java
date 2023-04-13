package thederpgamer.edencore.network.server.event;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.gui.eventeditor.EventEditorFrame;
import thederpgamer.edencore.manager.ClientCacheManager;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class OpenEventEditorPacket extends Packet {

	private EventData eventData;

	public OpenEventEditorPacket() {

	}

	public OpenEventEditorPacket(EventData eventData) {
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
		if(ClientCacheManager.editingEvent == null) {
			ClientCacheManager.editingEvent = eventData;
			new EventEditorFrame(eventData);
		}
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
	}
}
