package thederpgamer.edencore.network.client.buildsector;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.utils.DataUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * Requests a player be added to the build sector.
 * [CLIENT] -> [SERVER]
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/29/2021]
 */
public class RequestBuildSectorInvitePacket extends Packet {

    private String playerName;

    public RequestBuildSectorInvitePacket() {

    }

    public RequestBuildSectorInvitePacket(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        playerName = packetReadBuffer.readString();
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeString(playerName);
    }

    @Override
    public void processPacketOnClient() {

    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {
        Objects.requireNonNull(DataUtils.getBuildSector(playerState.getName())).addPlayer(playerName);
        EdenCore.getInstance().updateClientCacheData();
    }
}
