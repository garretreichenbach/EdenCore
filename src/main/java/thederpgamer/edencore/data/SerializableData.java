package thederpgamer.edencore.data;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.json.JSONObject;
import thederpgamer.edencore.data.buildsectordata.BuildSectorData;
import thederpgamer.edencore.data.exchangedata.ExchangeData;
import thederpgamer.edencore.data.playerdata.PlayerData;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public abstract class SerializableData {

	public enum DataType {
		PLAYER_DATA(PlayerData.class),
		BUILD_SECTOR_DATA(BuildSectorData.class),
		EXCHANGE_DATA(ExchangeData.class);

		private final Class<? extends SerializableData> dataClass;

		DataType(Class<? extends SerializableData> dataClass) {
			this.dataClass = dataClass;
		}

		public Class<? extends SerializableData> getDataClass() {
			return dataClass;
		}
	}

	protected String dataUUID;
	protected DataType dataType;

	protected SerializableData(DataType dataType, String dataUUID) {
		this.dataType = dataType;
		this.dataUUID = dataUUID;
	}

	protected SerializableData(PacketReadBuffer readBuffer) throws IOException {
		deserializeNetwork(readBuffer);
	}

	protected SerializableData(JSONObject data) {
		deserialize(data);
	}

	public boolean equals(Object obj) {
		return obj.getClass() == getClass() && ((SerializableData) obj).dataUUID.equals(dataUUID);
	}

	public String getUUID() {
		return dataUUID;
	}

	public DataType getDataType() {
		return dataType;
	}

	public abstract JSONObject serialize();

	public abstract void deserialize(JSONObject data);

	public abstract void serializeNetwork(PacketWriteBuffer writeBuffer) throws IOException;

	public abstract void deserializeNetwork(PacketReadBuffer readBuffer) throws IOException;
}
