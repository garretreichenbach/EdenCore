package thederpgamer.edencore.network.old.client.buildsector;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.utils.BuildSectorUtils;
import thederpgamer.edencore.utils.DataUtils;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class RequestBuildSectorProtectPacket extends Packet {
	private boolean protect;

	public RequestBuildSectorProtectPacket() {
	}

	public RequestBuildSectorProtectPacket(boolean protect) {
		this.protect = protect;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		protect = packetReadBuffer.readBoolean();
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeBoolean(protect);
	}

	@Override
	public void processPacketOnClient() {
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		//Todo: Pass along some sort of unique identifier for the build sector too, so if a player has admin access to multiple build sectors, the server knows which one to protect.
		BuildSectorData sectorData = DataUtils.getBuildSector(playerState.getName());
		if(sectorData != null) BuildSectorUtils.setPeace(sectorData, protect);
	}
}
