package thederpgamer.edencore.data.event;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import thederpgamer.edencore.data.SerializableData;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/26/2021]
 */
public class SquadMemberData implements SerializableData {

    public String playerName;
    public String shipName;
    public int factionId;
    public double shipMass;
    public String selectedShipName;
    public boolean ready;

    @Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {

    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {

    }
}
