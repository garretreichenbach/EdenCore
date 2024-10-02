package thederpgamer.edencore.network.old.client.exchange;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.PlayerUtils;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.FactionManager;
import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.controller.EntityNotFountException;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.gui.buildsectormenu.BuildSectorMenuPanel;
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
	private Vector3i sector;

	public RequestSpawnEntryPacket() {
	}

	public RequestSpawnEntryPacket(String entryName, boolean docked, int factionId, Vector3i sector) {
		this(entryName, entryName, docked, factionId, sector);
	}

	public RequestSpawnEntryPacket(String spawnName, String entryName, boolean docked, int factionId, Vector3i sector) {
		this.spawnName = spawnName;
		this.entryName = entryName;
		this.docked = docked;
		this.factionId = factionId;
		this.sector = sector;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		spawnName = packetReadBuffer.readString();
		entryName = packetReadBuffer.readString();
		docked = packetReadBuffer.readBoolean();
		factionId = packetReadBuffer.readInt();
		sector = packetReadBuffer.readVector();
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeString(spawnName);
		packetWriteBuffer.writeString(entryName);
		packetWriteBuffer.writeBoolean(docked);
		packetWriteBuffer.writeInt(factionId);
		packetWriteBuffer.writeVector(sector);
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
				if(factionId == FactionManager.PIRATES_ID) EntityUtils.spawnEnemy(playerState, entry, spawnName, sector);
				else EntityUtils.spawnEntry(playerState, entry, spawnName, factionId, sector);
			}
		} catch(EntityNotFountException exception) {
			exception.printStackTrace();
			PlayerUtils.sendMessage(playerState, "There was a severe error in spawning your entity! Please notify an admin ASAP!");
		}
		EdenCore.getInstance().updateClientCacheData();
		((BuildSectorMenuPanel) EdenCore.getInstance().buildSectorMenuControlManager.getMenuPanel()).refresh(false);
	}
}
