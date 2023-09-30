package thederpgamer.edencore.data.other;

import api.common.GameCommon;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.SerializableData;
import thederpgamer.edencore.data.player.PlayerData;
import thederpgamer.edencore.utils.DataUtils;

import javax.vecmath.Vector4f;
import java.io.IOException;
import java.util.Objects;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class WaypointMarker implements SerializableData {
	public static final int VISIBILITY_ALL = 0;
	public static final int VISIBILITY_ALLY = 1;
	public static final int VISIBILITY_FACTION = 2;
	public static final int VISIBILITY_SELF = 3;

	public enum WaypointType {
		STATION("Station"), SHIPYARD("Shipyard"), SHOP("Shop"), WARP_GATE("Warp Gate"), GAS_PLANET("Gas Giant"), PLANET("Planet"), OUTPOST("Outpost"), STAR("Star");
		public final String name;

		WaypointType(String name) {
			this.name = name();
		}
	}

	private Vector3i sector;
	private String name;
	private WaypointType type;
	private String owner;
	private int visibilitySetting;

	public WaypointMarker(Vector3i sector, String name, WaypointType type, String owner) {
		this.sector = sector;
		this.name = name;
		this.type = type;
		this.owner = owner;
		visibilitySetting = VISIBILITY_ALLY;
	}

	public WaypointMarker(PacketReadBuffer packetReadBuffer) throws IOException {
		deserialize(packetReadBuffer);
	}

	@Override
	public void deserialize(PacketReadBuffer readBuffer) throws IOException {
		sector = readBuffer.readVector();
		name = readBuffer.readString();
		type = WaypointType.valueOf(readBuffer.readString());
		owner = readBuffer.readString();
		visibilitySetting = readBuffer.readInt();
	}

	@Override
	public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
		writeBuffer.writeVector(sector);
		writeBuffer.writeString(name);
		writeBuffer.writeString(type.name);
		writeBuffer.writeString(owner);
		writeBuffer.writeInt(visibilitySetting);
	}

	@Override
	public void updateClients() {
	}

	public Vector3i getSector() {
		return sector;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WaypointType getType() {
		return type;
	}

	public void setType(WaypointType type) {
		this.type = type;
	}

	public String getOwner() {
		return owner;
	}

	public int getVisibility() {
		return visibilitySetting;
	}

	public void setVisibility(int visibilitySetting) {
		this.visibilitySetting = visibilitySetting;
	}

	public boolean canView(PlayerState playerState) {
		PlayerData thisData = DataUtils.getPlayerData(playerState);
		PlayerData ownerData = DataUtils.getPlayerDataByName(owner);
		if(ownerData != null) {
			switch(visibilitySetting) {
				case VISIBILITY_ALL:
					return true;
				case VISIBILITY_ALLY:
					return Objects.requireNonNull(GameCommon.getGameState()).getFactionManager().isFriend(ownerData.factionId, thisData.factionId);
				case VISIBILITY_FACTION:
					return thisData.factionId != 0 && thisData.factionId == ownerData.factionId;
				case VISIBILITY_SELF:
					return Objects.equals(playerState.getName(), owner);
			}
		}
		return Objects.equals(playerState.getName(), owner);
	}

	public Vector4f getColor() {
		return new Vector4f();
	}
}
