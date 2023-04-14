package thederpgamer.edencore.network.client.misc;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.data.GameServerState;
import thederpgamer.edencore.navigation.EdenMapDrawer;
import thederpgamer.edencore.navigation.MapMarker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 25.09.2021
 * TIME: 12:35
 * sends mapmarkers to be drawn from server to client.
 */
public class NavigationMapPacket extends Packet {
	Collection<MapMarker> markers = new ArrayList<>();
	Collection<Long> publicMarkers = new ArrayList<>();

	public NavigationMapPacket(Collection<MapMarker> markers) {
		this.markers = markers;
	}

	public NavigationMapPacket() {
	} //required by starloader to instantiate upon receiving

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		int size = packetReadBuffer.readInt();
		for(int i = 0; i < size; i++) {
			try {
				Class clazz = Class.forName(packetReadBuffer.readString());
				Object o = packetReadBuffer.readObject(clazz);
				if(!(o instanceof MapMarker)) continue;
				markers.add((MapMarker) o);
			} catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeInt(markers.size());
		for(MapMarker marker : markers) {
			packetWriteBuffer.writeString(marker.getClass().getName());
			packetWriteBuffer.writeObject(marker);
		}
	}

	@Override
	public void processPacketOnClient() {
		//write Markers to display on map
		EdenMapDrawer drawer = EdenMapDrawer.instance;
		drawer.getPublicMarkers().clear();
		for(MapMarker m : markers) {
			drawer.addMarker(m, true);
		}
		EdenMapDrawer.instance.updateInternalList();
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
	}

	public void sendToAllServer() {
		for(PlayerState p : GameServerState.instance.getPlayerStatesByName().values()) {
			PacketUtil.sendPacket(p, this);
		}
	}
}
