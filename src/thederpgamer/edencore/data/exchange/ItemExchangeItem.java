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
 * @version 1.0 - [11/24/2021]
 */
public class ItemExchangeItem extends ExchangeItem {

    public short itemId;
    public short metaId;
    public short subType;

    public ItemExchangeItem(PacketReadBuffer readBuffer) {
        super(readBuffer);
    }

    public ItemExchangeItem(short barType, int price, String name, String description, short itemId, short metaId, short subType) {
        super(barType, price, name, description);
        this.itemId = itemId;
        this.metaId = metaId;
        this.subType = subType;
    }

    @Override
    public GUIOverlay getIcon() {
        return IconDatabase.getBuildIconsInstance(GameClient.getClientState(), ElementKeyMap.getInfo(itemId).getBuildIconNum() + GameClient.getClientState().getMetaObjectManager().getObject(metaId).getExtraBuildIconIndex());
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
