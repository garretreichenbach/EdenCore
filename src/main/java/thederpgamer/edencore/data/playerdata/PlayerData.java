package thederpgamer.edencore.data.playerdata;

import api.common.GameCommon;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.json.JSONObject;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.Faction;
import thederpgamer.edencore.data.SerializableData;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class PlayerData extends SerializableData {

	private static final byte VERSION = 0;

	private String name;
	private int factionId;
	
	public PlayerData(String name, int factionId) {
		super(DataType.PLAYER_DATA);
		this.name = name;
		this.factionId = factionId;
	}
	
	public PlayerData(PlayerState playerState) {
		this(playerState.getName(), playerState.getFactionId());
	}

	public PlayerData(PacketReadBuffer readBuffer) throws IOException {
		super(readBuffer);
	}

	public PlayerData(JSONObject data) {
		super(data);
	}

	@Override
	public JSONObject serialize() {
		JSONObject data = new JSONObject();
		data.put("version", VERSION);
		data.put("uuid", getUUID());
		data.put("name", name);
		data.put("factionId", factionId);
		return data;
	}

	@Override
	public void deserialize(JSONObject data) {
		byte version = (byte) data.getInt("version");
		dataUUID = data.getString("uuid");
		name = data.getString("name");
		factionId = data.getInt("factionId");
	}

	@Override
	public void serializeNetwork(PacketWriteBuffer writeBuffer) throws IOException {
		writeBuffer.writeByte(VERSION);
		writeBuffer.writeString(dataUUID);
		writeBuffer.writeString(name);
		writeBuffer.writeInt(factionId);
	}

	@Override
	public void deserializeNetwork(PacketReadBuffer readBuffer) throws IOException {
		byte version = readBuffer.readByte();
		dataUUID = readBuffer.readString();
		name = readBuffer.readString();
		factionId = readBuffer.readInt();
	}
	
	public String getName() {
		return name;
	}
	
	public int getFactionId() {
		PlayerState playerState = getPlayerState();
		if(playerState != null && factionId != playerState.getFactionId()) {
			factionId = playerState.getFactionId();
			PlayerDataManager.getInstance().updateData(this, playerState.isOnServer());
		}
		return factionId;
	}

	public PlayerState getPlayerState() {
		return GameCommon.getPlayerFromName(name);
	}
	
	public Faction getFaction() {
		return GameCommon.getGameState().getFactionManager().getFaction(getFactionId());
	}
	
	public String getFactionName() {
		return getPlayerState().getFactionName();
	}
}
