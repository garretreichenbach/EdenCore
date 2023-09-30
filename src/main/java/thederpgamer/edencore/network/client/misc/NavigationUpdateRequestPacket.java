package thederpgamer.edencore.network.client.misc;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.manager.ServerDataManager;
import thederpgamer.edencore.network.server.NavigationUpdatePacket;

import java.io.IOException;

/**
 * Request packet for updating the navigation system.
 * [CLIENT] -> [SERVER]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class NavigationUpdateRequestPacket extends Packet {

	public NavigationUpdateRequestPacket() {

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
		PacketUtil.sendPacket(playerState, new NavigationUpdatePacket(ServerDataManager.getWaypointMarkers(playerState)));
	}
}
