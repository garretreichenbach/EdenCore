package thederpgamer.edencore.data.event.target;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import thederpgamer.edencore.data.event.EventTarget;
import thederpgamer.edencore.utils.ServerUtils;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/19/2021]
 */
public class DestroyTarget extends EventTarget {

    public DestroyTarget(String targetEntityUID) {
        super(targetEntityUID);
    }

    public DestroyTarget(PacketReadBuffer readBuffer) throws IOException {
        super(readBuffer);
    }

    @Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {
        target = readBuffer.readString();
    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
        writeBuffer.writeString((String) target);
    }

    @Override
    public int getProgress() {
        SegmentController entity = ServerUtils.getEntityFromUID((String) target);
        if(entity instanceof Ship) {
            return (int) ((int) ((Ship) entity).getReactorHp() / ((Ship) entity).getReactorHpMax());
        } else if(entity instanceof SpaceStation) {
            return (int) ((int) ((SpaceStation) entity).getReactorHp() / ((SpaceStation) entity).getReactorHpMax());
        } else return 0;
    }
}
