package thederpgamer.edencore.network.client.buildsector;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.PlayerUtils;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.utils.DataUtils;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Sends a request to the server to tp the client from a build sector.
 * <p>[CLIENT -> SERVER]</p>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/28/2021]
 */
public class RequestMoveFromBuildSectorPacket extends Packet {
	public RequestMoveFromBuildSectorPacket() {
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
		try {
			DataUtils.movePlayerFromBuildSector(playerState);
		} catch(IOException | SQLException exception) { //Ideally, this should never fail, because doing so would be a catastrophic problem
			EdenCore.getInstance().logException("Failed to move player \"" + playerState.getName() + "\" from a build sector!", exception);
			PlayerUtils.sendMessage(playerState, "The server encountered an error while trying to teleport you. Please report this to an admin!");
		}
	}
}