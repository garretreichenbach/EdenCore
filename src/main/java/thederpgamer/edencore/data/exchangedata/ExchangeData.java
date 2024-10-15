package thederpgamer.edencore.data.exchangedata;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.json.JSONObject;
import org.schema.game.server.data.blueprintnw.BlueprintClassification;
import thederpgamer.edencore.data.SerializableData;

import java.io.IOException;
import java.util.UUID;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ExchangeData extends SerializableData {
	
	private String name;
	private String producer;
	private int price;
	private BlueprintClassification category;
	private float mass;
	
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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getProducer() {
		return producer;
	}
	
	public void setProducer(String producer) {
		this.producer = producer;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public BlueprintClassification getCategory() {
		return category;
	}
	
	public void setCategory(BlueprintClassification category) {
		this.category = category;
	}
	
	public float getMass() {
		return mass;
	}
}
