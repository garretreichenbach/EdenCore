package thederpgamer.edencore.data.event;

import api.network.PacketReadBuffer;
import thederpgamer.edencore.data.SerializableData;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/05/2021]
 */
public abstract class EventTarget implements SerializableData {

    protected Object target;

    public EventTarget(Object target) {
        this.target = target;
    }

    public EventTarget(PacketReadBuffer readBuffer) throws IOException {
        deserialize(readBuffer);
    }

    public abstract int getProgress();
}
