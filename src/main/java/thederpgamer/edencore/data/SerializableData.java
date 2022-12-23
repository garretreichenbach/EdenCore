package thederpgamer.edencore.data;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;

import java.io.IOException;

/**
 * Interface for serializable persistent data.
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/29/2021]
 */
public interface SerializableData {

    void deserialize(PacketReadBuffer readBuffer) throws IOException;
    void serialize(PacketWriteBuffer writeBuffer) throws IOException;
    void updateClients();
}
