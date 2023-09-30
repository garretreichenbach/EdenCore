package thederpgamer.edencore.network.client.buildsector;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.PlayerUtils;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.utils.DataUtils;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Sends a request to the server to tp the client to a build sector.
 * <p>[CLIENT -> SERVER]</p>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/28/2021]
 */
public class RequestMoveToBuildSectorPacket extends Packet {
	private BuildSectorData sectorData;

	public RequestMoveToBuildSectorPacket() {
	}

	public RequestMoveToBuildSectorPacket(BuildSectorData sectorData) {
		this.sectorData = sectorData;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		sectorData = new BuildSectorData(packetReadBuffer);
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		sectorData.serialize(packetWriteBuffer);
	}

	@Override
	public void processPacketOnClient() {
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		try {
			DataUtils.movePlayerToBuildSector(playerState, sectorData);
		} catch(IOException | SQLException exception) {
			EdenCore.getInstance().logException("Failed to move player \"" + playerState.getName() + "\" to a build sector!", exception);
			PlayerUtils.sendMessage(playerState, "The server encountered an error while trying to teleport you. Please report this to an admin!");
		}
	}
}
