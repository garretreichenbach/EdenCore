package thederpgamer.edencore.network.server;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.api.starbridge.StarBridgeAPI;
import thederpgamer.edencore.data.player.DonatorData;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class SendDonatorsPacket extends Packet {

	private DonatorData[] donators;

	public SendDonatorsPacket() {

	}

	public SendDonatorsPacket(DonatorData[] donators) {
		this.donators = donators;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		int length = packetReadBuffer.readInt();
		donators = new DonatorData[length];
		for(int i = 0; i < length; i ++) {
			donators[i] = new DonatorData(packetReadBuffer.readString(), packetReadBuffer.readLong(), packetReadBuffer.readString());
		}
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeInt(donators.length);
		for(DonatorData donator : donators) {
			packetWriteBuffer.writeString(donator.name);
			packetWriteBuffer.writeLong(donator.discordId);
			packetWriteBuffer.writeString(donator.tier);
		}
	}

	@Override
	public void processPacketOnClient() {
		StarBridgeAPI.updateFromPacket(donators);
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {

	}
}
