package thederpgamer.edencore.data.exchangedata;

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
public class ExchangeData extends SerializableData {
	
	public ExchangeData() {
		super(DataType.EXCHANGE_DATA, UUID.randomUUID().toString());
	}

	public ExchangeData(PacketReadBuffer readBuffer) throws IOException {
		super(readBuffer);
	}

	public ExchangeData(JSONObject data) {
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
}
