package thederpgamer.edencore.data.buildsectordata;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.json.JSONObject;
import thederpgamer.edencore.data.SerializableData;

import java.io.IOException;
import java.util.UUID;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class BuildSectorData extends SerializableData {
	
	public BuildSectorData() {
		super(DataType.BUILD_SECTOR_DATA, UUID.randomUUID().toString());
	}

	public BuildSectorData(PacketReadBuffer readBuffer) throws IOException {
		super(readBuffer);
	}

	public BuildSectorData(JSONObject data) {
		super(data);
	}

	@Override
	public JSONObject serialize() {
		return null;
	}

	@Override
	public void deserialize(JSONObject data) {

	}

	@Override
	public void serializeNetwork(PacketWriteBuffer writeBuffer) throws IOException {

	}

	@Override
	public void deserializeNetwork(PacketReadBuffer readBuffer) throws IOException {

	}

	public  isPlayerInBuildSector( ) {
	}

	public  isPlayerInBuildSector( ) {
	}
}
