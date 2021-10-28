package thederpgamer.edencore.network.client;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.network.server.SendCacheUpdatePacket;
import thederpgamer.edencore.utils.DataUtils;

import java.io.IOException;

/**
 * Sends a request to the server to update the client cache data.
 * <p>[CLIENT -> SERVER]</p>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/28/2021]
 */
public class RequestClientCacheUpdatePacket extends Packet {

    public RequestClientCacheUpdatePacket() {

    }

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {

    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {

    }

    @Override
    public void processPacketOnClient() {

    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {
        DataUtils.getBuildSector(playerState.getName());
        PacketUtil.sendPacket(playerState, new SendCacheUpdatePacket());
    }
}
