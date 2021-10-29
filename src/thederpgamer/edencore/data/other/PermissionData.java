package thederpgamer.edencore.data.other;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import thederpgamer.edencore.data.SerializableData;

import java.io.IOException;
import java.util.HashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/29/2021]
 */
public class PermissionData implements SerializableData {

    public String playerName;
    public HashMap<String, Boolean> permissions;

    public PermissionData(String playerName, HashMap<String, Boolean> permissions) {
        this.playerName = playerName;
        this.permissions = permissions;
    }

    public PermissionData(PacketReadBuffer readBuffer) throws IOException {
        deserialize(readBuffer);
    }

    @Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {
        playerName = readBuffer.readString();
        if(permissions == null) permissions = new HashMap<>();
        permissions = readBuffer.readObject(permissions.getClass());
    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
        writeBuffer.writeString(playerName);
        writeBuffer.writeObject(permissions);
    }
}
