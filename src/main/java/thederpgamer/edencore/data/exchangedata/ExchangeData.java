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
	
	public enum ExchangeDataCategory {
		SHIP,
		STATION
	}
	
	private String name;
	private String description;
	private String producer;
	private int price;
	private ExchangeDataCategory category;
	private BlueprintClassification classification;
	private float mass;
	
	public ExchangeData(String name, String description, String producer, int price, ExchangeDataCategory category, BlueprintClassification classification, float mass) {
		super(DataType.EXCHANGE_DATA, UUID.randomUUID().toString());
		this.name = name;
		this.description = description;
		this.producer = producer;
		this.price = price;
		this.category = category;
		this.classification = classification;
		this.mass = mass;
	}

	public ExchangeData(PacketReadBuffer readBuffer) throws IOException {
		super(readBuffer);
	}

	public ExchangeData(JSONObject data) {
		super(data);
	}

	@Override
	public JSONObject serialize() {
		JSONObject data = new JSONObject();
		data.put("uuid", getUUID());
		data.put("name", name);
		data.put("description", description);
		data.put("producer", producer);
		data.put("price", price);
		data.put("category", category.name());
		data.put("classification", classification.name());
		data.put("mass", mass);
		return data;
	}

	@Override
	public void deserialize(JSONObject data) {
		dataUUID = data.getString("uuid");
		name = data.getString("name");
		description = data.getString("description");
		producer = data.getString("producer");
		price = data.getInt("price");
		category = ExchangeDataCategory.valueOf(data.getString("category"));
		classification = BlueprintClassification.valueOf(data.getString("classification"));
		mass = (float) data.getDouble("mass");
	}

	@Override
	public void serializeNetwork(PacketWriteBuffer writeBuffer) throws IOException {
		writeBuffer.writeString(dataUUID);
		writeBuffer.writeString(name);
		writeBuffer.writeString(description);
		writeBuffer.writeString(producer);
		writeBuffer.writeInt(price);
		writeBuffer.writeString(category.name());
		writeBuffer.writeString(classification.name());
		writeBuffer.writeFloat(mass);
	}

	@Override
	public void deserializeNetwork(PacketReadBuffer readBuffer) throws IOException {
		dataUUID = readBuffer.readString();
		name = readBuffer.readString();
		description = readBuffer.readString();
		producer = readBuffer.readString();
		price = readBuffer.readInt();
		category = ExchangeDataCategory.valueOf(readBuffer.readString());
		classification = BlueprintClassification.valueOf(readBuffer.readString());
		mass = readBuffer.readFloat();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
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
	
	public ExchangeDataCategory getCategory() {
		return category;
	}
	
	public BlueprintClassification getClassification() {
		return classification;
	}
	
	public void setClassification(BlueprintClassification classification) {
		this.classification = classification;
	}
	
	public float getMass() {
		return mass;
	}
	
	public void setMass(float mass) {
		this.mass = mass;
	}
}
