package thederpgamer.edencore.network.client;

import api.common.GameCommon;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.element.ElementDocking;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.network.objects.Sendable;
import thederpgamer.edencore.utils.DataUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [02/20/2022]
 */
public class RequestEntityDeletePacket extends Packet {

    private int entityId;

    public RequestEntityDeletePacket() {

    }

    public RequestEntityDeletePacket(SegmentController segmentController) {
        this.entityId = segmentController.getId();
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

    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {
        Sendable sendable = GameCommon.getGameObject(entityId);
        if(sendable instanceof SegmentController) {
            SegmentController segmentController = (SegmentController) sendable;
            if(Objects.requireNonNull(DataUtils.getPlayerCurrentBuildSector(playerState)).sector.equals(segmentController.getSector(new Vector3i()))) {
                segmentController.railController.destroyDockedRecursive();
                for(ElementDocking dock : segmentController.getDockingController().getDockedOnThis()) {
                    dock.from.getSegment().getSegmentController().markForPermanentDelete(true);
                    dock.from.getSegment().getSegmentController().setMarkedForDeleteVolatile(true);
                }
                segmentController.markForPermanentDelete(true);
                segmentController.setMarkedForDeleteVolatile(true);
            }
        }
    }
}
