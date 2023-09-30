package thederpgamer.edencore.network.server;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.gui.buildsectormenu.BuildSectorMenuPanel;

import java.io.IOException;

/**
 * Tells the client to refresh the build sector GUI.
 * [SERVER] -> [CLIENT]
 *
 * @author TheDerpGamer
 */
public class RefreshBuildSectorGUIPacket extends Packet {

	public RefreshBuildSectorGUIPacket() {

	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {

	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {

	}

	@Override
	public void processPacketOnClient() {
		((BuildSectorMenuPanel) EdenCore.getInstance().buildSectorMenuControlManager.getMenuPanel()).refresh(false);
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {

	}
}
