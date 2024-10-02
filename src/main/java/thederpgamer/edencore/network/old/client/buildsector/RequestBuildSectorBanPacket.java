package thederpgamer.edencore.network.old.client.buildsector;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.PlayerUtils;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.utils.DataUtils;
import thederpgamer.edencore.utils.ServerUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * Requests that a player be banned from a build sector.
 * [CLIENT] -> [SERVER]
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/30/2021]
 */
public class RequestBuildSectorBanPacket extends Packet {
	private String ownerName;
	private String playerName;
	private String reason;

	public RequestBuildSectorBanPacket() {
	}

	public RequestBuildSectorBanPacket(String ownerName, String playerName, String reason) {
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
					BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(targetPlayer);
					DataUtils.movePlayerFromBuildSector(targetPlayer);
					assert sectorData != null;
					sectorData.removePlayer(targetPlayer.getName());
					if(reason.isEmpty()) PlayerUtils.sendMessage(targetPlayer, "You were banned from " + ownerName + "'s build sector.");
					else PlayerUtils.sendMessage(targetPlayer, "You were banned from " + ownerName + "'s build sector for \"" + reason + "\".");
				}
			}
		} catch(Exception exception) {
			EdenCore.getInstance().logException("Encountered an exception while trying to ban player \"" + playerState + "\" from a build sector.", exception);
		}
	}
}
