package thederpgamer.edencore.network.client.exchange;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.PlayerUtils;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.FactionManager;
import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.controller.EntityNotFountException;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.utils.EntityUtils;

import java.io.IOException;

/**
 * Requests an entity to be spawned from an exchange purchase.
 * <p>[CLIENT] -> [SERVER]</p>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/20/2021]
 */
public class RequestSpawnEntryPacket extends Packet {
	private String spawnName;
	private String entryName;
	private boolean docked;
	private int factionId;

	public RequestSpawnEntryPacket() {
	}

	public RequestSpawnEntryPacket(String entryName, boolean docked, int factionId) {
		this(entryName, entryName, docked, factionId);
	}

	public RequestSpawnEntryPacket(String spawnName, String entryName, boolean docked, int factionId) {
		this.spawnName = spawnName;
		this.entryName = entryName;
		this.docked = docked;
		this.factionId = factionId;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		spawnName = packetReadBuffer.readString();
		entryName = packetReadBuffer.readString();
		docked = packetReadBuffer.readBoolean();
		factionId = packetReadBuffer.readInt();
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeString(spawnName);
		packetWriteBuffer.writeString(entryName);
		packetWriteBuffer.writeBoolean(docked);
		packetWriteBuffer.writeInt(factionId);
	}

	@Override
	public void processPacketOnClient() {
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		try {
			BlueprintEntry entry = BluePrintController.active.getBlueprint(entryName);
			if(docked) EntityUtils.spawnEntryOnDock(playerState, entry, spawnName, factionId);
			else {
				if(factionId == FactionManager.PIRATES_ID) EntityUtils.spawnEnemy(playerState, entry, spawnName);
				else EntityUtils.spawnEntry(playerState, entry, spawnName, factionId);
			}
		} catch(EntityNotFountException exception) {
			exception.printStackTrace();
			PlayerUtils.sendMessage(playerState, "There was a severe error in spawning your entity! Please notify an admin ASAP!");
		}
		EdenCore.getInstance().updateClientCacheData();
	}
}
