package thederpgamer.edencore.network;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.manager.ClientCacheManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ClientCacheCommandPacket extends Packet {

	private ClientCacheManager.ClientActionType actionType;
	private List<String> args = new ArrayList<>();

	public ClientCacheCommandPacket() {}

	public ClientCacheCommandPacket(ClientCacheManager.ClientActionType actionType, String... args) {
		this.actionType = actionType;
		Collections.addAll(this.args, args);
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		actionType = ClientCacheManager.ClientActionType.values()[packetReadBuffer.readInt()];
		args = packetReadBuffer.readStringList();
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeInt(actionType.ordinal());
		packetWriteBuffer.writeStringList(args);
	}

	@Override
	public void processPacketOnClient() {
		ClientCacheManager.processAction(actionType, args.toArray(new String[0]));
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {

	}
}
