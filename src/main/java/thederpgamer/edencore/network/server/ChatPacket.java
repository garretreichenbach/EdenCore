package thederpgamer.edencore.network.server;

import api.common.GameClient;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.textures.StarLoaderTexture;
import org.schema.game.common.data.player.PlayerState;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class ChatPacket extends Packet {

	private String name;

	public ChatPacket() {

	}

	public ChatPacket(String name) {
		this.name = name;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		name = packetReadBuffer.readString();
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeString(name);
	}

	@Override
	public void processPacketOnClient() {
		StarLoaderTexture.runOnGraphicsThread(new Runnable() {
			@Override
			public void run() {
				GameClient.getClientState().getWorldDrawer().getGuiDrawer().getChatNew().recreateTabs();
			}
		});
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {

	}
}
