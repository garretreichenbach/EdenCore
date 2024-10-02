package thederpgamer.edencore.data;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;

import java.io.IOException;
import java.util.UUID;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ExchangeItem implements SerializableData {

	public enum TurretType {
		ANTI_MISSILE("Anti-Missile"),

		;

		public final String displayName;

		TurretType(String displayName) {
			this.displayName = displayName;
		}
	}

	private String uid;
	private String name;
	private String producer;
	private int price;
	private String category;
	private float mass;

	public ExchangeItem(String name, String producer, int price, String category, float mass) {
		uid = UUID.randomUUID().toString();
		this.name = name;
		this.producer = producer;
		this.price = price;
		this.category = category;
		this.mass = mass;
	}

	public ExchangeItem(PacketReadBuffer readBuffer) throws IOException {
		deserialize(readBuffer);
	}

	@Override
	public void deserialize(PacketReadBuffer readBuffer) throws IOException {
		uid = readBuffer.readString();
		name = readBuffer.readString();
		producer = readBuffer.readString();
		price = readBuffer.readInt();
		category = readBuffer.readString();
		mass = readBuffer.readFloat();
	}

	@Override
	public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
		writeBuffer.writeString(uid);
		writeBuffer.writeString(name);
		writeBuffer.writeString(producer);
		writeBuffer.writeInt(price);
		writeBuffer.writeString(category);
		writeBuffer.writeFloat(mass);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ExchangeItem) {
			ExchangeItem item = (ExchangeItem) obj;
			return item.uid.equals(uid);
		}
		return false;
	}

	public String getUID() {
		return uid;
	}

	public String getName() {
		return name;
	}

	public String getProducer() {
		return producer;
	}

	public int getPrice() {
		return price;
	}

	public String getCategory() {
		return category;
	}

	public float getMass() {
		return mass;
	}
}
