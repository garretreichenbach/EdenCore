package thederpgamer.edencore.data.exchange;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.client.view.gui.inventory.InventorySlotOverlayElement;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIOverlay;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/24/2021]
 */
public class ItemExchangeItem extends ExchangeItem {

	public short itemId;
	public short metaId;
	public short subType;
	private transient GUIElement parent;

	public ItemExchangeItem(PacketReadBuffer readBuffer) {
		super(readBuffer);
	}

	public ItemExchangeItem(short barType, int price, String name, String description, short itemId, short metaId, short subType) {
		super(barType, price, name, description);
		this.itemId = itemId;
		this.metaId = metaId;
		this.subType = subType;
	}

	public void setTempOverlay(GUIElement element) {
		parent = element;
	}

	@Override
	public GUIOverlay getIcon() {
		InventorySlotOverlayElement blockSprite = new InventorySlotOverlayElement(false, parent.getState(), false, parent);
		blockSprite.setMeta(metaId);
		blockSprite.setSubSlotType(subType);
		blockSprite.setSlot(0);
		blockSprite.setLayer(-1);
		return blockSprite;
	}

	@Override
	public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
		writeBuffer.writeShort(barType);
		writeBuffer.writeInt(price);
		writeBuffer.writeString(name);
		if(description.length() > 96) this.description = description.substring(0, 95) + " ...";
		writeBuffer.writeString(description);
		writeBuffer.writeShort(itemId);
		writeBuffer.writeShort(metaId);
		writeBuffer.writeShort(subType);
	}

	@Override
	public void deserialize(PacketReadBuffer readBuffer) throws IOException {
		barType = readBuffer.readShort();
		price = readBuffer.readInt();
		name = readBuffer.readString();
		description = readBuffer.readString();
		if(description.length() > 96) this.description = description.substring(0, 95) + " ...";
		itemId = readBuffer.readShort();
		metaId = readBuffer.readShort();
		subType = readBuffer.readShort();
	}

	@Override
	public boolean equals(ExchangeItem exchangeItem) {
		return exchangeItem instanceof ItemExchangeItem && exchangeItem.name.equals(name) && exchangeItem.barType == barType && exchangeItem.price == price;
	}
}
