package thederpgamer.edencore.network.client.buildsector;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.PlayerUtils;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.manager.LogManager;
import thederpgamer.edencore.utils.DataUtils;
import thederpgamer.edencore.utils.ServerUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * Requests that a player be kicked from a build sector.
 * [CLIENT] -> [SERVER]
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/29/2021]
 */
public class RequestBuildSectorKickPacket extends Packet {
	private String ownerName;
	private String playerName;
	private String reason;

	public RequestBuildSectorKickPacket() {
	}

	public RequestBuildSectorKickPacket(String ownerName, String playerName, String reason) {
		this.ownerName = ownerName;
		this.playerName = playerName;
		this.reason = reason;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		ownerName = packetReadBuffer.readString();
		playerName = packetReadBuffer.readString();
		reason = packetReadBuffer.readString();
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeString(ownerName);
		packetWriteBuffer.writeString(playerName);
		packetWriteBuffer.writeString(reason);
	}

	@Override
	public void processPacketOnClient() {
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		PlayerState targetPlayer = ServerUtils.getPlayerByName(playerName);
		try {
			if(DataUtils.isPlayerInAnyBuildSector(targetPlayer) && Objects.requireNonNull(DataUtils.getPlayerCurrentBuildSector(targetPlayer)).ownerName.equals(ownerName)) {
				if(targetPlayer.isOnServer()) {
					DataUtils.movePlayerFromBuildSector(targetPlayer);
					if(reason.isEmpty()) PlayerUtils.sendMessage(targetPlayer, "You were kicked from " + ownerName + "'s build sector.");
					else PlayerUtils.sendMessage(targetPlayer, "You were kicked from " + ownerName + "'s build sector for \"" + reason + "\".");
				}
			}
		} catch(Exception exception) {
			LogManager.logException("Encountered an exception while trying to kick player \"" + playerState + "\" from a build sector.", exception);
		}
	}
}
