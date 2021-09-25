package thederpgamer.edencore.navigation;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.data.GameServerState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 25.09.2021
 * TIME: 12:35
 * sends mapmarkers to be drawn from server to client.
 */
public class NavigationMapPacket extends Packet {
    Collection<MapMarker> markers = new ArrayList<>();
    Collection<Long> publicMarkers = new ArrayList<>();

    public NavigationMapPacket(Collection<MapMarker> markers) {
        this.markers = markers;
    }

    public NavigationMapPacket() {} //required by starloader to instantiate upon receiving

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        int size = packetReadBuffer.readInt();
        for (int i = 0; i < size; i++) {
            markers.add(packetReadBuffer.readObject(MapMarker.class));
        }
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeInt(markers.size());
        for (MapMarker marker: markers) {
            packetWriteBuffer.writeObject(marker);
        }
    }

    @Override
    public void processPacketOnClient() {

        //write Markers to display on map
        for (MapMarker m: markers) {
            EdenMapDrawer drawer = EdenMapDrawer.instance;
            drawer.addMarker(m, true);
        }
        EdenMapDrawer.instance.updateInternalList();

    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {

    }

    public void sendToAllServer() {
        for (PlayerState p: GameServerState.instance.getPlayerStatesByName().values()) {
            PacketUtil.sendPacket(p,this);
        }

    }
}
