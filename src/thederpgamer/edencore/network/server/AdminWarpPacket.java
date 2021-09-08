package thederpgamer.edencore.network.server;

import api.common.GameClient;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.data.admin.AdminCommands;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/07/2021]
 */
public class AdminWarpPacket extends Packet {

    private Vector3i sector;

    public AdminWarpPacket() {

    }

    public AdminWarpPacket(Vector3i sector) {
        this.sector = sector;
    }

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        sector = packetReadBuffer.readVector();
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeVector(sector);
    }

    @Override
    public void processPacketOnClient() {
        GameClient.getClientState().getController().sendAdminCommand(AdminCommands.CHANGE_SECTOR, sector.x, sector.y, sector.z);
    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {

    }
}
