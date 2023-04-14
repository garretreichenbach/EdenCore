package thederpgamer.edencore.network.client.buildsector;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.utils.DataUtils;

import java.io.IOException;

/**
 * Sends an updated version of Build Sector permissions to server.
 * [CLIENT] -> [SERVER]
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/30/2021]
 */
public class UpdateBuildSectorPermissionsPacket extends Packet {
	private BuildSectorData sectorData;
	private String targetName;

	public UpdateBuildSectorPermissionsPacket() {
	}

	public UpdateBuildSectorPermissionsPacket(BuildSectorData sectorData, String targetName) {
		this.sectorData = sectorData;
		this.targetName = targetName;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		sectorData = new BuildSectorData(packetReadBuffer);
		targetName = packetReadBuffer.readString();
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		sectorData.serialize(packetWriteBuffer);
		packetWriteBuffer.writeString(targetName);
	}

	@Override
	public void processPacketOnClient() {
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		if(playerState.getName().equals(sectorData.ownerName)) DataUtils.updateBuildSector(sectorData);
	}
}
