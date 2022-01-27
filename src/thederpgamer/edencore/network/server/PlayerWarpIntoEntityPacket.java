package thederpgamer.edencore.network.server;

import api.common.GameClient;
import api.common.GameCommon;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.network.objects.Sendable;
import thederpgamer.edencore.utils.EntityUtils;

import java.io.IOException;

/**
 * <Description>
 * [SERVER] -> [CLIENT]
 *
 * @author TheDerpGamer
 * @version 1.0 - [01/27/2022]
 */
public class PlayerWarpIntoEntityPacket extends Packet {

    private int entityId;

    public PlayerWarpIntoEntityPacket() {

    }

    public PlayerWarpIntoEntityPacket(SegmentController entity) {
        this.entityId = entity.getId();
    }

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        entityId = packetReadBuffer.readInt();
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeInt(entityId);
    }

    @Override
    public void processPacketOnClient() {
        Sendable sendable = GameCommon.getGameObject(entityId);
        if(sendable instanceof SegmentController) EntityUtils.warpPlayerIntoEntity(GameClient.getClientPlayerState(), (SegmentController) sendable);
    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {

    }
}
