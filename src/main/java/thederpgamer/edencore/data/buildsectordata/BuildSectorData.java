package thederpgamer.edencore.data.buildsectordata;

import api.common.GameCommon;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.input.Keyboard;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.network.objects.Sendable;
import thederpgamer.edencore.data.SerializableData;

import javax.swing.text.Segment;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class BuildSectorData extends SerializableData {
	
	private Vector3i sector;
	private Set<Integer> entities = new HashSet<>();
	
	public BuildSectorData() {
		super(DataType.BUILD_SECTOR_DATA, UUID.randomUUID().toString());
		sector = BuildSectorDataManager.calculateRandomSector();
	}

	public BuildSectorData(PacketReadBuffer readBuffer) throws IOException {
		super(readBuffer);
	}

	public BuildSectorData(JSONObject data) {
		super(data);
	}

	@Override
	public JSONObject serialize() {
		JSONObject data = new JSONObject();
		data.put("uuid", getUUID());
		data.put("sector", sector.toString());
		data.put("entities", entities);
		return data;
	}

	@Override
	public void deserialize(JSONObject data) {
		dataUUID = data.getString("uuid");
		sector = new Vector3i(Vector3i.parseVector3i(data.getString("sector")));
		entities.clear();
		JSONArray entityArray = data.getJSONArray("entities");
		for(int i = 0; i < entityArray.length(); i ++) entities.add(entityArray.getInt(i));
	}

	@Override
	public void serializeNetwork(PacketWriteBuffer writeBuffer) throws IOException {
		writeBuffer.writeString(dataUUID);
		writeBuffer.writeString(sector.toStringPure());
		writeBuffer.writeInt(entities.size());
		for(int entityId : entities) writeBuffer.writeInt(entityId);
	}

	@Override
	public void deserializeNetwork(PacketReadBuffer readBuffer) throws IOException {
		dataUUID = readBuffer.readString();
		sector = new Vector3i(Vector3i.parseVector3i(readBuffer.readString()));
		entities.clear();
		int entityCount = readBuffer.readInt();
		for(int i = 0; i < entityCount; i ++) entities.add(readBuffer.readInt());
	}
	
	public Vector3i getSector() {
		return sector;
	}
	
	public Set<SegmentController> getEntities() {
		Set<SegmentController> entitySet = new HashSet<>();
		for(int entityId : entities) {
			Sendable entity = GameCommon.getGameObject(entityId);
			if(entity instanceof SegmentController) entitySet.add((SegmentController) entity);
		}
		return entitySet;
	}
}
