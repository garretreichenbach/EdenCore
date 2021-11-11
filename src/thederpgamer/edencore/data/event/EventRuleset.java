package thederpgamer.edencore.data.event;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import thederpgamer.edencore.data.SerializableData;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/05/2021]
 */
public class EventRuleset implements SerializableData {

    public EventRuleset(PacketReadBuffer readBuffer) throws IOException {
        deserialize(readBuffer);
    }

    @Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {

    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {

    }
}
