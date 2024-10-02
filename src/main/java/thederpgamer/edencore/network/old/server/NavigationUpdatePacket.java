package thederpgamer.edencore.network.old.server;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.other.WaypointMarker;
import thederpgamer.edencore.drawer.EdenMapDrawer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Packet for updating the navigation system.
 * [SERVER] -> [CLIENT]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class NavigationUpdatePacket extends Packet {
	private final List<WaypointMarker> waypointMarkers = new ArrayList<>();

	public NavigationUpdatePacket() {
	}

	public NavigationUpdatePacket(List<WaypointMarker> waypointMarkers) {
		this.waypointMarkers.addAll(waypointMarkers);
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		int count = packetReadBuffer.readInt();
		for(int i = 0; i < count; i++) waypointMarkers.add(new WaypointMarker(packetReadBuffer));
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeInt(waypointMarkers.size());
		for(WaypointMarker waypointMarker : waypointMarkers) waypointMarker.serialize(packetWriteBuffer);
	}

	@Override
	public void processPacketOnClient() {
		EdenMapDrawer.getInstance().addMarkers(waypointMarkers);
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
	}
}
