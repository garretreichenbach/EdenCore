package thederpgamer.edencore.data.exchange;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.forms.gui.GUIOverlay;
import thederpgamer.edencore.manager.LogManager;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public abstract class ExchangeItem {

    public short barType;
    public int price;
    public String name;
    public String description;

    public ExchangeItem(PacketReadBuffer readBuffer) {
        try {
            deserialize(readBuffer);
        } catch(IOException exception) {
            LogManager.logException("Encountered an exception while trying to deserialize exchange item data", exception);
        }
    }

    public ExchangeItem(short barType, int price, String name, String description) {
        this.barType = barType;
        this.price = price;
        this.name = name;
        this.description = description;
        if(description.length() > 96) this.description = description.substring(0, 95) + " ...";
    }

    public String createDescription() {
        return name + "\n" + price + " " + ElementKeyMap.getInfo(barType).getName() + "s\n" + description;
    }

    public abstract GUIOverlay getIcon();
    public abstract void serialize(PacketWriteBuffer writeBuffer) throws IOException;
    public abstract void deserialize(PacketReadBuffer readBuffer) throws IOException;
    public abstract boolean equals(ExchangeItem exchangeItem);
}
