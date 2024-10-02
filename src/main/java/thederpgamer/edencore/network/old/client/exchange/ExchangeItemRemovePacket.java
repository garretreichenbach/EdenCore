package thederpgamer.edencore.network.old.client.exchange;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.manager.ClientCacheManager;
import thederpgamer.edencore.manager.ServerCacheManager;

import java.io.IOException;

/**
 * Requests the removal of an exchange item from the server.
 * <p>[CLIENT -> SERVER]</p>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/18/2021]
 */
public class ExchangeItemRemovePacket extends Packet {

	private String itemUID;

	public ExchangeItemRemovePacket() {}

	public ExchangeItemRemovePacket(String itemUID) {
		this.itemUID = itemUID;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		itemUID = packetReadBuffer.readString();
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeString(itemUID);
	}

	@Override
	public void processPacketOnClient() {
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		assert playerState.isAdmin();
		ServerCacheManager.removeItem(ClientCacheManager.EXCHANGE_DATA, itemUID);
	}
}
