package thederpgamer.edencore.data.exchange;

import api.common.GameClient;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.forms.gui.GUIOverlay;
import org.schema.schine.graphicsengine.forms.gui.IconDatabase;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class ResourceExchangeItem extends ExchangeItem {

    public short itemId;
    public int itemCount;

    public ResourceExchangeItem(PacketReadBuffer readBuffer) {
        super(readBuffer);
    }

    public ResourceExchangeItem(short barType, int price, String name, String description, short itemId, int itemCount) {
        super(barType, price, name, description);
        this.itemId = itemId;
        this.itemCount = itemCount;
    }

    @Override
    public GUIOverlay getIcon() {
        return IconDatabase.getBuildIconsInstance(GameClient.getClientState(), ElementKeyMap.getInfo(itemId).getBuildIconNum());
    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
        writeBuffer.writeShort(barType);
        writeBuffer.writeInt(price);
        writeBuffer.writeString(name);
        writeBuffer.writeString(description);
        writeBuffer.writeShort(itemId);
        writeBuffer.writeInt(itemCount);
    }

    @Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {
        barType = readBuffer.readShort();
        price = readBuffer.readInt();
        name = readBuffer.readString();
        description = readBuffer.readString();
        itemId = readBuffer.readShort();
        itemCount = readBuffer.readInt();
    }

    @Override
    public boolean equals(ExchangeItem exchangeItem) {
        return exchangeItem instanceof ResourceExchangeItem && exchangeItem.name.equals(name) && exchangeItem.barType == barType && exchangeItem.price == price;
    }
}
