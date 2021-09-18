package thederpgamer.edencore.data;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.SavedCoordinate;
import thederpgamer.edencore.manager.NavigationUtilManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 18.09.2021
 * TIME: 18:20
 */
public class NavigationUtilPacket extends Packet {
    private HashMap<Long, SavedCoordinate> coordsAddList = new HashMap<>();
    private HashMap<Long,SavedCoordinate> coordsRemoveList = new HashMap<>();

    public NavigationUtilPacket(HashMap<Long, SavedCoordinate> add,HashMap<Long, SavedCoordinate> remove) {
        this.coordsAddList = add;
        this.coordsRemoveList = remove;
    }

    public NavigationUtilPacket() {}

    @Override
    public void readPacketData(PacketReadBuffer buffer) throws IOException {
        int sizeAdd = buffer.readInt();
        int sizeRm = buffer.readInt();
        for (int i = 0; i < sizeAdd; i++) {
            SavedCoordinate c = buffer.readObject(SavedCoordinate.class);
            coordsAddList.put(c.getSector().code(),c);
        }
        for (int i = 0; i < sizeRm; i++) {
            SavedCoordinate c = buffer.readObject(SavedCoordinate.class);
            coordsRemoveList.put(c.getSector().code(),c);
        }
    }

    @Override
    public void writePacketData(PacketWriteBuffer buffer) throws IOException {
        buffer.writeInt(coordsAddList.size());
        buffer.writeInt(coordsRemoveList.size());
        for (SavedCoordinate c: coordsAddList.values()) {
            buffer.writeObject(c);
        }
        for (SavedCoordinate c: coordsRemoveList.values()) {
            buffer.writeObject(c);
        }
    }

    @Override
    public void processPacketOnClient() {
        if (NavigationUtilManager.instance==null)
            return;
        NavigationUtilManager.instance.setCoordsAddList(coordsAddList);
        NavigationUtilManager.instance.setCoordsRemoveList(coordsRemoveList);
        NavigationUtilManager.instance.updatePlayerList(GameClientState.instance.getPlayer());
    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {

    }
}
